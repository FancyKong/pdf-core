/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.solr.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Date;

/**
 * @author FancyKong
 * @file TreatiseSolrDoc.java
 * @date 2017/4/20 9:26
 * @since 0.0.1
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SolrDocument(solrCoreName = "treatise")
public class TreatiseSolrDoc {

    @Id
    @Field
    private String id;

    @Field
    private String content;

    @Field
    private String title;

    @Field
    private Date publishDate;

    @Field
    private String author;

    @Field
    private String categoryName;

    @Field
    private Long categoryId;

    @Field
    private Long pCategoryId;

    @Field
    private String description;

    @Field
    private String publishHouse;

    @Field
    private String language;

}
