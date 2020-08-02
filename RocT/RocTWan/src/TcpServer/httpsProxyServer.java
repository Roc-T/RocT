package TcpServer;
import Handler.HttpsProxyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;


public class httpsProxyServer {
	private Channel channel;
    public synchronized void bind(int port) throws InterruptedException{
    	EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<Channel>() { 
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("httpCodec",new HttpServerCodec());
                            ch.pipeline().addLast("httpObject",new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("serverHandle",new HttpsProxyServerHandler());
                        }
                    });
            
            ChannelFuture f = b.bind(port).sync();
            channel = f.channel();
            f.channel().closeFuture().addListener((ChannelFutureListener) f2 -> {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
                
            });
        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();  
        }
        
    }
    public synchronized void close() {
        if (channel != null) {
            channel.close();
        }
    }
	
}
