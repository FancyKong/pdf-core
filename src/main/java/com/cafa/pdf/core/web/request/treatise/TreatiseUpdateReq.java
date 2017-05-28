/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web.request.treatise;

import com.cafa.pdf.core.commom.enums.Language;
import com.cafa.pdf.core.commom.enums.PublicationMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author FancyKong
 * @file Treatise.java
 * @date 2017/4/13 13:35
 * @since 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatiseUpdateReq implements java.io.Serializable {

    private static final long serialVersionUID = 624547075791028856L;

    private Long id;
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
     * 出版日期
     */
    @DateTimeFormat(pattern="yyyy-MM-dd",iso = DateTimeFormat.ISO.DATE)
    private Date publishDate;
    /**
     * 编号
     */
    private String no;
    /**
     * ISBN编号
     */
    private String ISBN;
    /**
     * 书名
     */
    private String bookName;
    /**
     * 简介
     */
    private String description;
    /**
     * 出版社
     */
    private String publishHouse;
    /**
     * 出版地
     */
    private String publishPlace;
    /**
     * 序
     */
    private String introductory;
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
    private String review;
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
     * 分类
     */
    private Long categoryId;
    /**
     * 作者
     */
    private Long authorId;
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

}
