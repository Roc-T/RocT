package Handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpsProxyServerHandler extends ChannelInboundHandlerAdapter {

 	private ChannelFuture cf;
    private String host;
    private int port;
    private ChannelHandlerContext clientCtx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	this.clientCtx = ctx;
    }
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
    	if (msg instanceof FullHttpRequest) {
    		//System.out.println(msg);
            FullHttpRequest request = (FullHttpRequest) msg;
            String host = request.headers().get("host");
            String[] temp = host.split(":");
            this.host = temp[0];
            if (temp.length > 1) {
                port = Integer.parseInt(temp[1]);
            } else {
                if (request.uri().indexOf("https") == 0) {
                    port = 443;
                }
                else {
                	port = 80;
                }
            }
            
            if ("CONNECT".equalsIgnoreCase(request.method().name())) {//HTTPS建立代理握手
                HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                //回发数据
                ctx.writeAndFlush(response);
                ctx.pipeline().remove("httpCodec");
                ctx.pipeline().remove("httpObject");
                return;
            }
            try {
            	Bootstrap bootstrap = new Bootstrap();
	            bootstrap.group(ctx.channel().eventLoop()) // 注册线程池
	                    .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
	                    .handler(new ChannelInitializer<Channel>() {

	                    	@Override
	                        protected void initChannel(Channel ch) throws Exception {
	                            ch.pipeline().addLast(new HttpClientCodec(),new HttpObjectAggregator(6553600));
	                            ch.pipeline().addLast(
	                            		new ChannelInboundHandlerAdapter() {
	                            			@Override
	                            		    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	                            		        FullHttpResponse response = (FullHttpResponse) msg;
	                            		        clientCtx.channel().writeAndFlush(msg);
	                            		    }
	                            			@Override
	                            	    	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	                            	        	System.out.println("远程主机强迫关闭了一个现有的连接。");
	                            	    	}
		                               
	                            		});	
	                        }
	                    });
	            ChannelFuture cf = bootstrap.connect(host, port);
	            
	            //成功之后写入数据
	            cf.addListener(new ChannelFutureListener() {
	                public void operationComplete(ChannelFuture future) throws Exception {
	                    if (future.isSuccess()) {
	                        future.channel().writeAndFlush(msg);
	                    } else {
	                        ctx.channel().close();
	                    }
	                }
	            });
            }catch(Exception e) {
            	e.printStackTrace();
            }
            
            
        } else { 
            if (cf == null) {//https连接
            	try {
            		Bootstrap bootstrap = new Bootstrap();
	                bootstrap.group(ctx.channel().eventLoop()) 
	                        .channel(NioSocketChannel.class) 
	                        .handler(new ChannelInitializer() {
	                            @Override
	                            protected void initChannel(Channel ch) throws Exception {
	                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
	                                    @Override
	                                    public void channelRead(ChannelHandlerContext ctx0, Object msg) throws Exception {
	                                    	ctx.channel().writeAndFlush(msg);
	                                    }
	                                    @Override
	                                	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	                                    	System.out.println("远程主机强迫关闭了一个现有的连接。");
	                                	}
	                               
	                                });
	                            }   
	                        });
	                cf = bootstrap.connect(host, port);
	                cf.addListener(new ChannelFutureListener() {
	                    public void operationComplete(ChannelFuture future) throws Exception {
	                        if (future.isSuccess()) {
	                            future.channel().writeAndFlush(msg);
	                        } else {
	                            ctx.channel().close();
	                        }
	                    }
	                });
            	}catch(Exception e) {
	            	e.printStackTrace();
            	
	            }
                
            } else {
                cf.channel().writeAndFlush(msg);
            }
        }
    }
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	System.out.println("远程主机强迫关闭了一个现有的连接。");
	}
}
