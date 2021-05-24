package org.yqj.net.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/24
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class AioClient implements CompletionHandler<Void, AioClient> {

    public String host = "localhost";

    public int port = 8888;

    private AsynchronousSocketChannel channel;

    private CountDownLatch latch;

    public AioClient() throws IOException {
        channel = AsynchronousSocketChannel.open();
    }

    public void start() {
        latch = new CountDownLatch(1);

        channel.connect(new InetSocketAddress(host, port), this, this);
        try {
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        try {
            channel.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, AioClient attachment) {
        byte[] writeString = "query time ordered".getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(writeString.length);
        byteBuffer.put(writeString);
        byteBuffer.flip();
        channel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (attachment.hasRemaining()){
                    channel.write(attachment, attachment, this);
                }else {
                    ByteBuffer reader = ByteBuffer.allocate(1024);
                    channel.read(reader, reader, new CompletionHandler<Integer, ByteBuffer>() {

                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            byte[] bytes = new byte[attachment.remaining()];
                            attachment.get(bytes);
                            String body;
                            try {
                                body = new String(bytes, "UTF-8");
                                System.out.println("body is " + body);
                                latch.countDown();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                channel.close();
                            }catch (IOException e){}finally {
                                latch.countDown();
                            }
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    channel.close();
                }catch (IOException e){}finally {
                    latch.countDown();
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, AioClient attachment) {
        log.error("aio client exec error cause: ", exc);
        try {
            channel.close();
        }catch (IOException e){
            log.error("channel close error, cause: ", e);
        }finally {
            latch.countDown();
        }
    }
}
