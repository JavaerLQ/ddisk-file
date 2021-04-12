package io.ddisk.utils;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.ddisk.domain.dto.FileDTO;
import io.ddisk.domain.vo.LoginUser;
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
import java.util.Optional;

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
		FileUtils.chunkDownload(fileDTO);
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
