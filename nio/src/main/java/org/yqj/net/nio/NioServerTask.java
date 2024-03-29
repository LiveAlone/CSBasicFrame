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
public abstract class NioServerTask implements Runnable {

    private SelectionKey selectionKey;

    private SocketChannel socketChannel;

    public NioServerTask(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
        this.socketChannel = (SocketChannel) selectionKey.channel();
    }

    @Override
    public void run() {
        log.info("socket channel try to process :{}, ops:{}, interestOps:{}",
                socketChannel.socket().getRemoteSocketAddress().toString(),
                selectionKey.readyOps(), selectionKey.interestOps());
        try {
            processSocketChannel(this.socketChannel, this.selectionKey);
        } catch (Exception e) {
            log.info("process client socket channel fail, cause: ", e);
            try {
                closeChannel();
            } catch (IOException ioException) {
                log.error("server close socket channel error, ignore cause: ", e);
            }
        }
    }

    protected void closeChannel() throws IOException {
        this.selectionKey.cancel();
        this.socketChannel.close();
    }

    /**
     * socket channel 进行数据读写处理等等
     * @param socketChannel
     * @param selectionKey
     * @throws Exception
     */
    public abstract void processSocketChannel(SocketChannel socketChannel, SelectionKey selectionKey) throws Exception;
}
