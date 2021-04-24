package io.ddisk.controller;

import io.ddisk.domain.dto.FileShareDTO;
import io.ddisk.domain.vo.LoginUser;
import io.ddisk.service.FileShareService;
import io.ddisk.utils.SpringWebUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/21
 */
@Tag(name = "FileShare", description = "该接口为用户文件分享接口，主要是做用户文件分享，删除，下载等操作")
@RestController
@RequestMapping("/share")
public class ShareController {

	@Autowired
	private FileShareService fileShareService;

	@Operation(summary = "分享文件", description = "前端提供分享文件id和分享条件", tags = {"fileShare"})
	@GetMapping(value = "/file")
	public ResponseEntity<Void> fileShare(@RequestBody FileShareDTO fileShareDTO){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.shareFile(fileShareDTO, user.getId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "移除分享文件", description = "取消分享", tags = {"fileShare"})
	@GetMapping(value = "/cancel")
	public ResponseEntity<Void> cancelShare(String shareId){
		fileShareService.cancelShare(shareId);
		return ResponseEntity.ok().build();
	}

}
