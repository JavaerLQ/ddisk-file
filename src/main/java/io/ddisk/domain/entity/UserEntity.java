package io.ddisk.domain.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/** 用户
 * @Author: Richard.Lee
 * @Date: created by 2021/2/20
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames={"username", "email"}))
public class UserEntity implements Serializable {

	/**
	 * 用户id
	 */
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	/**
	 * 用户名
	 */
	@Column(name = "username", nullable = false)
	private String username;

	/**
	 * 用户邮箱
	 */
	@Column(name = "email", nullable = false)
	private String email;

	/**
	 * 用户头像链接，指向UserFile的ID
	 */
	@Column(name = "image_file_id", length = 32)
	private String imgUrl;

	/**
	 * 加密后的密码
	 */
	@Column(name = "password", nullable = false, length = 64)
	private String password;


	/**
	 * 对应的角色
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private RoleEntity role;

	/**
	 * 注册时间
	 */
	@Column(name = "user_register_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date registerTime;

	/**
	 * 用户是否被锁定, 异常操作会被锁定
	 */
	@Column(name = "is_account_non_locked", nullable = false)
	private Boolean accountNonLocked;

}
