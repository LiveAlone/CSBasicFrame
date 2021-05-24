package org.yqj.net.nio;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

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

    public NioClient() throws Exception {
        try {
            this.selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (Exception e) {
            log.error("client socket channel create fail, cause: ", e);
            throw e;
        }
    }

    public void start() {
        try {
            doConnect();
        } catch (Exception e) {
            log.error("client socket connect error, cause: ", e);
            return;
        }

        while (!stop) {
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
        } catch (Exception e) {
            log.error("client close socket channel fail, cause: ", e);
        }
    }

    private void handleInput(SelectionKey key) throws Exception {
        if (key.isValid()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (socketChannel.finishConnect()) {
                    socketChannel.register(this.selector, SelectionKey.OP_READ);
                    doWrite(socketChannel);
                } else {
                    throw new RuntimeException("client connection not finish");
                }
            }
            if (key.isReadable()) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(byteBuffer);
                if (readBytes > 0) {
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    log.info("client gain data content from :{} content:{}", socketChannel.socket().getRemoteSocketAddress().toString(), new String(bytes, Charsets.UTF_8));
                } else if (readBytes < 0) {
                    log.info("client socket channel closed :{}", socketChannel.socket().getRemoteSocketAddress().toString());
                    key.cancel();
                    socketChannel.close();
                } else {
                    log.info("socket channel read content empty ignore");
                }
            }
        }
    }

    private void doWrite(SocketChannel sc) throws Exception {
        byte[] requestBody = "nio client request call".getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(requestBody.length);
        byteBuffer.put(requestBody);
        byteBuffer.flip();
        sc.write(byteBuffer);
        if (!byteBuffer.hasRemaining()) {
            log.info("write buffer success content");
        }
    }

    private void doConnect() throws Exception {
        if (socketChannel.connect(new InetSocketAddress(address, port))) {
            socketChannel.register(this.selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            socketChannel.register(this.selector, SelectionKey.OP_CONNECT);
        }
    }
}
