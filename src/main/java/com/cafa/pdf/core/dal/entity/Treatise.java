/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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
public class Treatise {
    //ISBN编号,书名,类别,作者,出版社,出版地,出版日期,页码,语种,版次,目录,简介
    //临时就这些. for prototype . 并不规范
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private String ISBN;
    private String author;
    private String category;
    private String publishHouse;
    private String publishPlace;
    private Date publishDate;
    private int pageNum;
    private String language;
    private int revision;
    private String description;

}
