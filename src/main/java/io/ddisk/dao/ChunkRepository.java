package io.ddisk.dao;

import io.ddisk.domain.dto.ChunkPathDTO;
import io.ddisk.domain.entity.ChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/23
 */
public interface ChunkRepository extends JpaRepository<ChunkEntity, Long> {

	/**
	 * 查询该文件里包含的chunkSize
	 * @param identifier
	 * @return
	 */
	@Query("select distinct ce.chunkSize from ChunkEntity ce where ce.identifier = :identifier")
	List<Long> findAllChunkSizeByIdentifier(String identifier);

	/**
	 * 根据文件唯一标识和切片大小查询切片
	 * @param identifier
	 * @param chunkSize
	 * @return
	 */
	List<ChunkEntity> findAllByIdentifierAndChunkSize(String identifier, Long chunkSize);

	/**
	 * 查询该文件已上传切片序号
	 */
	@Query(value = "select distinct ce.chunkNumber from ChunkEntity ce where ce.identifier = :identifier and ce.chunkSize = :chunkSize")
	Set<Integer> findAllNumberByIdentifierAndChunkSize(String identifier, Long chunkSize);

	/**
	 * 查询已合并的切片
	 * @return 文件唯一id
	 */
	@Query(value = "select distinct c.identifier from ChunkEntity c where c.identifier in (select f.id from FileEntity f)")
	List<String> findMergedFiles();

	/**
	 * 查询指定时间前未合并的切片,
	 * @return
	 */
	@Query(value = "select distinct new io.ddisk.domain.dto.ChunkPathDTO(c.identifier, c.chunkSize) from ChunkEntity c where c.uploadTime < :date and c.identifier not in (select f.id from FileEntity f)")
	List<ChunkPathDTO> findIncompleteChunksDateBefore(Date date);

	void deleteAllByIdentifierIn(Collection<String> identifier);
}
