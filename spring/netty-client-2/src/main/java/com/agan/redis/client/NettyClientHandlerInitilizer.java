package com.agan.redis.client;


import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

@Component
public class NettyClientHandlerInitilizer extends ChannelInitializer<Channel> {

    @Autowired
    private NettyClientInHandler nettyClientInHandler;
    @Autowired
    private NettyClientOutHandler nettyClientOutHandler;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // 通过socketChannel去获得对应的管道
        ChannelPipeline channelPipeline = ch.pipeline();

        //往pipeline链中添加一个 以"@@"为结尾分割的 解码器
        ByteBuf buf = Unpooled.copiedBuffer(NettyClient.DELIMITER.getBytes());
        channelPipeline.addLast("framer", new DelimiterBasedFrameDecoder(1024*1024*2, buf));
        //往pipeline链中添加一个编码器,把字符串编码为字节流，由netty发出去
        channelPipeline.addLast("encoder",new StringEncoder(CharsetUtil.UTF_8));
        //往pipeline链中添加一个解码器,netty收到字节流后，解码为字符串
        channelPipeline.addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
        //往pipeline链中添加自定义的handler(发送消息处理类)
        channelPipeline.addLast(nettyClientOutHandler);
        //往pipeline链中添加自定义的handler(发送消息处理类)
        channelPipeline.addLast(nettyClientInHandler);
    }

}