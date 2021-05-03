package io.ddisk.domain.entity;

import io.ddisk.utils.UUIDUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/22
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "file_share_group")
public class FileShareGroupEntity {

	@Id
	@Column(name = "group_id", length = 32)
	private String id = UUIDUtil.random32();

	/**
	 * 到期时间，为空表示永不过期
	 */
	@Column(name = "due_date", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dueDate;

	/**
	 * 分享创建时间
	 */
	@Column(name = "share_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	/**
	 * 分享密钥
	 */
	@Column(name = "share_key", nullable = true)
	private String key;

	/**
	 * 匿名下载
	 */
	@Column(name = "anonymous_download", nullable = false)
	private Boolean anonymousDownload;

	/**
	 * 公开到分享大厅
	 */
	@Column(name = "is_visible", nullable = false)
	private Boolean visible;

	/**
	 * 分享该文件的用户
	 */
	@Column(name = "user_id", nullable = false)
	private Long userId;

	/**
	 * 是否生效
	 */
	@Column(name = "is_active", nullable = false)
	private Boolean active;
}
