package com.cafa.pdf.core.web.request.statistics;

import lombok.Data;

@Data
public class HitsSearchReq implements java.io.Serializable {

    private static final long serialVersionUID = 3486329082359799309L;
    private String ISBN;
    private String bookName;
    private String keyword;


}
