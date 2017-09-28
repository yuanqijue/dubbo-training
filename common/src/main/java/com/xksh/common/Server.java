package com.xksh.common;


import com.xksh.common.serialization.JsonSerialization;
import com.xksh.common.serialization.User;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by aaron on 2017/9/28.
 */
public class Server {

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ResponseHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(8888).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static class ResponseHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("channelRead...");

            ByteBuf m = (ByteBuf) msg;
            byte[] result = new byte[m.readableBytes()];
            // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
            m.readBytes(result);

            JsonSerialization jsonSerialization = new JsonSerialization();
            ByteArrayInputStream input = new ByteArrayInputStream(result);
            System.out.println("服务器端接收到:" + jsonSerialization.deserialize(User.class, input).toString());
            m.release();
            User user = new User("Joseph Zhang", 28);
            System.out.println("服务器端传输:" + user.toString());

            ByteArrayOutputStream output = (ByteArrayOutputStream) jsonSerialization.serialize(user);
            byte[] bs = output.toByteArray();
            ByteBuf encoded = ctx.alloc().buffer(bs.length);
            encoded.writeBytes(bs);
            ctx.write(encoded);
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
