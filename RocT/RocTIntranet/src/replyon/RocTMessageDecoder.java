package replyon;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

public class RocTMessageDecoder extends MessageToMessageDecoder<ByteBuf>{
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {


		RocTMessage roctMessage = new RocTMessage();

		int type = msg.readInt();
		RocTMessageType roctMessageType = RocTMessageType.converTotype(type);
		roctMessage.setType(roctMessageType);

		int port = msg.readInt();
		roctMessage.setPort(port);

		int resultLength = msg.readInt();
		CharSequence result = msg.readCharSequence(resultLength, CharsetUtil.UTF_8);
		roctMessage.setResult(result.toString());

		int pwdLength = msg.readInt();
		CharSequence pwd = msg.readCharSequence(pwdLength, CharsetUtil.UTF_8);
		roctMessage.setPassword(pwd.toString());

		int reasonLength = msg.readInt();
		CharSequence reason = msg.readCharSequence(reasonLength, CharsetUtil.UTF_8);
		roctMessage.setReason(reason.toString());

		int ChannelIdLength = msg.readInt();
		CharSequence ChannelId = msg.readCharSequence(ChannelIdLength, CharsetUtil.UTF_8);
		roctMessage.setChannelId(ChannelId.toString());

		byte[] data = null;
		if (msg.isReadable()) {
			data = ByteBufUtil.getBytes(msg);
		}

		roctMessage.setData(data);

		out.add(roctMessage);
	}
}
