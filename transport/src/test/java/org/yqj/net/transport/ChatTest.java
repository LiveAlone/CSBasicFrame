package org.yqj.net.transport;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandler;
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
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yaoqijun.
 * Date:2016-02-05
 * Email:yaoqj@terminus.io
 * Descirbe:
 */
@Ignore
@Slf4j
public class ChatTest {

    @Test
    public void testClientFirst() {
        startClientName("ClientFirst");
    }

    @Test
    public void testClientSecond() {
        startClientName("ClientSecond");
    }

    public static void startClientName(String clientName) {
        NettyClient nettyClient = new NettyClient("localhost", 9001, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(1024)).addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder(Charsets.UTF_8)).addLast(new LineEncoder());
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        Thread.sleep((ThreadLocalRandom.current().nextInt(10) + 1) * 1000);
                                        ctx.writeAndFlush(String.format("client %s push message %d", clientName, System.currentTimeMillis()));
                                    }
                                } catch (Exception e) {
                                    log.error("chat client catch exception", e);
                                } finally {
                                    ctx.close();
                                }
                            }
                        }).start();
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

    @Test
    public void testServerBuildStart() {

        NettyServer nettyServer = new NettyServer(9001, new ChannelInitializer<SocketChannel>() {

            private ChatServer chatServer = new ChatServer();

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(1024)).addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder(Charsets.UTF_8)).addLast(new LineEncoder());
                ch.pipeline().addLast(chatServer);
            }
        });
        nettyServer.start();
    }

    @ChannelHandler.Sharable
    public static class ChatServer extends ChannelInboundHandlerAdapter {

        private final Map<Integer, ChannelHandlerContext> channelHandlerContextMap = Maps.newConcurrentMap();

        private final AtomicInteger idx = new AtomicInteger(0);

        public ChatServer() {
            log.info("************** chart server started in");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            addChannelHandlerContext(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String msgContent = (String) msg;
            log.info("char server receive message: '{}'", msgContent);
            pushAllMessage(msgContent);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }

        public void addChannelHandlerContext(ChannelHandlerContext context) {
            Integer idxInteger = idx.incrementAndGet();
            log.info("client new connect is :{}", idxInteger);
            channelHandlerContextMap.put(idxInteger, context);
        }

        public void pushAllMessage(String message) {
            Iterator<Map.Entry<Integer, ChannelHandlerContext>> iterator = channelHandlerContextMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, ChannelHandlerContext> channelHandlerContextEntry = iterator.next();
                Integer idx = channelHandlerContextEntry.getKey();
                ChannelHandlerContext context = channelHandlerContextEntry.getValue();
                if (!context.channel().isActive()) {
                    log.info("chat server ids :{} closed removed", idx);
                    iterator.remove();
                }

                log.info("chat server try to push idx:{} message is '{}'", idx, message);
                context.writeAndFlush(message);
            }
        }
    }
}
