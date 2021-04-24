package io.ddisk.dao;

import io.ddisk.domain.entity.UserFileEntity;
import io.ddisk.domain.vo.PathNodeVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/8
 */
public interface UserFileRepository extends JpaRepository<UserFileEntity, String> {

	List<UserFileEntity> findAllByUserIdAndIdIn(Long userId, Collection<String> ids);

	Optional<UserFileEntity> findByIdAndDelete(String userFileId, Boolean delete);

	List<UserFileEntity> findAllByIdInAndDelete(Collection<String> userFileIds, Boolean delete);

	List<UserFileEntity> findAllByDeleteIsTrueAndDeleteTimeBefore(Date date);

	List<UserFileEntity> findAllByUserIdAndDirAndDelete(Long userId, Boolean dir, Boolean delete);

	List<UserFileEntity> findAllByPidInAndDelete(List<String> pids, Boolean delete);

	Page<UserFileEntity> findAllByUserIdAndPidAndDelete(Long userId, String pid, Boolean delete, Pageable pageable);

	/**
	 * 分页查询非回收站文件列表，根据用户id和扩展名集合
	 */
	Page<UserFileEntity> findAllByUserIdAndExtensionInAndDirIsFalseAndDeleteIsFalse(Long userId, Collection<String> extensions, Pageable pageable);

	/**
	 * 分页查询其他文件列表，根据扩展名集合排除文件
	 */
	Page<UserFileEntity> findAllByUserIdAndExtensionNotInAndDirIsFalseAndDeleteIsFalse(Long userId, Collection<String> extensions, Pageable pageable);

	Page<UserFileEntity> findAllByFilenameLike(String filename, Pageable pageable);

	@Query(
			value = "select u1 from UserFileEntity u1 where u1.userId=:userId and u1.delete=true and (u1.pid is null or u1.pid not in (select u2.id from UserFileEntity u2 where u2.userId = :userId AND u2.delete = true))"
	)
	Page<UserFileEntity> findAllRecycleRoot(Long userId, Pageable pageable);


	@Query("select new io.ddisk.domain.vo.PathNodeVO(uf.id, uf.pid, uf.filename) from UserFileEntity uf where uf.userId = :userId")
	List<PathNodeVO> findAllPathNodeByUserId(Long userId);

	@Modifying
	@Query("update UserFileEntity uf set uf.pid=:to where uf.id in :fromList")
	int updatePidByIdInAndPid(List<String> fromList, String to);

	@Modifying
	@Query("update UserFileEntity uf set uf.delete=:delete, uf.deleteTime=current_date where uf.id in :ids")
	int updateDeleteByIdIn(Collection<String> ids, Boolean delete);
}
