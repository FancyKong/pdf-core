package com.cafa.pdf.core.commom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AuthorDTO implements java.io.Serializable {

    private static final long serialVersionUID = 7774935507446252392L;
    private Long id;

    private String password;

    private String nickname;

    private String email;

    private String telephone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="UTC")
    private Date createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="UTC")
    private Date modifiedTime;

    private String activeStr;


}
