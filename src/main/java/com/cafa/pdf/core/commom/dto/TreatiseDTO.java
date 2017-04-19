/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.commom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author FancyKong
 * @file TreatiseDTO.java
 * @date 2017/4/14 11:05
 * @since 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatiseDTO implements java.io.Serializable {

    private static final long serialVersionUID = 5687645651510183078L;
    private Long id;
    /**
     * ISBN编号
     */
    private String ISBN;
    /**
     * 书名
     */
    private String bookName;
    /**
     * 作者
     */
    private String author;
    /**
     * 类别
     */
    private Long category;
    /**
     * 出版社
     */
    private String publishHouse;
    /**
     * 出版地
     */
    private String publishPlace;
    /**
     * 出版日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GTM+8")
    private Date publishDate;
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 语种
     */
    private String language;
    /**
     * 版次
     */
    private String revision;
    /**
     * 简介？
     */
    private String description;

}
