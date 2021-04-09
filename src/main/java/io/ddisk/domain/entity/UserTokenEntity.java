package io.ddisk.domain.entity;

import io.ddisk.domain.enums.TokenTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户令牌表，用于存储用户验证令牌
 * @Author: Richard.Lee
 * @Date: created by 2021/4/7
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "user_token", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "token_type"}))
public class UserTokenEntity {
	/**
	 * 用户id
	 */
	@Id
	@Column(name = "user_token_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	@Column(name = "email", nullable = false)
	private String email;

	/**
	 * 令牌类型，用于区分是何作用的令牌
	 */
	@Column(name = "token_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private TokenTypeEnum type;

	/**
	 * 令牌是一个uuid，拥有唯一性
	 */
	@Column(name = "token", nullable = false)
	private String token;

	/**
	 * 用户文件创建时间
	 */
	@Column(name = "expires_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiresTime;

	/**
	 * 是否有效
	 */
	@Column(name = "is_active", nullable = false)
	private Boolean active;


	public UserTokenEntity(String email, TokenTypeEnum type, String token, Date expiresTime) {
		this.email = email;
		this.type = type;
		this.token = token;
		this.expiresTime = expiresTime;
		this.active = true;
	}

	public UserTokenEntity(String email, TokenTypeEnum type) {
		this.email = email;
		this.type = type;
		this.active = false;
	}
}
