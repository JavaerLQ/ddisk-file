package io.ddisk.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy="uuid")
	private String shareId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_file_id", unique = true, nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private UserFileEntity userFileEntity;


	@Column(name = "share_group_id", length = 32)
	private String fileShareGroupId;

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
