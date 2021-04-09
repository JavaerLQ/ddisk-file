package io.ddisk.domain.entity;

import io.ddisk.domain.enums.ImageSizeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 略缩图文件
 * @Author: Richard.Lee
 * @Date: created by 2021/4/5
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "thumbnail")
public class ThumbnailEntity {

	@Id
	@Column(name = "thumbnail_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	/**
	 * 真实文件{@code FileEntity.id}
	 */
	@Column(name = "file_id", nullable = false)
	private String fileId;

	/**
	 * url地址
	 */
	@Column(name = "img_url", nullable = false, unique = true)
	private String url;

	@Column(name = "width", nullable = false)
	private Integer width;

	@Column(name = "height", nullable = false)
	private Integer height;

	@Column(name = "file_size", nullable = false)
	private Long fileSize;

	public ThumbnailEntity(String fileId, String url, ImageSizeEnum sizeEnum, Long fileSize) {
		this.fileId = fileId;
		this.url = url;
		this.width = sizeEnum.getWidth();
		this.height = sizeEnum.getHeight();
		this.fileSize = fileSize;
	}
}
