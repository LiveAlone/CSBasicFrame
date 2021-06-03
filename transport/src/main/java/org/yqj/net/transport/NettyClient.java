package org.yqj.net.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/6/2
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class NettyClient {

    private String host;

    private int port;

    private ChannelInitializer<SocketChannel> channelChannelInitializer;

    public NettyClient(String host, int port, ChannelInitializer<SocketChannel> channelChannelInitializer) {
        this.host = host;
        this.port = port;
        this.channelChannelInitializer = channelChannelInitializer;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(channelChannelInitializer);
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("client server connect error, cause: ", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
