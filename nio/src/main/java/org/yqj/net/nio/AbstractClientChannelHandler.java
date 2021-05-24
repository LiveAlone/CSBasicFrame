package org.yqj.net.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/24
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public abstract class AbstractClientChannelHandler implements Runnable {

    private SelectionKey selectionKey;

    private SocketChannel socketChannel;

    public AbstractClientChannelHandler(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
        this.socketChannel = (SocketChannel) selectionKey.channel();
    }

    @Override
    public void run() {
        try {
            processChannelReader(selectionKey, socketChannel);
        } catch (Exception e) {
            log.error("client process socket channel fail, cause: ", e);
            try {
                selectionKey.cancel();
                socketChannel.close();
            } catch (IOException ioException) {
                log.error("ignore client channel close error, cause: ", e);
            }
        }
    }

    /**
     * process channel condition
     * @param selectionKey
     * @param socketChannel
     */
    public abstract void processChannelReader(SelectionKey selectionKey, SocketChannel socketChannel);
}
