package com.xksh.common;

import com.xksh.common.serialization.JsonSerialization;
import com.xksh.common.serialization.User;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by aaron on 2017/9/29.
 */
public class Client {


    public static void main(String[] args) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new RequestHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect("127.0.0.1", 8888).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static class RequestHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf m = (ByteBuf) msg;
            byte[] result = new byte[m.readableBytes()];
            m.readBytes(result);
            JsonSerialization jsonSerialization = new JsonSerialization();
            ByteArrayInputStream input = new ByteArrayInputStream(result);
            System.out.println("客户端接收到:" + jsonSerialization.deserialize(User.class, input).toString());
            m.release();
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            User user = new User("Aaron Wang", 28);
            System.out.println("客户端传输:" + user.toString());
            JsonSerialization jsonSerialization = new JsonSerialization();
            ByteArrayOutputStream output = (ByteArrayOutputStream) jsonSerialization.serialize(user);
            byte[] bs = output.toByteArray();
            ByteBuf encoded = ctx.alloc().buffer(bs.length);
            encoded.writeBytes(bs);
            ctx.write(encoded);
            ctx.flush();
        }
    }
}
