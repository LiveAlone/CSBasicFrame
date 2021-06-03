package org.yqj.net.transport;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
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
public class SingleTest {

    @Test
    public void TestNone() {
        System.out.println("none");
    }

    @Test
    public void testClientBuildStart() {
        NettyClient nettyClient = new NettyClient("localhost", 9001, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel registered");
                        super.channelRegistered(ctx);
                    }

                    @Override
                    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel unregistered");
                        super.channelUnregistered(ctx);
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel active");
                        for (int i = 0; i < 100000; i++) {
                            ctx.write(Unpooled.copiedBuffer(" ABCDEFGHIJKLMNOPQRSTUVWXUZ ".getBytes()));
                        }
                        ctx.flush();
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel inactive");
                        super.channelInactive(ctx);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("************ channel read");

                        ByteBuf buf = (ByteBuf) msg;
                        byte[] req = new byte[buf.readableBytes()];
                        buf.readBytes(req);
                        log.info("client received data info is :{}", new String(req, Charsets.UTF_8));
                    }

                    @Override
                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel read complete");
                        super.channelReadComplete(ctx);
                    }

                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        log.info("************ user event triggered");
                        super.userEventTriggered(ctx, evt);
                    }

                    @Override
                    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ sever channel writable changed");
                        super.channelWritabilityChanged(ctx);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        log.info("************ server exception cached, cause: ", cause);
                        ctx.close();
                        super.exceptionCaught(ctx, cause);
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
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel registered");
                        super.channelRegistered(ctx);
                    }

                    @Override
                    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel unregistered");
                        super.channelUnregistered(ctx);
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel active");
                        super.channelActive(ctx);
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel inactive");
                        super.channelInactive(ctx);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("************ channel read");

                        ByteBuf buf = (ByteBuf) msg;
                        byte[] req = new byte[buf.readableBytes()];
                        buf.readBytes(req);
                        log.info("server read data info is :{}", new String(req, Charsets.UTF_8));

                        ByteBuf resp = Unpooled.copiedBuffer(String.valueOf(System.currentTimeMillis()).getBytes());
                        ctx.writeAndFlush(resp);
                    }

                    @Override
                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ channel read complete");
                    }

                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        log.info("************ user event triggered");
                        super.userEventTriggered(ctx, evt);
                    }

                    @Override
                    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
                        log.info("************ sever channel writable changed");
                        super.channelWritabilityChanged(ctx);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        log.info("************ server exception cached, cause: ", cause);
                        ctx.close();
                        super.exceptionCaught(ctx, cause);
                    }
                });
            }
        });
        nettyServer.start();
    }

}
