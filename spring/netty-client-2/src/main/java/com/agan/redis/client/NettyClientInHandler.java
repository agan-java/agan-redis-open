package com.agan.redis.client;


import java.util.concurrent.TimeUnit;

import com.agan.redis.service.NettyClientService;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;

@Slf4j
@Component
@ChannelHandler.Sharable // 标注一个channel handler可以被多个channel安全地共享
public class NettyClientInHandler extends SimpleChannelInboundHandler<String> {

    @Autowired
    private NettyClient nettyClient;

    @Autowired
    private NettyClientService service;

    /**
     * 服务端发生消息给客户端，会触发该方法进行接收消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("收到服务端内容 : " + msg);
        this.service.ackSyncMsg(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("请求连接成功...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接被断开...");
        // 使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                // 重连
                nettyClient.start();
            }
        }, 20, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    /**
     * 处理异常, 一般将实现异常处理逻辑的Handler放在ChannelPipeline的最后
     * 这样确保所有入站消息都总是被处理，无论它们发生在什么位置，下面只是简单的关闭Channel并打印异常信息
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }

}
