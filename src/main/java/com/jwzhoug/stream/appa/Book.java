package com.jwzhoug.stream.appa;

import java.util.Arrays;

/**
 * TODO...
 *
 * @author: zhoujw
 * @Date: 2019-04-25
 */
@Author(name = "Tom")
@Author(name = "Jack")
@Author(name = "Alan")
public class Book {

    public static void main(String[] args) {

        Author[] authors = Book.class.getAnnotationsByType(Author.class);
        Arrays.asList(authors).stream().forEach(author -> System.out.println(author.name()));
    }

}
