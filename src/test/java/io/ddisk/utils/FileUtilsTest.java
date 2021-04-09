package io.ddisk.utils;

import io.ddisk.domain.dto.FileUploadDTO;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/22
 */
class FileUtilsTest {

	@Test
	void chunk() throws IOException {
		FileUploadDTO dto = new FileUploadDTO();
		dto.setChunkNumber(1);
		dto.setChunkSize(11L);
		dto.setFilename("hello.txt");
		dto.setIdentifier("ddisk");
		MultipartFile file = new MockMultipartFile("hello.txt", "hello.txt", "text/plain", "Hello World".getBytes(StandardCharsets.UTF_8));
		dto.setFile(file);

		FileUtils.chunk(dto);
		Path chunkPath = FileUtils.getChunkPath(dto);
		assertTrue(Files.deleteIfExists(chunkPath));

		// 清理垃圾
		FileSystemUtils.deleteRecursively(chunkPath.getParent());
	}

}