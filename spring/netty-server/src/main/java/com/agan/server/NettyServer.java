package com.agan.server;

import java.net.InetSocketAddress;

import lombok.extern.slf4j.Slf4j;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {


    /**
     * @Fields DELIMITER : 自定义分隔符，服务端和客户端要保持一致
     */
    public static final String DELIMITER = "@@";

    /**
     * @Fields boss : boss 线程组用于处理连接工作, 默认是系统CPU个数的两倍，也可以根据实际情况指定
     */
    private EventLoopGroup boss = new NioEventLoopGroup();

    /**
     * @Fields work : work 线程组用于数据处理, 默认是系统CPU个数的两倍，也可以根据实际情况指定
     */
    private EventLoopGroup work = new NioEventLoopGroup();

    /**
     * @Fields port : 监听端口
     */
    private Integer port = 8888;

    private NettyServerHandlerInitializer handlerInitializer = new NettyServerHandlerInitializer();

    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer();
        server.start();
    }

    /**
     */
    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, work)
                    // 指定Channel
                    .channel(NioServerSocketChannel.class)
                    // 使用指定的端口设置套接字地址
                    .localAddress(new InetSocketAddress(port))

                    // 服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                    .option(ChannelOption.SO_BACKLOG, 1024)

                    // 设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                    .childOption(ChannelOption.SO_KEEPALIVE, true)

                    // 将小的数据包包装成更大的帧进行传送，提高网络的负载,即TCP延迟传输
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(handlerInitializer);

            ChannelFuture future = bootstrap.bind().sync();

            if (future.isSuccess()) {
                System.out.println("启动 Netty Server...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            try {
//                boss.shutdownGracefully().sync();
//                work.shutdownGracefully().sync();
//                LOGGER.info("关闭Netty...");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }

    }


}
