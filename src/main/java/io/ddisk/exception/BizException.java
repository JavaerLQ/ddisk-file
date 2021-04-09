package io.ddisk.exception;

import io.ddisk.exception.msg.BizMessage;
import lombok.Getter;

/**
 * @author lee
 * @date 2021/1/31
 */
@Getter
public class BizException extends RuntimeException{


    /**
     * 响应码
     */
    private Integer code;

    public BizException(BizMessage baseMessage) {
        super(baseMessage.getMessage());
        this.code = baseMessage.getCode();
    }

    public BizException(BizMessage baseMessage, Throwable e) {
        super(baseMessage.getMessage(), e);
        this.code = baseMessage.getCode();
    }


    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(Integer code, String message, Throwable e) {
        super(message, e);
        this.code = code;
    }
}
