package com.robocon321.demo.entity.post;

import java.util.Date;
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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.robocon321.demo.entity.post.product.Product;
import com.robocon321.demo.entity.review.Comment;
import com.robocon321.demo.entity.review.Vote;
import com.robocon321.demo.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = false)
	private String description;
	
	@Column(nullable = false, columnDefinition  = "DEFAULT 0")
	private int view;
	
	private String thumbnail;
	
	@Column(name = "gallery_image")
	private String galleryImage;
	
	@Column(nullable = false)
	private String type;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "parent_id")
	private Post parentPost;
	
	@Column(nullable = false)
	private String slug;
	
	@Column(name = "meta_title")
	private String metaTitle;
	
	@Column(name = "meta_description")
	private String metaDescription;
	
	@Column(name = "comment_status", 
			nullable = false, 			
			columnDefinition = "DEFAULT 1")
	private Integer commentStatus;
	
	@Column(name = "comment_count", 
			nullable = false, 			
			columnDefinition = "DEFAULT 1")
	private Integer commentCount;
	
	@Column(nullable = false, columnDefinition = "DEFAULT 1")	
	private Integer status;
		
	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "mod_user_id")
	@JsonIgnore
	private User modifiedUser;
	
	@Column(name = "mod_time", 
			nullable = false, 
			columnDefinition = "DEFAULT CURRENT_TIMESTAMP")
	private Date modifiedTime;
	
//	@ManyToMany(cascade = CascadeType.REMOVE)
//	@JoinTable(name = "link_post", 
//				joinColumns = @JoinColumn(nullable = false, name = "post1_id"), 
//				inverseJoinColumns = @JoinColumn(nullable = false, name = "post2_id"))	
//	@JsonIgnore
//	private List<Post> linkPosts;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "post")
	@JsonIgnore
	private List<Comment> comments;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "post")
	@JsonIgnore
	private List<Vote> votes;	
	
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "post")
	@JsonIgnore
	private List<PostMeta> postMetas;
	
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "parentPost")
	@JsonIgnore
	private List<Post> posts;

	@OneToOne(cascade = CascadeType.REMOVE, mappedBy = "post")
	@JsonIgnore
	private Product product;
}
