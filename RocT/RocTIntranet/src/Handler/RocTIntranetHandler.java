package Handler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import TCPconnection.TcpConnection;
import UI.IntranetButtonListener;
import UI.IntranetProxyListener;
import UI.IntranetRun;
import replyon.RocTMessage;
import replyon.RocTMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.swing.*;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class RocTIntranetHandler extends CommonHandler{

    private boolean registered = false;
    private int httpsPort;
    private String password;
    public ConcurrentHashMap<Integer,String> remoteToProxy = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, CommonHandler> channelHandlerMap = new ConcurrentHashMap<>();
    private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    /**
     * 添加静态链表，用于储存results
     */
    private static LinkedList<String> results;
    private IntranetButtonListener clientButtonListener;
    public IntranetProxyListener clientProxyListener = new IntranetProxyListener();
    private IntranetRun clientRun;

    public RocTIntranetHandler(String password, LinkedList<String> resultsDemo, IntranetButtonListener clientButtonListener, IntranetProxyListener clientProxyListener,ConcurrentHashMap<Integer,String> remoteToProxy) {
        results = resultsDemo;
        this.password = password;
        this.clientButtonListener = clientButtonListener;
        this.clientProxyListener = clientProxyListener;
        this.remoteToProxy = remoteToProxy;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
        RocTMessage message  = new RocTMessage();
        message.setType(RocTMessageType.REGISTER);
        message.setPassword(password);
        ctx.writeAndFlush(message);
        //断线重连
        if(registered) {
            if(remoteToProxy.size()!=0) {
                for(Integer key : remoteToProxy.keySet()) {
                    reBind(key);
                }
            }
            setHttpsProxy(httpsPort);
        }

    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RocTMessage roctMessage = (RocTMessage) msg;

        if (roctMessage.getType() == RocTMessageType.REGISTER_RESULT) {
            processRegisterResult(roctMessage);
        }else if(roctMessage.getType() == RocTMessageType.BIND_RESULT) {
            processBindResult(roctMessage);
        }else if (roctMessage.getType() == RocTMessageType.CONNECTED) {
            processConnected(roctMessage);
        } else if (roctMessage.getType() == RocTMessageType.DISCONNECTED) {
            processDisconnected(roctMessage);
        } else if (roctMessage.getType() == RocTMessageType.DATA) {
            processData(roctMessage);
        }else if(roctMessage.getType() == RocTMessageType.PROXY_REGISTER_RESULT) {//
            processHttpsProxyResult(roctMessage);
        }
        else if (roctMessage.getType() == RocTMessageType.KEEPALIVE) {
            // 心跳包, 不处理
        } else {
            throw new Exception("Unknown type");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.close();
        results.add("😂Loss connection to server!");
        this.getCtx().channel().pipeline().remove(this);
    }

    public void processRegisterResult(RocTMessage msg) throws Exception {
        if(!registered) {
            if(msg.getResult().equals("false")) {
                //把所有的system.out.print都改为输出到UI的状态栏
                results.add("😂Fail to register : " + msg.getReason());
                JOptionPane.showMessageDialog(null,"😂Fail to register : " + msg.getReason());
            } else {
                registered = true;
                this.clientButtonListener.getjFrame().dispose();
                clientRun = new IntranetRun(results, this);
                clientRun.showUI();
                results.add("😁Register Success");
            }
        }
    }

    public void reBind(int remotePort) {
        RocTMessage message  = new RocTMessage();
        message.setType(RocTMessageType.BIND);
        message.setPort(remotePort);
        message.setReason(remoteToProxy.get(remotePort));
        ctx.writeAndFlush(message);
    }
    public void processBind(int remotePort,String proxyAdress,String proxyPort) {
        RocTMessage message  = new RocTMessage();
        message.setType(RocTMessageType.BIND);
        message.setPort(remotePort);
        message.setReason(proxyAdress+":"+ proxyPort);
        ctx.writeAndFlush(message);
        remoteToProxy.put(remotePort, proxyAdress+":"+ proxyPort);
        results.add("😁Begin to bind" + proxyAdress+":"+ proxyPort+" to " + remotePort);
    }

    public void processBindResult(RocTMessage msg) {
        if(msg.getResult().equals("false")) {
            results.add("😂Fail to bind : " + msg.getReason());
            JOptionPane.showMessageDialog(null,"😂Fail to bind : Address already in use: bind");
        }else {
            results.add("😄Bind success");
            JOptionPane.showMessageDialog(null,"😂Bind success");
            this.clientProxyListener.getjFrame().dispose();
        }
    }


    private void processConnected(RocTMessage msg) throws Exception {

        try {
            int remotePort = msg.getPort();
            String proxyAddress = remoteToProxy.get(remotePort);
            String proxyaddress = proxyAddress.split(":")[0];
            int proxyPort = Integer.valueOf(proxyAddress.split(":")[1]);
            RocTIntranetHandler thisHandler = this;
            TcpConnection proxyConnection = new TcpConnection();
            proxyConnection.connect(proxyaddress, proxyPort, new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ProxyHandler proxyHandler = new ProxyHandler(thisHandler,msg.getChannelId());
                    ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), proxyHandler);

                    channelHandlerMap.put(msg.getChannelId(), proxyHandler);
                    channelGroup.add(ch);
                }
            });
        } catch (Exception e) {
            RocTMessage message = new RocTMessage();
            message.setType(RocTMessageType.DISCONNECTED);
            message.setChannelId(msg.getChannelId());
            ctx.writeAndFlush(message);
            channelHandlerMap.remove(msg.getChannelId());
            throw e;
        }
    }

    private void processDisconnected(RocTMessage msg) {
        String channelId = msg.getChannelId();
        CommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            handler.getCtx().close();
            channelHandlerMap.remove(channelId);
        }
    }

    public void portDisconnected(int port) {
        RocTMessage msg = new RocTMessage();
        msg.setType(RocTMessageType.DISCONNECTED);
        msg.setPort(port);
        remoteToProxy.remove(port);
        ctx.writeAndFlush(msg);
    }

    private void processData(RocTMessage msg) {
        String channelId = msg.getChannelId();
        CommonHandler handler = channelHandlerMap.get(channelId);

        if (handler != null) {
            ChannelHandlerContext ctx = handler.getCtx();
            ctx.writeAndFlush(msg.getData());
        }
    }

    public void setHttpsProxy(int port) {
        httpsPort = port;
        RocTMessage msg = new RocTMessage();
        msg.setType(RocTMessageType.PROXY_REGISTER);
        msg.setPort(port);
        ctx.writeAndFlush(msg);
        results.add("😁Begin to start proxy server on " + port);
        this.clientRun.setHttpsPort(String.valueOf(port));
    }

    private void processHttpsProxyResult(RocTMessage msg) {
        if(msg.getResult().equals("false")) {
            results.add("😂Proxy false");
            JOptionPane.showMessageDialog(null,"😂RocT Https ProxyServer failed to start");

        }
        else {
            results.add("😄proxy success");
            JOptionPane.showMessageDialog(null,"😄RocT Https ProxyServer started on port " + msg.getPort());
            //输出注册成功
        }
    }

}