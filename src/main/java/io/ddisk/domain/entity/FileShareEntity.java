package io.ddisk.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/21
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "file_share")
public class FileShareEntity {

	@Id
	@Column(name = "file_share_id", length = 32)
	private String shareId;

	@Column(name = "user_file_id", nullable = false)
	private String userFileId;

	@Column(name = "file_name", nullable = false)
	private String filename;

	@Column(name = "file_extension", nullable = true)
	private String extension;

	@Column(name = "is_dir", nullable = false)
	private Boolean dir;

	@Column(name = "share_group_id", length = 32)
	private String fileShareGroupId;

	/**
	 * 父目录id，最上层文件null
	 */
	@Column(name = "share_pid", length = 32)
	private String pid;
	/**
	 * 下载次数
	 */
	@Column(name = "download_count")
	private Long dCount;

	/**
	 * 保存次数
	 */
	@Column(name = "save_count")
	private Long sCount;
}
