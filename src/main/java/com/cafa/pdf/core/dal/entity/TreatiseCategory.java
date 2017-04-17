package com.cafa.pdf.core.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "t_treatise_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "treatise_category")
public class TreatiseCategory implements java.io.Serializable {

	private static final long serialVersionUID = -5196023570569388721L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "name", nullable = false, length = 16)
	private String name;

	@Column(name = "description", nullable=false, columnDefinition = "varchar(1024) default '' ")
	private String description;


}
