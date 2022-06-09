package com.robocon321.demo.entity.post.product;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.robocon321.demo.entity.checkout.CartItem;
import com.robocon321.demo.entity.post.Post;
import com.robocon321.demo.entity.taxomony.TaxomonyObj;
import com.robocon321.demo.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product implements TaxomonyObj {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(targetEntity = Post.class)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;
	
	@Column(name = "min_price", nullable = false)
	private Double minPrice;

	@Column(name = "max_price", nullable = false)
	private Double maxPrice;

	@Column(columnDefinition = "DEFAULT 0", nullable = false)
	private Double weight;

	@Column(columnDefinition = "DEFAULT 0", nullable = false)
	private Double width;

	@Column(columnDefinition = "DEFAULT 0", nullable = false)
	private Double height;
	
	@Column(name = "stock_quantity", columnDefinition = "DEFAULT 0", nullable = false)
	private Integer stockQuantity;
		
	@Formula("(SELECT count(*) FROM product p JOIN cart_item ci ON p.id = ci.product_id JOIN cart ca ON ca.id = ci.cart_id JOIN checkout ck ON ck.cart_id = ca.id WHERE p.id = id GROUP BY p.id)")
	private Integer totalSales;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	@JsonIgnore
	private List<CartItem> cartItems;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	@JsonIgnore
	private List<Attribute> attributes;

	@ManyToMany
	@JoinTable(name = "wishlist",
		joinColumns = @JoinColumn(name = "product_id"),
		inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	@JsonIgnore
	private List<User> users;
}
