package com.cafa.pdf.core.web.request.statistics;

import lombok.Data;

@Data
public class ReadingSearchReq implements java.io.Serializable {

    private static final long serialVersionUID = 8695355009884639910L;
    private String ISBN;
    private String bookName;
    private String keyword;


}
