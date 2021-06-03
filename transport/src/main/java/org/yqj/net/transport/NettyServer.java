package org.yqj.net.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/6/2
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class NettyServer {

    private int port;

    private ChannelInitializer<SocketChannel> channelChannelInitializer;

    public NettyServer(int port, ChannelInitializer<SocketChannel> channelChannelInitializer) {
        this.port = port;
        this.channelChannelInitializer = channelChannelInitializer;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(channelChannelInitializer);
            ChannelFuture channelFuture = b.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty server start bind port:{} error: ", this.port, e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
