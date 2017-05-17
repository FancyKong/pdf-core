package com.cafa.pdf.core.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 系统配置
 * @author Cherish
 * @version 1.0
 * @date 2017/5/17 14:50
 */
@Entity
@Table(name = "t_sys_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysConfig implements java.io.Serializable {

    private static final long serialVersionUID = 2549536170923433451L;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    /**
     * 关键字
     */
    @Column(name = "keyword", nullable = false)
    private String keyword;
    /**
     * 字符串值
     */
    @Column(name = "str_value")
    private String strValue;
    /**
     * 整数值
     */
    @Column(name = "int_value")
    private Integer intValue;
    /**
     * 长整数值
     */
    @Column(name = "long_value")
    private Long longValue;
    /**
     * 备注
     */
    @Column(name = "description")
    private String description;

}
