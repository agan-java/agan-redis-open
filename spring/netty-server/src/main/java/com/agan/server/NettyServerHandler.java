package com.agan.server;


import java.util.concurrent.atomic.AtomicInteger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable //标注一个channel handler可以被多个channel安全地共享
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {


    public static AtomicInteger nConnection = new AtomicInteger(0);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 收到消息直接打印输出
        System.out.println("收到客户端："+ctx.channel().remoteAddress() + "的内容 : " + msg);
        // 返回客户端消息 - 我已经接收到了你的消息
        ackMessage(ctx, msg);
    }

    /**
     *确认消息
     */
    public void ackMessage(ChannelHandlerContext ctx, String message) {
        //自定义分隔符
        String msg = message+NettyServer.DELIMITER;
        //回应客户端
        ctx.writeAndFlush(msg);
    }

    /**
     * TCP连接成功触发这里
     *每次来一个新连接就对连接数加一
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        nConnection.incrementAndGet();
        System.out.println("请求连接..."+ctx.channel().id()+"，当前连接数: ："+nConnection.get());
    }

    /**
     *每次与服务器断开的时候，连接数减一
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        nConnection.decrementAndGet();
        System.out.println("断开连接...当前连接数: ："+  nConnection.get());
    }


    /**
     *连接异常的时候回调
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        // 打印错误日志
        cause.printStackTrace();
        Channel channel = ctx.channel();

        if(channel.isActive()){
            ctx.close();
        }
    }

}