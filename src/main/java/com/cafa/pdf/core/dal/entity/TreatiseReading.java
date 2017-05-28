/**
 * JDKCC.com
 * Copyright (c) 2011-2017 All Rights Reserved.
 */
package com.cafa.pdf.core.dal.entity;

import com.cafa.pdf.core.commom.enums.Language;
import com.cafa.pdf.core.commom.enums.PublicationMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author FancyKong
 * @file TreatiseReading.java
 * @date 2017/4/13 13:35
 * @since 0.0.1
 */
@Entity
@Table(name = "t_treatise_reading")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatiseReading implements java.io.Serializable {

    private static final long serialVersionUID = 2855297224736248527L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    private String bookName;

    private Long count;
}
