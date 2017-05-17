/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.solr.document;

import lombok.*;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * @author FancyKong
 * @file ChapterSolrDoc.java
 * @date 2017/4/20 9:26
 * @since 0.0.1
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SolrDocument(solrCoreName = "chapter")
@ToString
public class ChapterSolrDoc {

    @Id
    @Field
    private String id;

    @Field
    private String content;

    @Field
    private String treatiseId;

    @Field
    private Integer seq;
}
