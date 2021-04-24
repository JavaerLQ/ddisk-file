package io.ddisk.controller;

import io.ddisk.domain.dto.PageDTO;
import io.ddisk.domain.enums.FileTypeEnum;
import io.ddisk.domain.vo.DirTreeNode;
import io.ddisk.domain.vo.FileVO;
import io.ddisk.domain.vo.LoginUser;
import io.ddisk.domain.vo.PathNodeVO;
import io.ddisk.domain.vo.base.PageVO;
import io.ddisk.service.UserFileService;
import io.ddisk.utils.SpringWebUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;


/**
 * @author Richard.Lee
 */
@Tag(name = "File", description = "该接口为文件接口，主要用来做一些文件的基本操作，如创建目录，删除，移动，复制等。")
@RestController
@RequestMapping("/file")
public class FileController {

	@Autowired
	private UserFileService userFileService;


	@Operation(summary = "获取目录及文件", description = "前端提供父目录和分页数据，返回该文件夹下的这些数据", tags = {"file"})
	@GetMapping(value = "/list")
	@Parameters({
			@Parameter(name = "pid", description = "用户文件目录id，需要登录用户的文件", required = true)
	})
	public ResponseEntity<PageVO<FileVO>> getFileList(@NotNull PageDTO page, @RequestParam(required = false) String pid) {

		LoginUser user = SpringWebUtils.requireLogin();
		PageVO<FileVO> fileListPage = userFileService.listTheDir(user.getId(), pid, page);
		return ResponseEntity.ok(fileListPage);
	}

	@Operation(summary = "获取某类型文件", description = "获取某一类型文件，例如图片、视频、文档等", tags = {"file"})
	@GetMapping(value = "/list/type")
	@Parameters({
			@Parameter(name = "fileType", description = "文件类型，图片文件，视频文件等", required = true)
	})
	public ResponseEntity<PageVO<FileVO>> getFileListByType(@NotNull PageDTO page, FileTypeEnum fileType) {

		LoginUser user = SpringWebUtils.requireLogin();
		PageVO<FileVO> fileListPage = userFileService.listTheTypeFile(user.getId(), fileType, page);
		return ResponseEntity.ok(fileListPage);
	}

	@Operation(summary = "文件搜索", description = "模糊搜索文件名", tags = {"file"})
	@GetMapping(value = "/search")
	@Parameters({
			@Parameter(name = "filename", description = "模糊搜索文件名", required = true)
	})
	public ResponseEntity<PageVO<FileVO>> searchFile(@NotNull PageDTO page, @NotBlank String filename) {

		PageVO<FileVO> fileListPage = userFileService.searchFile(filename, page);
		return ResponseEntity.ok(fileListPage);
	}

	@Operation(summary = "获取目录树", description = "文件移动的时候需要用到该接口，用来展示目录树，展示机制为饱汉模式", tags = {"file"})
	@GetMapping(value = "/dir/tree")
	public ResponseEntity<DirTreeNode> getDirTree() {

		LoginUser user = SpringWebUtils.requireLogin();
		DirTreeNode tree = userFileService.dirTree(user.getId());
		return ResponseEntity.ok(tree);
	}

	@Operation(summary = "查询回收站文件", description = "查询某用户回收站所有文件", tags = {"file"})
	@GetMapping(value = "/list/recycle")
	public ResponseEntity<PageVO<FileVO>> listRecycleBin(@NotNull PageDTO page) {

		LoginUser user = SpringWebUtils.requireLogin();
		PageVO<FileVO> fileListPage = userFileService.listRecycle(user.getId(), page);
		return ResponseEntity.ok(fileListPage);
	}

	@Operation(summary = "父节点列表", description = "用于显示父路径，字典key为文件id", tags = {"file"})
	@GetMapping(value = "/path/tree")
	public ResponseEntity<Map<String, PathNodeVO>> getPathTree() {

		LoginUser user = SpringWebUtils.requireLogin();
		Map<String, PathNodeVO> pathTreeMap = userFileService.getPathTreeMap(user.getId());
		return ResponseEntity.ok(pathTreeMap);
	}

