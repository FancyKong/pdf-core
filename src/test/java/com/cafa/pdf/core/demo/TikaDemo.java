/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.demo;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;

/**
 * @author FancyKong
 * @file TikaDemo.java
 * @date 2017/3/25 21:18
 * @since 0.0.1
 */
public class TikaDemo {
    public static void main(String[] args) throws IOException, TikaException {
        Tika tika = new Tika();
        System.out.println(tika.parseToString(new File("E:c.pdf")));
    }
}
