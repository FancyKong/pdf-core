package com.cafa.pdf.core.commom.dto;

import lombok.Data;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/5/29 9:03
 */
@Data
public class ReadingDTO implements java.io.Serializable{

    private static final long serialVersionUID = -8594014640996112557L;
    private Long id;
    private String ISBN;
    private String bookName;
    private Long count;

}
