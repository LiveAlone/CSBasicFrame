package org.yqj.net.transport;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by yaoqijun.
 * Date:2016-02-05
 * Email:yaoqj@terminus.io
 * Descirbe:
 */
@Ignore
@Slf4j
public class LibTest {

    @Test
    public void TestNone() {
        System.out.println("none");
    }

    @Test
    public void testClientBuildStart() {
        NettyClient nettyClient = new NettyClient("localhost", 9001, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder())
                        .addLast(new StringEncoder(Charsets.UTF_8));
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        for (int i = 0; i < 1000; i++) {
                            ctx.write("Client Put Content \n");
                        }
                        ctx.flush();
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        String msgContent = (String) msg;
                        log.info("client received data info is :{}", msgContent);
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

    @Test
    public void testServerBuildStart() {

        NettyServer nettyServer = new NettyServer(9001, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder())
                        .addLast(new StringEncoder(Charsets.UTF_8));
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        String msgContent = (String) msg;
                        log.info("server read data info is :{}", msgContent);
                        ctx.writeAndFlush(System.currentTimeMillis() + "ms \n");
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        ctx.close();
                    }
                });
            }
        });
        nettyServer.start();
    }

}
