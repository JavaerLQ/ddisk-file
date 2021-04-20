package io.ddisk.utils;

import io.ddisk.domain.enums.ThumbnailTypeEnum;
import io.vavr.control.Try;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/5
 */
public class ImageUtils {

	/**
	 * 生成{@code ThumbnailTypeEnum.MIN_SIZE} 大小的略缩图
	 * @param in
	 * @param out
	 */
	public static void generateMinSize(File in, File out){
		Try.run( ()-> Thumbnails.of(in).size(ThumbnailTypeEnum.MIN_SIZE.getWidth(), ThumbnailTypeEnum.MIN_SIZE.getHeight()).toFile(out) );
	}

	/**
	 * 生成{@code ThumbnailTypeEnum.MIN_SCALE} 大小的略缩图
	 * @param in
	 * @param out
	 */
	public static void generateMinScale(File in, File out){
		Try.run( ()-> Thumbnails.of(in).scale(ThumbnailTypeEnum.MIN_SCALE.getScale()).toFile(out) );
	}
}
