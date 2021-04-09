package io.ddisk.domain.entity;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/3
 */

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "role")
public class RoleEntity implements Serializable {

	@Id
	@Column(name = "role_id")
	private Long id;

	@Column(name = "role_name", nullable = false)
	private String name;
}
