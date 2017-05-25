/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.commom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author FancyKong
 * @file TreatiseShowDTO.java
 * @date 2017/5/23 21:49
 * @since 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatiseShowDTO implements java.io.Serializable {
    /**
     * ID
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 分类名字
     */
    private String categoryName;
    /**
     * 高亮文本
     */
    private String highlighted;
    /**
     * 作者名字
     */
    private String author;
    /**
     * 出版日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GTM+8")
    private Date publishDate;
}
