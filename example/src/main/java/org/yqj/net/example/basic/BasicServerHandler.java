package org.yqj.net.transport.basic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/25
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class BasicServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        log.info("basic server gain request, thead:{} content is :{}", Thread.currentThread().getName(), body);
        ByteBuf resp = Unpooled.copiedBuffer(String.format("%s $_", String.valueOf(System.currentTimeMillis())).getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("server read complete read thread is :{}", Thread.currentThread().getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
