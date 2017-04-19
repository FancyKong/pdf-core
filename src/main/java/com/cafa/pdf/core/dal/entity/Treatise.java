/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.entity;

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
    //ISBN编号,书名,类别,作者,出版社,出版地,出版日期,页码,语种,版次,目录,简介
    //临时就这些. for prototype . 并不规范
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    /**
     * ISBN编号
     */
    @Column(name = "ISBN", nullable = false)
    private String ISBN;
    /**
     * 书名
     */
    @Column(name = "book_name", nullable = false)
    private String bookName;
    /**
     * 作者
     */
    @Column(name = "author", nullable = false)
    private String author;
    /**
     * 类别
     */
    @Column(name = "category", nullable = false)
    private Long category;
    /**
     * 出版社
     */
    @Column(name = "publish_house", nullable = false)
    private String publishHouse;
    /**
     * 出版地
     */
    @Column(name = "publish_place", nullable = false)
    private String publishPlace;
    /**
     * 出版日期
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "publish_date", nullable = false)
    private Date publishDate;
    /**
     * 页码
     */
    @Column(name = "page_num", nullable = false)
    private Integer pageNum;
    /**
     * 语种
     */
    @Column(name = "language", nullable = false)
    private String language;
    /**
     * 版次
     */
    @Column(name = "revision", nullable = false)
    private String revision;
    /**
     * 简介？
     */
    @Column(name = "description", nullable = false)
    private String description;

}
