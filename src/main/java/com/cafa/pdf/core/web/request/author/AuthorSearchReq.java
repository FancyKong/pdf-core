package com.cafa.pdf.core.web.request.author;

import lombok.Data;

@Data
public class AuthorSearchReq implements java.io.Serializable {

    private static final long serialVersionUID = -2805222349329475945L;
    private String nickname;

    private String telephone;

    private Integer active;


}
