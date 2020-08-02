package TcpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcpServer {
	private Channel channel;
	

    public synchronized void bind(int port, ChannelInitializer<SocketChannel> channelInitializer) throws InterruptedException {
    	
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(port).sync();
            
            channel = future.channel();
            channel.closeFuture().addListener((ChannelFutureListener) f -> {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
                
            });
            future.addListener(fu -> {
                if (fu.isSuccess()) {
                    System.out.println("RocT-WAN start success!");
                } else {
                	System.out.println("RocT-WAN start fail! will close current service!");
                    System.exit(0);
                }
            });
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            throw e;
        }
    }

    public synchronized void close() {
        if (channel != null) {
            channel.close();
        }
    }
}
