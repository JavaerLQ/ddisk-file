package io.ddisk.dao;

import io.ddisk.domain.entity.ThumbnailEntity;
import io.ddisk.domain.enums.ImageSizeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/5
 */
public interface ThumbnailRepository extends JpaRepository<ThumbnailEntity, Long> {

	@Query("from ThumbnailEntity where fileId=:fileId and width=:#{#size.width} and height=:#{#size.height}")
	Optional<ThumbnailEntity> findByFileIdAndImageSize(String fileId, ImageSizeEnum size);

	@Query("from ThumbnailEntity t where t.fileId not in (select f.id from FileEntity f)")
	List<ThumbnailEntity> findAllGarbage();
}
