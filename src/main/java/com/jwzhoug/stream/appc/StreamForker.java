package com.jwzhoug.stream.appc;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-25
 */
public class StreamForker<T> {

    private final Stream<T> stream;
    private final Map<Object, Function<Stream<T>, ?>> forks = new HashMap<>();

    public StreamForker(Stream<T> stream) {
        this.stream = stream;
    }

    public StreamForker<T> fork(Object key, Function<Stream<T>, ?> f) {
        forks.put(key, f);
        return this;
    }

    public Results getResults() {

        ForkingStreamConsumer<T> consumer = build();
        try {
            stream.sequential().forEach(consumer);
        } finally {
            consumer.finish();
        }

        return consumer;
    }


    public static interface Results {
        public <R> R get(Object key);
    }

    private static class ForkingStreamConsumer<T> implements Consumer<T>, Results {

        static final Object END_OF_STREAM = new Object();

        private final List<BlockingQueue<T>> queues;
        private final Map<Object, Future<?>> actions;

        public ForkingStreamConsumer(List<BlockingQueue<T>> queues, Map<Object, Future<?>> actions) {
            this.queues = queues;
            this.actions = actions;
        }

        @Override
        public <R> R get(Object key) {

            try {
                return ((Future<R>) actions.get(key)).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void accept(T t) {
            queues.forEach(q -> q.add(t));
        }

        void finish() {
            accept((T) END_OF_STREAM);
        }
    }

    private ForkingStreamConsumer<T> build() {

        List<BlockingQueue<T>> queues = new ArrayList<>();

        Map<Object, Future<?>> actions =
                forks.entrySet().stream().reduce(
                        new HashMap<Object, Future<?>>(),
                        (map, e) -> {
                            map.put(e.getKey(),
                                    getOperationResult(queues, e.getValue()));
                            return map;
                        },
                        (m1, m2) -> {
                            m1.putAll(m2);
                            return m2;
                        });

        return new ForkingStreamConsumer<>(queues, actions);

    }


    private Future<?> getOperationResult(List<BlockingQueue<T>> queues, Function<Stream<T>, ?> f) {

        BlockingQueue<T> queue = new LinkedBlockingQueue<>();
        queues.add(queue);
        Spliterator<T> spliterator = new BlockingQueueSpliterator<>(queue);
        Stream<T> source = StreamSupport.stream(spliterator, false);
        return CompletableFuture.supplyAsync(() -> f.apply(source));
    }


    private static class BlockingQueueSpliterator<T> implements Spliterator<T> {

        private final BlockingQueue<T> q;

        public BlockingQueueSpliterator(BlockingQueue<T> q) {
            this.q = q;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            T t;
            while (true) {
                try {
                    t = q.take();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 队列任务未结束
            if (t != ForkingStreamConsumer.END_OF_STREAM) {
                action.accept(t);
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }
    }
}