	@Operation(summary = "移动文件", description = "移动文件", tags = {"file"})
	@PostMapping(value = "/move")
	@Parameters({
			@Parameter(name = "from", description = "移动文件id", required = true),
			@Parameter(name = "to", description = "目标文件夹id", required = true)
	})
	public ResponseEntity<Void> move(@NotBlank String from, @RequestParam(required = false) String to) {

		userFileService.move(from, to);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "移动文件", description = "批量移动文件", tags = {"file"})
	@PostMapping(value = "/batch/move")
	@Parameters({
			@Parameter(name = "from", description = "移动文件id列表", required = true),
			@Parameter(name = "to", description = "目标文件夹id", required = true)
	})
	public ResponseEntity<Void> batchMove(@RequestParam(value = "from") @NotEmpty @NotBlank List<String> from, @NotBlank @RequestParam(required = false) String to) {

		userFileService.move(from, to);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "创建目录", description = "创建文件夹", tags = {"file"})
	@PostMapping(value = "/mkdir")
	@Parameters({
			@Parameter(name = "pid", description = "父目录id", required = false),
			@Parameter(name = "name", description = "目录名", required = true),

	})
	public ResponseEntity<Void> mkdir(@RequestParam(required = false) @NotBlank String pid, @NotBlank String name) {
		LoginUser user = SpringWebUtils.requireLogin();
		userFileService.mkdir(user.getId(), pid, name);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "重命名", description = "重命名文件名和扩展名", tags = {"file"})
	@PostMapping(value = "/rename")
	@Parameters({
			@Parameter(name = "userFileId", description = "文件id", required = true),
			@Parameter(name = "filename", description = "文件基础名", required = true),
			@Parameter(name = "extension", description = "文件扩展名", required = true),

	})
	public ResponseEntity<Void> rename(@NotBlank String userFileId, @NotBlank String filename, String extension) {
		userFileService.rename(userFileId, filename, extension);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "删除文件", description = "删除文件或文件夹", tags = {"file"})
	@PostMapping(value = "/delete")
	@Parameters({
			@Parameter(name = "userFileId", description = "文件id", required = true),
	})
	public ResponseEntity<Void> delete(@NotBlank String userFileId) {
		userFileService.delete(userFileId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "批量删除文件", description = "批量删除文件或文件夹", tags = {"file"})
	@PostMapping(value = "/batch/delete")
	@Parameters({
			@Parameter(name = "userFileIds", description = "文件id列表", required = true),
	})
	public ResponseEntity<Void> batchDelete(@NotEmpty @NotBlank @RequestParam(value = "userFileIds") List<String> userFileIds) {
		userFileService.delete(userFileIds);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "恢复文件", description = "恢复回收站文件或文件夹", tags = {"file"})
	@PostMapping(value = "/recover")
	@Parameters({
			@Parameter(name = "userFileId", description = "文件id", required = true),
	})
	public ResponseEntity<Void> recover(@NotBlank String userFileId) {
		userFileService.recover(userFileId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "批量恢复文件", description = "批量恢复回收站文件或文件夹", tags = {"file"})
	@PostMapping(value = "/batch/recover")
	@Parameters({
			@Parameter(name = "userFileIds", description = "文件id列表", required = true),
	})
	public ResponseEntity<Void> batchRecover(@NotEmpty @NotBlank @RequestParam(value = "userFileIds") List<String> userFileIds) {
		userFileService.recover(userFileIds);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "删除回收站文件", description = "真正的删除文件接口，删除回收站里的文件", tags = {"file"})
	@PostMapping(value = "/delete/recycle")
	@Parameters({
			@Parameter(name = "userFileId", description = "文件id", required = true),
	})
	public ResponseEntity<Void> deleteFromRecycleBin(@NotBlank String userFileId) {
		userFileService.deleteFromRecycleBin(userFileId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "批量删除回收站文件", description = "真正的删除文件接口，删除回收站里的文件", tags = {"file"})
	@PostMapping(value = "/batch/delete/recycle")
	@Parameters({
			@Parameter(name = "userFileIds", description = "文件id列表", required = true),
	})
	public ResponseEntity<Void> batchDeleteFromRecycleBin(@RequestParam(value = "userFileIds") @NotNull @NotBlank List<String> userFileIds) {
		userFileService.deleteFromRecycleBin(userFileIds);
		return ResponseEntity.ok().build();
	}

}
