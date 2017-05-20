package com.cafa.pdf.core.web.request.author;

import lombok.Data;

@Data
public class AuthorUpdateReq implements java.io.Serializable {

    private static final long serialVersionUID = -4468922410807772451L;

    private Long id;
    /**
     * 姓名
     */
    private String nickname;
    /**
     * 手机
     */
    private String telephone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 性别
     */
    private String gender;
    /**
     * -1: 申请状态
     * 0：冻结
     * 1：激活
     */
    private Integer active;
    /**
     * 出生年、籍贯、学历、单位、职称、职务
     */
    private Integer birthYear;
    private String birthPlace;
    private String education;
    private String company;
    private String job;
    private String duties;


}
