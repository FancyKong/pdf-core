/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.commom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author FancyKong
 * @file Language.java
 * @date 2017/4/23 23:33
 * @since 0.0.1
 */
@AllArgsConstructor
@Getter
public enum Language {
    CHN("CHN", "中文"),
    ENG("ENG", "英文"),
    OTH("OTH", "其他");

    private String code;

    private String desc;
}
