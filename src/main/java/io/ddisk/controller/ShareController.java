package io.ddisk.controller;

import io.ddisk.domain.dto.FileShareDTO;
import io.ddisk.domain.vo.LoginUser;
import io.ddisk.service.FileShareService;
import io.ddisk.utils.SpringWebUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/21
 */
@Tag(name = "FileShare", description = "该接口为用户文件分享接口，主要是做用户文件分享，删除，下载等操作")
@RestController
@Validated
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
	@PostMapping(value = "/cancel/file")
	public ResponseEntity<Void> cancelShareFile(@Length(min = 32, max = 32) String shareId){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.cancelShareFile(shareId, user.getId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "移除分享文件", description = "批量取消分享", tags = {"fileShare"})
	@PostMapping(value = "/batch/cancel/file")
	public ResponseEntity<Void> batchCancelShareFile(@RequestParam(name = "shareIds")@NotEmpty List<String> shareIds){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.batchCancelShareFile(shareIds, user.getId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "取消分享文件组", description = "取消分享组", tags = {"fileShare"})
	@PostMapping(value = "/cancel/group")
	public ResponseEntity<Void> cancelShareGroup(@Length(min = 32, max = 32)String shareGroupId){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.cancelShareGroup(shareGroupId, user.getId());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "取消分享文件组", description = "批量取消分享组", tags = {"fileShare"})
	@PostMapping(value = "/batch/cancel/group")
	public ResponseEntity<Void> batchCancelShareGroup(@RequestParam(name = "shareGroupIds") @NotEmpty List<String> shareGroupIds){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.batchCancelShareGroup(shareGroupIds, user.getId());
		return ResponseEntity.ok().build();
	}


	@Operation(summary = "保存分享文件", description = "保存分享文件", tags = {"fileShare"})
	@PostMapping(value = "/save")
	public ResponseEntity<Void> saveShareFile(@Length(min = 32, max = 32) String shareId, @Length(min = 32, max = 32) String pid, @Length(min = 6, max = 6)String key){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.saveShareFile(List.of(shareId), pid, user.getId(), key);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "保存分享文件", description = "批量保存分享文件", tags = {"fileShare"})
	@PostMapping(value = "/batch/save")
	public ResponseEntity<Void> batchSaveShareFile(@RequestParam(value = "shareIds") @NotEmpty List<String> shareIds, @Length(min = 32, max = 32) String pid, @Length(min = 6, max = 6) String key){
		LoginUser user = SpringWebUtils.requireLogin();
		fileShareService.saveShareFile(shareIds, pid, user.getId(), key);
		return ResponseEntity.ok().build();
	}
}
