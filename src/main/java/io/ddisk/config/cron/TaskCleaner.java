package io.ddisk.config.cron;

import io.ddisk.service.FileCleanerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 垃圾清理器
 *
 * @Author: Richard.Lee
 * @Date: created by 2021/4/5
 */
@Configuration
@EnableScheduling
public class TaskCleaner {


	@Autowired
	private FileCleanerService fileCleanerService;

	/**
	 * 每天1点清理
	 * 清理已合并的切片文件
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void cleanMergedChunkFile() {
		fileCleanerService.cleanMergedFiles();
	}

	/**
	 * 每天2点清理
	 * 清理不完整切片文件
	 */
	@Scheduled(cron = "0 0 2 * * ?")
	public void cleanIncompleteChunkFile() {
		fileCleanerService.cleanIncompleteChunks();
	}


	/**
	 * 每天3点清理
	 * 清理未使用的文件，即FileEntity.count <= 0文件
	 */
	@Scheduled(cron = "0 0 3 * * ?")
	public void cleanUnusedFile() {
		fileCleanerService.cleanCount0Files();
	}


	/**
	 * 每天4点清理
	 * 清理缩略图文件
	 */
	@Scheduled(cron = "0 0 4 * * ?")
	public void cleanThumbnail() {
		fileCleanerService.cleanThumbnail();
	}

	/**
	 * 每天0点清理回收站
	 * 清理回收站文件
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void cleanRecycleFile() {
		fileCleanerService.cleanRecycleFile();
	}
}
