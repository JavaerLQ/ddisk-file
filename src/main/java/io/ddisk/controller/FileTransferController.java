package io.ddisk.controller;

import io.ddisk.domain.dto.FileDTO;
import io.ddisk.domain.dto.FileUploadDTO;
import io.ddisk.domain.dto.MergeFileDTO;
import io.ddisk.domain.vo.LoginUser;
import io.ddisk.domain.vo.UploadFileVO;
import io.ddisk.service.FileService;
import io.ddisk.service.FileShareService;
import io.ddisk.utils.ResponseUtils;
import io.ddisk.utils.SpringWebUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jodd.bean.BeanCopy;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.Collection;


/**
 * @author lee
 * @date 2021/3/8
 */
@Slf4j
@Tag(name = "FileTransfer", description = "该接口为文件传输接口，主要用来做文件的上传和下载")
@RequestMapping("transfer")
@Validated
@RestController
public class FileTransferController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileShareService fileShareService;

    @Operation(summary = "极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false")
    @GetMapping("upload")
    @ResponseBody
    public ResponseEntity<UploadFileVO> uploadFileSpeed(@Validated FileUploadDTO fileUploadDto){
        LoginUser user = SpringWebUtils.requireLogin();
        UploadFileVO vo = fileService.speedUpload(user.getId(), fileUploadDto);
        return ResponseEntity.ok(vo);
    }


    @Operation(summary = "上传文件", description = "真正的上传文件接口")
    @PostMapping("upload")
    public ResponseEntity<UploadFileVO> uploadChunk(@Validated FileUploadDTO fileUploadDto) {

        LoginUser user = SpringWebUtils.requireLogin();
        Collection<Integer> uploaded = fileService.upload(user.getId(), fileUploadDto);
        return ResponseEntity.ok().body(UploadFileVO.builder().needMerge(uploaded.size()==fileUploadDto.getTotalChunks()).uploaded(uploaded).build());
    }

    @Operation(summary = "下载文件", description = "文件下载接口，保证文件安全，阻止非法用户下载")
    @GetMapping("download/{userFileId}")
    @Parameters({
            @Parameter(name = "userFileId", description = "用户文件id，需要登录用户的文件", required = true)
    })
    public void downloadFile(@PathVariable @NotBlank @Length(min = 32, max = 32) String userFileId){

        LoginUser user = SpringWebUtils.requireLogin();
        FileDTO fileDTO = fileService.getFileResource(user.getId(), user.getRole(), userFileId);
        ResponseUtils.sendFile(fileDTO);
    }

    @Operation(summary = "下载文件", description = "文件下载接口，保证文件安全，阻止非法用户下载")
    @GetMapping("/anonymous/download/{shareId}")
    @Parameters({
            @Parameter(name = "userFileId", description = "用户文件id，需要登录用户的文件", required = true)
    })
    public void anonymousDownloadFile(@PathVariable @NotBlank @Length(min = 32, max = 32) String shareId){

        FileDTO fileDTO = fileShareService.getFileResource(shareId);
        ResponseUtils.sendFile(fileDTO);
    }


    @Operation(summary = "获取略缩图", description = "略缩图获取，用于图片类型文件的略缩图和头像")
    @GetMapping("thumbnail/{userFileId}")
    @Parameters({
            @Parameter(name = "userFileId", description = "用户文件id，需要登录用户的文件", required = true)
    })
    public void thumbnail(@PathVariable @NotBlank @Length(min = 32, max = 32) String userFileId){

        LoginUser user = SpringWebUtils.requireLogin();
        FileDTO fileDTO = fileService.getThumbnail(user.getId(), userFileId);
        ResponseUtils.sendFile(fileDTO);
    }


    @Operation(summary = "合并", description = "合并上传的切片文件")
    @PostMapping("merge")
    public ResponseEntity<Void> mergeFile(@Validated @RequestBody MergeFileDTO mergeFileDTO) {

        LoginUser user = SpringWebUtils.requireLogin();
        fileService.mergeFile(mergeFileDTO);

        FileUploadDTO uploadDTO = new FileUploadDTO();
        BeanCopy.from(mergeFileDTO).to(uploadDTO).copy();

        fileService.speedUpload(user.getId(), uploadDTO);
        log.info("用户[{}], 合并[{}]文件", user.getUsername(), uploadDTO.getIdentifier());
        return ResponseEntity.ok().build();
    }

}
