package org.yqj.net.aio;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
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
public class AioServer {

    public int port = 8888;

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public CountDownLatch countDownLatch;

    public AioServer() throws Exception {
        try {
            this.asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            this.asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
        } catch (Exception e) {
            log.error("async server socket channel create fail, cause: ", e);
            throw e;
        }
    }

    public void start() {
        countDownLatch = new CountDownLatch(1);
        doAccept();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("server aio interrupted exception, cause: ", e);
        }
    }

    private void doAccept() {
        asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }

    public static class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {

        @Override
        public void completed(AsynchronousSocketChannel result, AioServer attachment) {
            attachment.asynchronousServerSocketChannel.accept(attachment, this);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            result.read(byteBuffer, byteBuffer, new ReadCompletionHandler(result));
        }

        @Override
        public void failed(Throwable exc, AioServer attachment) {
            log.error("accept field cause: ", exc);
            attachment.countDownLatch.countDown();
        }
    }

    public static class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

        private AsynchronousSocketChannel channel;

        public ReadCompletionHandler(AsynchronousSocketChannel channel){
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            attachment.flip();
            byte[] body = new byte[attachment.remaining()];
            attachment.get(body);

            try {
                String req = new String(body, "UTF-8");
                System.out.println("time server receiver is "+ req);

                String currentTime = "this is current time";

                doWrite(currentTime);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void doWrite(String currentTime){
            byte[] bytes = currentTime.getBytes();

            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);

            byteBuffer.put(bytes);

            byteBuffer.flip();

            channel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (attachment.hasRemaining()){
                        channel.write(attachment, attachment, this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            try {
                this.channel.close();
            }catch (Exception e){
                log.error("channel read content error, cause: ", e);
            }
        }
    }
}
