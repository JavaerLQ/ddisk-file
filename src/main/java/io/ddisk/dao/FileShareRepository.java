package io.ddisk.dao;

import io.ddisk.domain.entity.FileShareEntity;
import io.ddisk.domain.entity.UserFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/21
 */
public interface FileShareRepository extends JpaRepository<FileShareEntity, String> {

	Boolean existsByUserFileEntity(UserFileEntity userFileEntity);
}
