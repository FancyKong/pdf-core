/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.commom.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author FancyKong
 * @file AdvanceForm.java
 * @date 2017/7/24 21:27
 * @since 0.0.1
 */
@Getter
@Setter
public class AdvanceForm {
    private String language;
    private String bookName;
    private String author;
    private String publishHouse;
    private Long category;
    private Integer publishYear;
}
