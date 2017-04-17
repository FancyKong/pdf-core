package com.cafa.pdf.core.web.request.user;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class UserSearchReq implements java.io.Serializable {

    private String username;

    private String nickname;

    private String telephone;

    private String position;

    private Integer active;


}
