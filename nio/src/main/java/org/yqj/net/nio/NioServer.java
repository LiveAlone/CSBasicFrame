package org.yqj.net.nio;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/24
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class NioServer {

    public int workerCount = Runtime.getRuntime().availableProcessors();

    public int serverPort = 8888;

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    public NioServer() {
        try {
            this.selector = Selector.open();
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.socket().bind(new InetSocketAddress(serverPort), 1024);
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            log.info("nio server socket start with port :{}", this.serverPort);
        } catch (Exception e) {
            log.error("nio server socket open error, cause ", e);
        }
    }

    public void stop() {
        this.stop = true;
    }

}
