package io.ddisk.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 独一无二的文件
 * @Author: Richard.Lee
 * @Date: created by 2021/2/20
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "file")
public class FileEntity implements Serializable {

	/**
	 * 文件独一无二的MD5
	 */
	@Id
	@Column(name = "file_id", length = 32)
	private String id;

	/**
	 * 文件大小
	 */
	@Column(name = "file_size", nullable = false)
	private Long size;

	/**
	 * 指向文件真正的URL
	 */
	@Column(name = "file_url", nullable = false)
	private String url;

	/**
	 * 该文件使用人数
	 */
	@Column(name = "reference_count", nullable = false)
	private Long count;

	/**
	 * 文件类型
	 */
	@Column(name = "mimetype", nullable = false)
	private String mimetype;

	/**
	 * 用户文件创建时间
	 */
	@Column(name = "create_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	public FileEntity(String uuid, Long size, String url, Long count, String mimetype) {
		this.id = uuid;
		this.size = size;
		this.url = url;
		this.count = count;
		this.mimetype = mimetype;
		this.createTime = new Date();
	}
}
