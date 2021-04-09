package io.ddisk.exception.msg;

/**
 * @apiNote 响应消息接口。为了更好的分类，不会将所有的响应消息放在一个枚举类中。所以定义该接口，并
 *          将所有定义响应消息的枚举类都实现该接口。
 */
public interface BaseMessage {

    /**
     * 获取响应码
     * @return
     */
    Integer getCode();

    /**
     * 获取响应消息
     * @return
     */
    String getMessage();

}
