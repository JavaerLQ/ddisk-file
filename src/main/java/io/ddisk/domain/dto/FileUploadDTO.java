package io.ddisk.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Schema(name = "上传文件DTO",required = true)
public class FileUploadDTO {

    @Schema(description = "文件夹ID")
    private Long folderId;

    @Schema(description = "上传时间")
    private Date uploadTime;

    @NotBlank
    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "扩展名")
    private String extension;

    @Min(0)
    @Schema(description = "切片数量")
    private Integer chunkNumber;

    @Min(0)
    @Schema(description = "切片大小")
    private Long chunkSize;

    @Min(0)
    @Schema(description = "所有切片")
    private Integer totalChunks;

    @Min(0)
    @Schema(description = "总大小")
    private Long totalSize;

    @Min(0)
    @Schema(description = "当前切片大小")
    private Long currentChunkSize;

    @NotBlank
    @Schema(description = "md5码")
    private String identifier;

    @Schema(description = "真正的文件")
    private MultipartFile file;
}
