package io.ddisk.utils;

import io.ddisk.domain.enums.ImageSizeEnum;
import io.vavr.control.Try;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/5
 */
public class ImageUtils {

	/**
	 * 生成{@code ImageSizeEnum.MIN} 大小的略缩图
	 * @param in
	 * @param out
	 */
	public static void generateMin(File in, File out){
		Try.run( ()-> Thumbnails.of(in).size(ImageSizeEnum.MIN.getWidth(), ImageSizeEnum.MIN.getHeight()).toFile(out));
	}
}
