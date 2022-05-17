package com.robocon321.demo.entity.taxomony;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.robocon321.demo.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "taxomony_meta")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxomonyMeta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String key;
	private String value;
	
	@ManyToOne(targetEntity = Taxomony.class)
	@JoinColumn(name = "taxomony_id")
	@JsonIgnore
	private Taxomony taxomony;
}
