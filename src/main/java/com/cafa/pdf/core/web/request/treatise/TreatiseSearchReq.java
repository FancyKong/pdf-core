package com.cafa.pdf.core.web.request.treatise;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TreatiseSearchReq implements java.io.Serializable {

    private long id;
    private String ISBN;
    private String author;
    private String category;
    private String publishHouse;
    private String publishPlace;
    @DateTimeFormat(pattern="yyyy-MM-dd",iso = DateTimeFormat.ISO.DATE)
    private Date publishDate;
    private int pageNum;
    private String language;
    private int revision;
    private String description;


}
