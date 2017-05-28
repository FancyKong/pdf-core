/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author FancyKong
 * @file TreatiseHits.java
 * @date 2017/4/13 13:35
 * @since 0.0.1
 */
@Entity
@Table(name = "t_treatise_hits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatiseHits implements java.io.Serializable {

    private static final long serialVersionUID = 2855297224736248527L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id; //指向著作的id

    private String bookName;

    private Long count;
}
