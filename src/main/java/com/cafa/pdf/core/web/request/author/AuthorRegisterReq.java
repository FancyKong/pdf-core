package com.cafa.pdf.core.web.request.author;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRegisterReq implements java.io.Serializable {

    private static final long serialVersionUID = -5744420808807057786L;
    /*姓名、性别、出生年、电话、籍贯、单位、学历、职称、职务、邮箱*/
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
    private String password;
    private String repeatPwd;
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
