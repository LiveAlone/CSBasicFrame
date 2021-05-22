package org.yqj.net.bio;

import java.net.Socket;

/**
 * Description: 单线程Socket 接口函数处理方式
 *
 * @author yaoqijun
 * @date 2021/5/22
 * Email: yaoqijunmail@foxmail.com
 */
public interface SocketHandler {

    /**
     * 处理Socket 执行上线文
     * @param socket
     * @throws Exception
     */
    void handle(Socket socket) throws Exception;
}
