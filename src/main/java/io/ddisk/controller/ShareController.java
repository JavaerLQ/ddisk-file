package io.ddisk.controller;

import io.ddisk.domain.dto.FileShareDTO;
import io.ddisk.domain.vo.LoginUser;
import io.ddisk.service.FileShareService;
import io.ddisk.utils.SpringWebUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	@PostMapping(value = "/file")
	public ResponseEntity<Void> fileShare(@RequestBody FileShareDTO fileShareDTO){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.shareFile(fileShareDTO, user.getId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "移除分享文件", description = "取消分享", tags = {"fileShare"})
	@GetMapping(value = "/cancel/file")
	public ResponseEntity<Void> cancelShareFile(String shareId){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.cancelShareFile(shareId, user.getId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "移除分享文件", description = "批量取消分享", tags = {"fileShare"})
	@GetMapping(value = "/batch/cancel/file")
	public ResponseEntity<Void> batchCancelShareFile(@RequestParam(name = "shareIds") List<String> shareIds){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.batchCancelShareFile(shareIds, user.getId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "取消分享文件组", description = "取消分享组", tags = {"fileShare"})
	@GetMapping(value = "/cancel/group")
	public ResponseEntity<Void> cancelShareGroup(String shareGroupId){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.cancelShareGroup(shareGroupId, user.getId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "取消分享文件组", description = "批量取消分享组", tags = {"fileShare"})
	@GetMapping(value = "/batch/cancel/group")
	public ResponseEntity<Void> batchCancelShareGroup(@RequestParam(name = "shareGroupIds") List<String> shareGroupIds){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.batchCancelShareGroup(shareGroupIds, user.getId());
		return ResponseEntity.ok().build();
	}


	@Operation(summary = "保存分享文件", description = "保存分享文件", tags = {"fileShare"})
	@GetMapping(value = "/save")
	public ResponseEntity<Void> saveShareFile(String shareId, String pid){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.saveShareFile(List.of(shareId), pid, user.getId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "保存分享文件", description = "批量保存分享文件", tags = {"fileShare"})
	@GetMapping(value = "/batch/save")
	public ResponseEntity<Void> batchSaveShareFile(@RequestParam(name = "shareGroupIds") List<String> shareIds, String pid){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.saveShareFile(shareIds, pid, user.getId());
		return ResponseEntity.ok().build();
	}
}
