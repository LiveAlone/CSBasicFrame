package org.yqj.net.nio;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by yaoqijun.
 * Date:2016-02-05
 * Email:yaoqj@terminus.io
 * Descirbe:
 */
@Slf4j
@Ignore
public class NioTest {

    @Test
    public void nioServerStart() throws Exception {
        NioServer nioServer = new NioServer(selectionKey -> new NioServerTask(selectionKey) {
            @Override
            public void processSocketChannel(SocketChannel sc, SelectionKey sk) throws Exception {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(byteBuffer);
                if (readBytes > 0) {
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    log.info("server gain data content from :{} content:{}", sc.socket().getRemoteSocketAddress().toString(), new String(bytes, Charsets.UTF_8));

                    String response = String.format("%d \n", System.currentTimeMillis());
                    ByteBuffer byteBufferResponse = ByteBuffer.allocate(response.getBytes().length);
                    byteBufferResponse.put(response.getBytes());
                    byteBufferResponse.flip();
                    sc.write(byteBufferResponse);
                }else if (readBytes < 0){
                    log.info("server close closed socket :{}", sc.socket().getRemoteSocketAddress().toString());
                    sk.cancel();
                    sc.close();
                }else {
                    log.info("server do none data content");
                }
            }
        });
        nioServer.start();
    }

    @Test
    public void nioClientStart() throws Exception {
        new NioClient().start();
    }
}
