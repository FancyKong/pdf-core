/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.web.request.treatise;

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
public class TreatiseSaveReq implements java.io.Serializable {

    private static final long serialVersionUID = -4815718221777847521L;

    private String ISBN;
    private String bookName;
    private String author;
    private String category;
    private String publishHouse;
    private String publishPlace;
    @DateTimeFormat(pattern="yyyy-MM-dd",iso = DateTimeFormat.ISO.DATE)
    private Date publishDate;
    private Integer pageNum;
    private String language;
    private String revision;
    private String description;

}
