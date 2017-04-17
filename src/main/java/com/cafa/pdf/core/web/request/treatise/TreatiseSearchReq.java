package com.cafa.pdf.core.web.request.treatise;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TreatiseSearchReq implements java.io.Serializable {

    private static final long serialVersionUID = -5113264073253603846L;

    private String ISBN;
    private String bookName;
    private String author;
    private Long category;
    private String publishHouse;
    private String publishPlace;
    @DateTimeFormat(pattern="yyyy-MM-dd",iso = DateTimeFormat.ISO.DATE)
    private Date publishDate;
    private Integer pageNum;
    private String language;
    private String revision;
    private String description;


}
