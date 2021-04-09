package io.ddisk.domain.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * 记录上传的切片
 * @Author: Richard.Lee
 * @Date: created by 2021/3/22
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "chunk")
public class ChunkEntity {

	@Id
	@GeneratedValue
	@Column(name = "chunk_id")
	private Long id;
	/**
	 * 当前文件块，从1开始
	 */
	@Column(name = "chunk_number", nullable = false)
	private Integer chunkNumber;
	/**
	 * 分块大小
	 */
	@Column(name = "chunk_size", nullable = false)
	private Long chunkSize;
	/**
	 * 当前分块大小
	 */
	@Column(name = "current_chunk_size", nullable = false)
	private Long currentChunkSize;
	/**
	 * 总大小
	 */
	@Column(name = "total_size", nullable = false)
	private Long totalSize;
	/**
	 * 文件标识
	 */
	@Column(name = "identifier", nullable = false)
	private String identifier;
	/**
	 * 总块数
	 */
	@Column(name = "total_chunks", nullable = false)
	private Integer totalChunks;

	/**
	 * 切片上传时间
	 */
	@Column(name = "upload_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date uploadTime = new Date();


	public void setUploadTime(Date uploadTime) {
		if (Objects.isNull(uploadTime)){
			uploadTime = new Date();
		}
		this.uploadTime = uploadTime;
	}
}
