package com.cafa.pdf.core.dal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "t_author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author implements java.io.Serializable {

    private static final long serialVersionUID = -2352745039383411872L;
    /*姓名、性别、出生年、电话、籍贯、单位、学历、职称、职务、邮箱*/
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
     * 密码
     */
    @JsonIgnore
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
    @Column(name = "is_active", nullable = false)
    private Integer active;
    /**
     * 出生年、籍贯、学历、单位、职称、职务
     */
    @Column(name = "birth_year")
    private String birthYear;
    @Column(name = "birth_place")
    private String birthPlace;
    private String education;
    private String company;
    private String job;
    private String duties;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", nullable = false, length = 19)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_time", nullable = false, length = 19)
    private Date modifiedTime;


}
