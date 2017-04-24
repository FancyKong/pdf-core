/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.commom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author FancyKong
 * @file PublicationMode.java
 * @date 2017/4/23 23:29
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
public enum PublicationMode {
    DC("DC", "单册"),
    SC("SC", "上册"),
    ZC("ZC", "中册"),
    XC("XC", "下册");

    private String code;

    private String desc;
}
