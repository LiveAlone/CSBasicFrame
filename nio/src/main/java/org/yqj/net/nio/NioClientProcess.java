package org.yqj.net.nio;

import java.nio.channels.SocketChannel;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/24
 * Email: yaoqijunmail@foxmail.com
 */
public interface NioClientProcess {

    /**
     * nio 链接处理方式
     * @param socketChannel
     * @throws Exception
     */
    void processSocketChannelConnected(SocketChannel socketChannel) throws Exception;


    /**
     * 数据读取异常
     * @param socketChannel
     * @throws Exception
     */
    void processSocketChanelRead(SocketChannel socketChannel) throws Exception;
}
