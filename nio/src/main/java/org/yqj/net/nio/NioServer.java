package org.yqj.net.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/24
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class NioServer {

    public int serverPort = 8888;

    private final Selector selector;

    private volatile boolean stop;

    /**
     * 执行处理消息任务线程池
     */
    private final ExecutorService executorService;

    private Function<SelectionKey, AbstractNioServerTask> function;

    public NioServer(Function<SelectionKey, AbstractNioServerTask> fun) throws Exception {
        try {
            this.selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(serverPort), 1024);
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            log.info("nio server socket start with port :{}", this.serverPort);
        } catch (Exception e) {
            log.error("nio server socket open error, cause ", e);
            throw e;
        }
        executorService = NioThreadPoolFactory.buildTheadPool("nioServerWorker");
        this.function = fun;
    }

    public void start() {
        while (!this.stop) {
            try {
                this.selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey selectionKey = null;
                while (it.hasNext()) {
                    selectionKey = it.next();
                    it.remove();
                    try {
                        handleInput(selectionKey);
                    } catch (Exception e) {
                        log.error("selection key handle error cause: ", e);
                        // 异常状态尝试关闭
                        if (selectionKey != null) {
                            selectionKey.cancel();
                            if (selectionKey.channel() != null) {
                                selectionKey.channel().close();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("selector select keys error, cause: ", e);
            }
        }

        try {
            if (this.selector != null) {
                this.selector.close();
            }
        } catch (Exception e) {
            log.error("selector close error, cause: ", e);
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // accept 事件, 当前线程等待处理
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(this.selector, SelectionKey.OP_READ);
                log.info("server socket accept connection :{}", sc.socket().getRemoteSocketAddress().toString());
            }

            if (key.isReadable()) {
                executorService.submit(this.function.apply(key));
            }
        }
    }

    public void stop() {
        this.stop = true;
    }
}
