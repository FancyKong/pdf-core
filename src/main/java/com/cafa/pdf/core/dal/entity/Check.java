package com.cafa.pdf.core.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/4/26 8:22
 */
@Entity
@Table(name = "t_check")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Check implements java.io.Serializable {

    private static final long serialVersionUID = -380458929081530006L;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    /**
     * 随机键
     */
    @Column(name = "random_key", nullable = false)
    private String randomKey;
    /**
     * 0:会员 或 1:著作者 2:管理员
     */
    @Column(name = "active_type", nullable = false)
    private Integer activeType;
    /**
     * 要激活的ID
     */
    @Column(name = "active_id", nullable = false)
    private Long activeId;


}
