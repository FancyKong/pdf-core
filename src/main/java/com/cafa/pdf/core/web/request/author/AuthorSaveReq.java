package com.cafa.pdf.core.web.request.author;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class AuthorSaveReq implements java.io.Serializable {

    private static final long serialVersionUID = 2314832395004869236L;
    /**
     * 姓名
     */
    private String nickname;
    /**
     * 手机
     */
    @Pattern(regexp = "^[1][34578][0-9]{9}$", message = "请输入正确的手机号码")
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
     * 密码
     */
    @Pattern(regexp="[0-9A-Za-z]{6,16}$", message="密码必须是6~16位字母和数字的组合")
    private String password;
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
