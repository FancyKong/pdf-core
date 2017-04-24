/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.commom.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author FancyKong
 * @file Education.java
 * @date 2017/4/24 0:08
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
public enum  Education {
    DOCTOR("DOCTOR", "博士"),
    MASTER("MASTER", "硕士"),
    BACHELOR("BACHELOR", "学士"),
    OTHER("OTHER", "其他");

    private String code;

    private String desc;
}
