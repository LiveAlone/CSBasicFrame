package org.yqj.net.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/24
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class NioClient {

    public String address = "localhost";

    public Integer port = 8888;

    private Selector selector;

    private SocketChannel socketChannel;

    private boolean stop;

    private AtomicInteger threadNumber;

    /**
     * 执行处理消息任务线程池
     */
    private ExecutorService executorService;

    private Function<SelectionKey, AbstractClientChannelHandler> function;

    public NioClient(Function<SelectionKey, AbstractClientChannelHandler> fun) throws Exception{
        this.function = fun;
        try {
            this.selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        }catch (Exception e){
            log.error("client socket channel create fail, cause: ", e);
            throw e;
        }

        threadNumber = new AtomicInteger(1);
        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r);
            thread.setName(String.format("client_process_work_thread_%d", threadNumber.getAndIncrement()));
            return thread;
        };
        executorService = new ThreadPoolExecutor(4,
                4, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(5000), threadFactory);
    }

    public void start() {
        try {
            if (doConnect()){

            }
        }catch (Exception e){
            log.error("client socket connect error, cause: ", e);
            return;
        }

        while (!stop){
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
            } catch (IOException e) {
                log.error("selector selectKeys error, cause: ", e);
                break;
            }
        }

        try {
            this.selector.close();
        }catch (Exception e){
            log.error("client close socket channel fail, cause: ", e);
        }
    }

    private void handleInput(SelectionKey key) throws Exception {
        if (key.isValid()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (socketChannel.finishConnect()) {
                    socketChannel.register(this.selector, SelectionKey.OP_READ);
                    // todo connection finish
                }else {
                    throw new RuntimeException("client connection not finish");
                }
            }
            if (key.isReadable()) {
                // todo readable content
            }
        }
    }

    private boolean doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress(address, port))) {
            socketChannel.register(this.selector, SelectionKey.OP_READ);
            return true;
        }else {
            socketChannel.register(this.selector, SelectionKey.OP_CONNECT);
            return false;
        }
    }
}
