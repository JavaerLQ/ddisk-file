package io.ddisk.exception.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lee
 * @date 2021/1/31
 */
@Getter
@AllArgsConstructor
public enum BizMessage implements BaseMessage {

    /** 系统通用异常 **/
    SYSTEM_EXCEPTION(1000, "系统异常"),
    SYSTEM_DAMAGED_BY_USER(1001, "请停止您的非法行为"),
    REQUEST_PARAMS_INVALIDATE(1002, "请求参数校验不通过"),

    /**
     * 文件上传，文件IO
     */
    UPLOAD_FILE_NULL(2000, "未包含文件上传域"),
    UPLOAD_FILE_GET_BYTES_FAIL(2001, "获取二进制文件失败"),
    DIR_CREATE_FAIL(2002, "磁盘目录创建失败"),
    FILE_DELETE_FAIL(2003, "磁盘文件删除失败"),
    FILE_DELETE_RECURSIVELY(2003, "磁盘文件递归删除失败"),
    UPLOAD_FILE_STREAM_FAIL(2004, "上传文件流获取失败"),
    FILE_COPY_FAIL(2005, "文件拷贝失败"),
    CHUNK_INCOMPLETE(2006, "上传切片不完整"),
    CHUNK_NOT_EXIST(2007, "上传切片不存在"),
    FILE_CREATE_FILE(2008, "文件创建失败"),
    FILE_MD5_COMPUTE_FAIL(2009, "文件MD5计算失败"),
    PATH_NOT_A_FILE(2010, "不是一个标准文件"),
    FILE_SIZE_GET_FAIL(2011, "获取文件大小失败"),
    FILE_MERGE_FAIL(2012, "文件合并失败"),
    FILE_NOT_EXIST(2013, "文件不存在"),
    FILE_ILLEGAL_ACCESS(2014, "文件非法访问"),
    FILE_DOWNLOAD_FAIL(2015, "文件下载失败"),
    DIR_CAN_NOT_DOWNLOAD(2016, "目录不能被下载"),
    FILE_MOVE_FAIL(2017, "文件重命名或移动失败"),
    FILE_NOT_IMAGE(2018, "非图片类型文件"),
    NOT_MEDIA_FILE(2019, "非媒体文件"),

    /**
     * 参数类错误
     */
    PARAM_FILE_TYPE_ERROR(3001, "请求文件类型错误"),



    /** 用户异常 **/
    USER_NOT_LOGIN(5000, "用户未登录"),
    USERNAME_EXIST(5001, "用户名已注册"),
    EMAIL_EXIST(5002, "邮箱已被占用"),
    EMAIL_NOT_EXIST(5003, "邮箱不存在"),
    PASSWORD_ERROR(5004, "密码错误"),
    USERNAME_NOT_EXIST(5005, "用户名不存在"),
    USER_LOGIN_ERROR(5006, "用户登录异常"),
    BAD_CREDENTIALS(5007, "用户名或密码错误"),
    ACCOUNT_LOCKED(5008, "账户已被锁定"),
    CREDENTIALS_EXPIRED(5009, "密码已过期"),
    ACCOUNT_EXPIRED(5010, "账户已过期"),
    ACCOUNT_DISABLED(5011, "账户已被禁用"),
    PASSWORD_RESET_FAIL(5012, "密码修改失败"),
    USER_NOT_EXIST(5013, "用户不存在"),
    PASSWORD_NO_MATCH(5014, "两次密码不匹配"),
    USER_TOKEN_VALID(5015, "令牌生效中"),
    USER_TOKEN_INVALID(5016, "令牌已失效"),
    USER_TOKEN_NOT_EXIST(5017, "用户令牌不存在"),

    /**
     * 用户文件
     */
    USED_STORAGE_OUT_OF_MAX(7000, "用户存储空间不足"),
    USER_DIR_NOT_EXIST(7001, "用户文件夹不存在"),
    USER_FILE_NOT_EXIST(7002, "用户文件不存在"),
    USER_FILE_NOT_DIR(7003, "该用户文件不是文件夹"),
    USER_FILE_NOT_ACCESS(7004, "用户文件不可访问"),

    ;
    private Integer code;
    private String message;
}
