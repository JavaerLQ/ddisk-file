package io.ddisk.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 用户可用存储空间
 * @Author: Richard.Lee
 * @Date: created by 2021/3/22
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "user_storage")
public class UserStorageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "storage_id")
	private Long storageId;

	/**
	 * 用户id
	 */
	@Column(name = "user_id", unique = true)
	private Long userId;

	/**
	 * 已使用存储空间
	 */
	@Column(name = "used_storage", nullable = false)
	private Long usedStorage;

	public UserStorageEntity(Long userId, Long usedStorage) {
		this.userId = userId;
		this.usedStorage = usedStorage;
	}
}
