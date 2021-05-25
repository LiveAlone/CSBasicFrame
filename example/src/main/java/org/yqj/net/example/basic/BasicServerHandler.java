package org.yqj.net.example.basic;

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
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
        log.info("server channel read thread is :{}", Thread.currentThread().getName());
        log.info("basic server gain request content is :{}", body);
        ByteBuf resp = Unpooled.copiedBuffer(String.format("%s \n", String.valueOf(System.currentTimeMillis())).getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("server read complete read thread is :{}", Thread.currentThread().getName());
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
