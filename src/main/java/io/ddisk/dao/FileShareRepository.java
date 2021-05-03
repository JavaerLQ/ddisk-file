package io.ddisk.dao;

import io.ddisk.domain.entity.FileShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/21
 */
public interface FileShareRepository extends JpaRepository<FileShareEntity, String> {

	List<FileShareEntity> findAllByPidIn(Collection<String> pids);

	List<FileShareEntity> findAllByFileShareGroupIdIn(Collection<String> shareGroupIds);
}
