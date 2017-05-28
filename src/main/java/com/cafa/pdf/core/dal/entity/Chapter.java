package com.cafa.pdf.core.dal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 章节实体类
 * @author Cherish
 * @version 1.0
 * @date 2017/4/18 23:25
 */
@Entity
@Table(name = "t_chapter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapter implements java.io.Serializable {

    private static final long serialVersionUID = -6226784708025343572L;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    /**
     * 著作ID
     */
    @Column(name = "treatise_id", nullable = false)
    private Long treatiseId;
    /**
     * 自定义排序，第几章
     */
    @Column(name = "seq", nullable = false)
    private Integer seq;
    /**
     * 章标题
     */
    @Column(name = "title", nullable = false)
    private String title;
    /**
     * 是否被加密
     */
    @Column(name = "is_privacy", nullable = false)
    private Integer privacy;
    /**
     * 是否被加密
     */
    @Column(name = "page", nullable = false)
    private Integer page;


}
