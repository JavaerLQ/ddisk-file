package io.ddisk.dao;

import io.ddisk.domain.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/16
 */
public interface FileRepository extends JpaRepository<FileEntity, String> {

	List<FileEntity> findAllByCountAndCreateTimeBefore(Long count, Date date);
}
