package com.jwzhoug.stream.chap3;

import java.io.*;
import java.net.URLDecoder;

/**
 * 注意使用文件读取新方式，可以不用去关闭文件流
 *
 * @author: zhoujw
 * @Date: 2019-04-25
 */
public class ExecuteAround {

    public ExecuteAround() throws UnsupportedEncodingException {
    }

    public static void main(String[] args) throws IOException {

        String result = processFileLimit();
        System.out.println(result);


        String oneLine = processFile(br -> br.readLine());
        System.out.println(oneLine);

        String twoLines = processFile(br -> br.readLine() + br.readLine());
        System.out.println(twoLines);


    }

    public static String filePath;

    static {
        try {
            filePath = URLDecoder.decode(ExecuteAround.class.getClassLoader().getResource("").getFile(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static String processFileLimit() throws IOException {
        try (BufferedReader br = new BufferedReader(
                new FileReader(filePath + "lambda/chap3/data.txt"))) {
            return br.readLine();
        }
    }

    public static String processFile(BufferedReaderProcessor bp) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new FileReader(filePath + "lambda/chap3/data.txt"))) {
            return bp.process(br);
        }
    }

    public interface BufferedReaderProcessor {
        String process(BufferedReader br) throws IOException;
    }
}
