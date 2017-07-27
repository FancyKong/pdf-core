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
import java.util.List;

/**
 * @author FancyKong
 * @file TreatiseDTO.java
 * @date 2017/4/14 11:05
 * @since 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatisePortalDTO implements java.io.Serializable {

    private static final long serialVersionUID = 5687645651510183078L;
    private Long id;
    /**
     * ISBN编号
     */
    private String ISBN;
    /**
     * 编号
     */
    private String no;
    /**
     * 书名
     */
    private String bookName;
    /**
     * 作者
     */
    private String author;
    /**
     * 简介
     */
    private String description;
    /**
     * 类别
     */
    private String category;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate;
    /**
     * 总页数,单位:页
     */
    private Integer pageNumber;
    /**
     * 字数,单位:千字
     */
    private Integer words;
    /**
     * 定价,单位:分
     */
    private Integer price;
    /**
     * 序
     */
    private List<String> introductory;
    /**
     * 前言
     */
    private String preface;
    /**
     * 绪言
     */
    private String exordium;
    /**
     * 书评
     */
    private List<String> review;
    /**
     * 跋
     */
    private String postscript;
    /**
     * 章外后记
     */
    private String appendix;
    /**
     * 印次 如:2015年12月第2次印刷
     */
    private String impression;
    /**
     * 版次 如:2015年12月第5版
     */
    private String edition;
    /**
     * 开本 如:787X980 1/16
     */
    private String bookSize;
    /**
     * 关键词
     */
    private String keywords;
    /**
     * 参考文献
     */
    private String referenceBooks;
    /**
     * 推荐书
     */
    private String recommends;
    /**
     * 系列丛书
     */
    private String series;
    /**
     * 相关著作
     */
    private String relatedBooks;
    /**
     * 出版形式
     * @see com.cafa.pdf.core.commom.enums.PublicationMode
     */
    private String publicationMode;
    /**
     * 语种
     * @see com.cafa.pdf.core.commom.enums.Language
     */
    private String language;
    /**
     * 点击量
     */
    private Long hits;
    /**
     * 阅读量
     */
    private Long reading;
}
