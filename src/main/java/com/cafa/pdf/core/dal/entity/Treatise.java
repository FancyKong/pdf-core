/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.entity;

import com.cafa.pdf.core.commom.enums.Language;
import com.cafa.pdf.core.commom.enums.PublicationMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author FancyKong
 * @file Treatise.java
 * @date 2017/4/13 13:35
 * @since 0.0.1
 */
@Entity
@Table(name = "t_treatise")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Treatise implements java.io.Serializable {

    private static final long serialVersionUID = 2855297224736248527L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    /**
     * 总页数,单位:页
     */
    @Column(name="page_number",nullable = false)
    private int pageNumber = 0;
    /**
     * 字数,单位:千字
     */
    @Column(nullable = false)
    private int words = 0;
    /**
     * 定价,单位:分
     */
    @Column(nullable = false)
    private int price = 0;
    /**
     * 出版日期
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "publish_date", nullable = false)
    private Date publishDate;
    /**
     * 编号
     */
    @Column(name = "ISBN", nullable = false , length = 40)
    private String no;
    /**
     * ISBN编号
     */
    @Column(name = "ISBN", nullable = false , length = 40)
    private String ISBN;
    /**
     * 书名
     */
    @Column(name = "book_name", nullable = false,length = 40)
    private String bookName;
    /**
     * 简介
     */
    @Column(name = "description", nullable = false)
    private String description;
    /**
     * 出版社
     */
    @Column(name = "publish_house", nullable = false ,length = 40)
    private String publishHouse = "";
    /**
     * 出版地
     */
    @Column(name = "publish_place", nullable = false,length = 40)
    private String publishPlace = "";
    /**
     * 序
     */
    @Column(nullable = false)
    private String introductory = "";
    /**
     * 前言
     */
    @Column(nullable = false)
    private String preface = "";
    /**
     * 绪言
     */
    @Column(nullable = false)
    private String exordium = "";
    /**
     * 书评
     */
    @Column(nullable = false)
    private String review = "";
    /**
     * 跋
     */
    @Column(nullable = false)
    private String postscript = "";
    /**
     * 章外后记
     */
    @Column(nullable = false)
    private String appendix = "";
    /**
     * 印次 如:2015年12月第2次印刷
     */
    @Column(nullable = false, length = 40)
    private String impression = "";
    /**
     * 版次 如:2015年12月第5版
     */
    @Column(nullable = false, length = 40)
    private String edition = "";
    /**
     * 开本 如:787X980 1/16
     */
    @Column(nullable = false, length = 40)
    private String bookSize = "";
    /**
     * 关键词
     */
    @Column(nullable = false)
    private String keywords;
    /**
     * 参考文献
     */
    @Column(nullable = false)
    private String referenceBooks = "";
    /**
     * 推荐书
     */
    @Column(nullable = false)
    private String recommends = "";
    /**
     * 系列丛书
     */
    @Column(nullable = false)
    private String series = "";
    /**
     * 相关著作
     */
    @Column(nullable = false)
    private String relatedBooks = "";
    /**
     * 分类
     */
    @Column(nullable = false , name = "category_id")
    private Long categoryId;
    /**
     * 作者
     */
    @Column(nullable = false , name = "author_id")
    private Long authorId;
    /**
     * 出版形式
     * @see com.cafa.pdf.core.commom.enums.PublicationMode
     */
    @Column(nullable = false)
    private String publicationMode = PublicationMode.DC.getCode();
    /**
     * 语种
     * @see com.cafa.pdf.core.commom.enums.Language
     */
    @Column(nullable = false)
    private String language = Language.CHN.getCode();


}
