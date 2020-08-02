package Handler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import TcpServer.TcpServer;
import TcpServer.httpsProxyServer;
import relyOn.RocTMessage;
import relyOn.RocTMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;


public class RocTWanHandler extends CommonHandler {

    static LinkedList<RocTWanHandler> HandlerList;
    public static ConcurrentHashMap<Integer,String> remoteToProxy = new ConcurrentHashMap<>();
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static LinkedList<Integer> httpsPortList = new LinkedList<>();

    private int num;
    private String password;
    private boolean registered = false;
    private int httpsPort;
    private httpsProxyServer httpsServer;
    private ConcurrentHashMap<Integer, TcpServer> remoteServers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, SocketChannel> channelMap = new ConcurrentHashMap<>();


    /**
     * 添加静态链表，用于储存results
     */
    private static LinkedList<String> results;

    public RocTWanHandler(String password, LinkedList<String> resultsDemo,int num,LinkedList<RocTWanHandler> HandlerListDemo) {
        this.password = password;
        this.num = num;
        results = resultsDemo;
        HandlerList = HandlerListDemo;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RocTMessage roctMessage = (RocTMessage) msg;

        if (roctMessage.getType() == RocTMessageType.REGISTER) {
            processRegister(roctMessage);
        }
        else if (registered) {
            if (roctMessage.getType() == RocTMessageType.DISCONNECTED) {
                processDisconnected(roctMessage);
            }else if(roctMessage.getType() == RocTMessageType.BIND){
                processBind(roctMessage);
            }else if (roctMessage.getType() == RocTMessageType.DATA) {
                processData(roctMessage);
            } else if (roctMessage.getType() == RocTMessageType.PROXY_REGISTER) {
                processProxyRegiseter(roctMessage);
            }else if (roctMessage.getType() == RocTMessageType.KEEPALIVE) {
                // 心跳包, 不处理
            } else {
                throw new Exception("Unknown type ");
            }
        } else {
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //关闭所有remotePort
        if (registered) {
            for(Integer key : remoteServers.keySet()) {
                remoteServers.get(key).close();
                remoteToProxy.remove(key);
                results.add("Client "+ num +": 😂Stop server on port: " + key);
            }
            if(httpsServer != null) {
                httpsServer.close();
                httpsPortList.remove(httpsPortList.indexOf(httpsPort));
                results.add("Client "+ num +": 😂Stop httpsServer on port: " + httpsPort);
            }
        }
        HandlerList.remove(this);
    }

    private void processRegister(RocTMessage roctMessage) {
        RocTMessage reply = new RocTMessage();
        if(!registered) {
            if (this.password != null && !password.equals(roctMessage.getPassword())) {
                reply.setResult("false");
                reply.setReason("password is wrong");
            } else {
                registered = true;
                reply.setResult("true");
                results.add("Client "+ num +": 😄Register success");
            }

        }else {
            reply.setResult("true");
        }
        reply.setType(RocTMessageType.REGISTER_RESULT);
        ctx.writeAndFlush(reply);
    }
    private void processBind(RocTMessage roctMessage) {
        RocTMessage reply = new RocTMessage();
        int port = roctMessage.getPort();

        String address = roctMessage.getReason();//地址存在了reason中
        remoteToProxy.put(port,address);//proxyAddress:proxyPort -> remotePort

        try {
            TcpServer remoteServer = new TcpServer();
            RocTWanHandler thisHandler = this;

            remoteServer.bind(port, new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    WanProxyHandler proxyServerHandler = new WanProxyHandler(thisHandler,port);
                    ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), proxyServerHandler);

                    channelMap.put(ch.id().asLongText(),ch);
                    channels.add(ch);
                }
            });
            remoteServers.put(port, remoteServer);
            reply.setResult("true");

            results.add("Client "+ num +": 😄Bind success, start server on port: " + port);
        } catch (Exception e) {
            reply.setResult("false");
            reply.setReason("Address is already binded");
            results.add("Client "+ num +": 😂Fail to bind");
            e.printStackTrace();
        }
        reply.setType(RocTMessageType.BIND_RESULT);
        ctx.writeAndFlush(reply);
    }


    private void processData(RocTMessage roctMessage) {
        channelMap.get(roctMessage.getChannelId()).writeAndFlush(roctMessage.getData());
    }

    private void processDisconnected(RocTMessage roctMessage) {
        if(roctMessage.getPort()== 0) {
            channels.close(channel -> channel.id().asLongText().equals(roctMessage.getChannelId()));
            if(channelMap.get(roctMessage.getChannelId()) != null)
                channelMap.remove(roctMessage.getChannelId());
        }else {
            remoteServers.get(roctMessage.getPort()).close();
            remoteServers.remove(roctMessage.getPort());
            remoteToProxy.remove(roctMessage.getPort());
            results.add("Client "+ num +": 😂Stop proxy server on port: " + roctMessage.getPort());
        }

    }

    private void processProxyRegiseter(RocTMessage roctMessage) {
        httpsPort = roctMessage.getPort();
        RocTMessage msg = new RocTMessage();
        msg.setType(RocTMessageType.PROXY_REGISTER_RESULT);
        msg.setPort(httpsPort);
        try {
            httpsServer = new  httpsProxyServer();
            httpsServer.bind(httpsPort);
            httpsPortList.add(httpsPort);
            results.add("Client "+ num +": 😄https proxy server open on " + httpsPort);
            msg.setResult("true");
        }catch(Exception e) {
            msg.setResult("false");
            msg.setReason(e.getStackTrace().toString());
        }
        ctx.writeAndFlush(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            RocTMessage roctMessage = new RocTMessage();
            roctMessage.setType(RocTMessageType.KEEPALIVE);
            ctx.writeAndFlush(roctMessage);
            IdleStateEvent e = (IdleStateEvent) evt;
        }
    }

}
