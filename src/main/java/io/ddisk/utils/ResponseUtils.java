package io.ddisk.utils;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.ddisk.domain.dto.FileDTO;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import io.vavr.control.Try;
import jodd.util.StringPool;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/5
 */
public class ResponseUtils {

	/**
	 * 构建一个快捷回复工具类
	 *
	 * @param response
	 * @return
	 */
	public static BodyBuilder build(HttpServletResponse response) {
		return new BodyBuilder(response);
	}


	/**
	 * 构建一个快捷回复工具类
	 *
	 * @return
	 */
	public static BodyBuilder build() {
		return new BodyBuilder(SpringWebUtils.getResponse());
	}

	/**
	 * 发送文件至客户端, 支持视频拖拽，断点续传
	 */
	public static void sendFile(FileDTO fileDTO) {
		HttpServletRequest request = SpringWebUtils.getRequest();
		HttpServletResponse response = SpringWebUtils.getResponse();
		try (
				FileInputStream in = new FileInputStream(fileDTO.getUrl());
				ServletOutputStream out = response.getOutputStream()
		) {
			//浏览器设置
			String userAgent = request.getHeader("User-Agent");
			String filename = fileDTO.getFullName();
			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				//IE浏览器处理
				filename = java.net.URLEncoder.encode(filename, StandardCharsets.UTF_8);
			} else {
				// 非IE浏览器的处理：
				filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
			}
			response.setContentType(fileDTO.getContextType());
			response.setContentLengthLong(fileDTO.getSize());
			response.setHeader("Content-Disposition", String.format("attachment;fileName=%s", filename));
			try {
				long start = 0, end = fileDTO.getSize();
				//如果是video标签发起的请求就不会为null
				String rangeString = request.getHeader("Range");
				start = Long.parseLong(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
				//拖动进度条时的断点
				response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileDTO.getSize()));
				response.setHeader("Accept-Ranges", "bytes");
				String etag = DigestUtils.md5DigestAsHex((SpringWebUtils.getRequestUser() + fileDTO.getUrl()).getBytes(StandardCharsets.UTF_8));
				response.setHeader("Etag", "W/" + etag);
			} catch (Exception ignore) {
			}
			StreamUtils.copy(in, out);
			response.flushBuffer();
		} catch (IOException e) {
			throw new BizException(BizMessage.FILE_DOWNLOAD_FAIL, e.getCause());
		}
	}

	public static class BodyBuilder {
		private final HttpServletResponse response;

		private BodyBuilder(HttpServletResponse response) {
			this.response = response;
		}

		/**
		 * 设置application/json;charset=utf-8，响应状态码为200
		 *
		 * @return
		 */
		public ResultMapper ok() {
			this.response.setStatus(HttpServletResponse.SC_OK);
			this.response.setContentType("application/json;charset=utf-8");
			return new ResultMapper(Try.of(this.response::getWriter).get());
		}


		/**
		 * 设置application/json;charset=utf-8，响应状态码为400
		 *
		 * @return
		 */
		public ResultMapper badRequest() {
			this.response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			this.response.setContentType("application/json;charset=utf-8");
			return new ResultMapper(Try.of(this.response::getWriter).get());
		}


		/**
		 * 设置application/json;charset=utf-8，自定义响应状态码
		 *
		 * @return
		 */
		public ResultMapper status(int status) {
			this.response.setStatus(status);
			this.response.setContentType("application/json;charset=utf-8");
			return new ResultMapper(Try.of(this.response::getWriter).get());
		}
	}

	public static class ResultMapper {

		private final PrintWriter writer;
		private final List<Object> result = new LinkedList<>();

		private ResultMapper(PrintWriter writer) {
			this.writer = writer;
		}


		/**
		 * 像客户端写入数据，默认为追加模式
		 *
		 * @param obj
		 * @return
		 */
		public ResultMapper write(Object obj) {
			this.result.add(obj);
			return this;
		}

		/**
		 * 发送, 并刷新关闭流
		 */
		public void send() {
			if (this.result.isEmpty()) {
				this.writer.flush();
				this.writer.close();
			}

			ObjectWriter jsonWriter = Try.of(() ->
					new ObjectMapper().writer(new DefaultPrettyPrinter())
			).get();

			String json = Try.of(() ->
					this.result.size() == 1 ?
							jsonWriter.writeValueAsString(result.get(0)) :
							jsonWriter.writeValueAsString(result)
			).getOrElse(StringPool.EMPTY);

			this.writer.write(json);
			this.writer.flush();
			this.writer.close();
		}
	}
}
