package com.cafa.pdf.core.commom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AuthorDTO implements java.io.Serializable {

    private static final long serialVersionUID = 7774935507446252392L;
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
     * 登录账号
     */
    private String username;
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
    private String activeStr;
    /**
     * 出生年、籍贯、学历、单位、职称、职务
     */
    private Integer birthYear;
    private String birthPlace;
    private String education;
    private String company;
    private String job;
    private String duties;
    /**
     * 注册时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdTime;


}
