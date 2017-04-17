package com.cafa.pdf.core.web.request.user;

import lombok.Data;

@Data
public class UserSearchReq implements java.io.Serializable {

    private String username;

    private String nickname;

    private String telephone;

    private String position;

    private Integer active;


}
