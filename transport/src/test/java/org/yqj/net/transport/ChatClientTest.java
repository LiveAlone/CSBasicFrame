package org.yqj.net.transport;

import com.google.common.base.Charsets;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/6/3
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
@Ignore
public class ChatClientTest {

    public static void main(String[] args) {
        startClientMessagePushTest();
    }

    public static void startClientMessagePushTest() {
        NettyClient nettyClient = new NettyClient("localhost", 9001, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(1024)).addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder(Charsets.UTF_8)).addLast(new LineEncoder());
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        ctx.writeAndFlush("xiaohongshu");
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//                                try {
//                                    String readerStr = reader.readLine();
//                                    while (!"exit".equals(readerStr)) {
//                                        log.info("chat client try to push message :{}", readerStr);
//                                        ctx.writeAndFlush(readerStr);
//                                        readerStr = reader.readLine();
//                                    }
//                                }catch (Exception e){
//                                    log.error("chat client catch exception", e);
//                                }finally {
//                                    ctx.close();
//                                }
//                            }
//                        }).start();
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        String msgContent = (String) msg;
                        log.info("chat client received data info is :{}", msgContent);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        ctx.close();
                    }
                });
            }
        });
        nettyClient.start();
    }

}
