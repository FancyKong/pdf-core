package com.cafa.pdf.core.dal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_customer")
public class Customer implements java.io.Serializable {

	private static final long serialVersionUID = 2285174464789310329L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
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
     * 密码
     */
    @JsonIgnore
    private String password;
    /**
     * 性别
     */
    private String gender;
    /**
     * -1: 申请状态 0：冻结 1：激活
     */
    @Column(name = "is_active", nullable = false)
    private Integer active;
    /**
     * 研究领域
     */
    @Column(name = "study", nullable = false)
    private String study;
    /**
     * 出生年
     */
    @Column(name = "birth_year")
    private Integer birthYear;
    /**
     * 籍贯
     */
    @Column(name = "birth_place")
    private String birthPlace;
    /**
     * 学历
     * @see com.cafa.pdf.core.commom.enums.Education
     */
    private String education;
    /**
     * 单位
     */
    private String company;
    /**
     * 职称
     */
    private String job;
    /**
     * 职务
     */
    private String duties;
    /**
     * 申请时的ip
     */
    private Integer ip;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", nullable = false, length = 19)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_time", nullable = false, length = 19)
    private Date modifiedTime;

}
