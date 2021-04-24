package io.ddisk.service.impl;

import io.ddisk.dao.FileShareRepository;
import io.ddisk.dao.UserFileRepository;
import io.ddisk.domain.dto.FileShareDTO;
import io.ddisk.domain.entity.UserFileEntity;
import io.ddisk.service.FileShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/21
 */
@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class FileShareServiceImpl implements FileShareService {

	@Autowired
	private FileShareRepository fileShareRepository;

	@Autowired
	private UserFileRepository userFileRepository;


	/**
	 * 分享文件
	 *
	 * @param fileShareDTO
	 * @param userId
	 */
	@Override
	public void shareFile(FileShareDTO fileShareDTO, Long userId) {

		List<UserFileEntity> userFileEntityList = userFileRepository.findAllByUserIdAndIdIn(userId, fileShareDTO.getFileIds());
		Map<String, UserFileEntity> userFileEntityMap = userFileEntityList.stream().collect(Collectors.toMap(UserFileEntity::getFileId, uf -> uf));


	}

	/**
	 * 取消分享文件
	 *
	 * @param shareId
	 */
	@Override
	public void cancelShare(String shareId){

	}

	/**
	 * 检查分享文件有效性，并更改其状态
	 */
	private void checkValid(){
	}
}
