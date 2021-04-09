package io.ddisk.dao;

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
	 * 根据文件唯一标识获取该文件的所有切片
	 * @param identifier
	 * @return
	 */
	List<ChunkEntity> findAllByIdentifier(String identifier);

	/**
	 * 查询该文件已上传切片序号
	 */
	@Query(value = "select distinct ce.chunkNumber from ChunkEntity ce where ce.identifier = :identifier")
	Set<Integer> findAllNumberByIdentifier(String identifier);

	/**
	 * 确定一个文件是否上传过该切片
	 * @param identifier
	 * @param chunkNumber
	 * @return
	 */
	Boolean existsByIdentifierAndChunkNumber(String identifier, Integer chunkNumber);

	/**
	 * 查询已合并的切片
	 * @return 文件唯一id
	 */
	@Query(value = "select distinct c.identifier from ChunkEntity c where c.identifier in (select f.id from FileEntity f)")
	List<String> findMergedFiles();

	/**
	 * 查询指定时间前未合并的切片,
	 * @return 文件唯一id
	 */
	@Query(value = "select distinct c.identifier from ChunkEntity c where c.uploadTime < :date and c.identifier not in (select f.id from FileEntity f)")
	List<String> findIncompleteChunksDateBefore(Date date);

	void deleteAllByIdentifierIn(Collection<String> identifier);
}
