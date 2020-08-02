package relyOn;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class RocTMessageEncoder extends MessageToByteEncoder<RocTMessage>{
		
	 	@Override
	 	protected void encode(ChannelHandlerContext ctx, RocTMessage msg, ByteBuf out) throws Exception {
	 		//字节数组缓冲区
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        try( DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);){
	        	//在缓冲区中写入RocMessage的类型
	            RocTMessageType roctmessageType = msg.getType();
	            dataOutputStream.writeInt(roctmessageType.getCode());
	            dataOutputStream.writeInt(msg.getPort());
	            
	            byte[] resultBytes = msg.getResult().getBytes(CharsetUtil.UTF_8);
	            dataOutputStream.writeInt(resultBytes.length);
	            dataOutputStream.write(resultBytes);
	            
	            byte[] pwdBytes = msg.getPassword().getBytes(CharsetUtil.UTF_8);
	            dataOutputStream.writeInt(pwdBytes.length);
	            dataOutputStream.write(pwdBytes);
	            
	            byte[] reasonBytes = msg.getReason().getBytes(CharsetUtil.UTF_8);
	            dataOutputStream.writeInt(reasonBytes.length);
	            dataOutputStream.write(reasonBytes);
	            
	            byte[] ChannelIdBytes = msg.getChannelId().getBytes(CharsetUtil.UTF_8);
	            dataOutputStream.writeInt(ChannelIdBytes.length);
	            dataOutputStream.write(ChannelIdBytes);
	            
	            if (msg.getData() != null && msg.getData().length > 0) {
	                dataOutputStream.write(msg.getData());
	            }
	            
	            byte[] data = byteArrayOutputStream.toByteArray();
	            out.writeInt(data.length);
	            out.writeBytes(data);
	            
	        }
}
}
