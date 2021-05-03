package io.ddisk.service;

import io.ddisk.domain.dto.PageDTO;
import io.ddisk.domain.entity.UserFileEntity;
import io.ddisk.domain.enums.FileTypeEnum;
import io.ddisk.domain.vo.DirTreeNode;
import io.ddisk.domain.vo.FileVO;
import io.ddisk.domain.vo.PathNodeVO;
import io.ddisk.domain.vo.base.PageVO;

import java.util.List;
import java.util.Map;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/25
 */
public interface UserFileService {


	/**
	 * 创建文件夹
	 * @param userId
	 * @param pid 父目录
	 * @param filename 文件夹   
	 */
	void mkdir(Long userId, String pid,  String filename);

	/**
	 * 文件重命名
	 * @param userFileId 用户文件id
	 * @param filename 文件名
	 * @param extension 扩展名
	 */
	void rename(String userFileId, String filename, String extension);
	
	/**
	 * 分页查询
	 * 列出某目录下的所有文件，包含文件夹。
	 * @param userId 用户ID
	 * @param pid 父目录ID
	 * @param page 分页数据，前端起始页1，默认每页条数10
	 * @return
	 */
	PageVO<FileVO> listTheDir(Long userId, String pid, PageDTO page);

	/**
	 * 展示目录树
	 * @param userId
	 * @return
	 */
	DirTreeNode dirTree(Long userId);

	/**
	 * 移动文件
	 * @param from
	 * @param to
	 */
	void move(String from, String to);

	/**
	 * 批量移动文件
	 * @param fromList
	 * @param to
	 */
	void move(List<String> fromList, String to);

	/**
	 * 删除文件或者文件夹，删除文件夹时，文件夹里的文件也一并删除。此操作为逻辑删除
	 * @param userFileId
	 */
	void delete(String userFileId);

	/**
	 * 批量删除文件或者文件夹，删除文件夹时，文件夹里的文件也一并删除。此操作为逻辑删除
	 * @param userFileList
	 */
	void delete(List<String> userFileList);


	/**
	 * 恢复回收站文件
	 * @param userFileId
	 */
	void recover(String userFileId);

	/**
	 * 批量恢复回收站文件
	 * @param userFileIdList
	 */
	void recover(List<String> userFileIdList);

	/**
	 * 真正的删除，从回收站里删除
	 */
	void deleteFromRecycleBin(String userFileId);


	/**
	 * 真正的删除，从回收站里删除
	 */
	void deleteFromRecycleBin(List<String> userFileIdList);

	/**
	 * 分页查询出这个类型的文件
	 * @param fileType
	 * @param page
	 * @return
	 */
	PageVO<FileVO> listTheTypeFile(Long userId, FileTypeEnum fileType, PageDTO page);

	/**
	 * 列出回收站所有文件
	 * @param userId
	 * @param page
	 * @return
	 */
	PageVO<FileVO> listRecycle(Long userId, PageDTO page);


	/**
	 * 获取路径树map
	 * @param userId
	 * @return
	 */
	public Map<String, PathNodeVO> getPathTreeMap(Long userId);

	/**
	 * 搜索文件
	 * @param filename
	 * @param page
	 * @return
	 */
	PageVO<FileVO> searchFile(String filename, PageDTO page);

	/**
	 * 检查图片是否合法，是否是该用户的图片
	 * @param userId
	 * @param fileId
	 */
	void checkImage(Long userId, String fileId);

	/**
	 * 获取这些文件或者目录及子文件、子目录
	 *
	 * @param userFileIdList
	 * @param delete     如果是获取回收站文件及子文件true，获取非回收站文件false
	 * @return
	 */
	List<UserFileEntity> getChildrenList(List<String> userFileIdList, Boolean delete);
}
