package Handler;

import replyon.RocTMessage;
import replyon.RocTMessageType;
import io.netty.channel.ChannelHandlerContext;
public class ProxyHandler extends CommonHandler{

    private CommonHandler clientHandler;
    private String serverChannelId;

    public ProxyHandler(CommonHandler proxyHandler, String serverChannelId) {
        this.clientHandler = proxyHandler;
        this.serverChannelId = serverChannelId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        RocTMessage message = new RocTMessage();
        message.setType(RocTMessageType.DATA);
        message.setData(data);
        message.setChannelId(serverChannelId);

        clientHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RocTMessage message = new RocTMessage();
        message.setType(RocTMessageType.DISCONNECTED);
        message.setChannelId(serverChannelId);
        clientHandler.getCtx().writeAndFlush(message);
    }
}
