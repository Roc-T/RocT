package relyOn;

public class RocTMessage {
	private RocTMessageType type;
	private int port;
    private String pwd;
    private String result;
    private String reason;
    private String ChannelId;
    private byte[] data;

    public RocTMessage(){
    	type = RocTMessageType.DISCONNECTED;
    	port = 0;
    	pwd ="null";
    	result ="null";
    	reason = "null";
    	ChannelId  ="null";
    }
    public RocTMessageType getType() {
        return type;
    }

    public void setType(RocTMessageType type) {
        this.type = type;
    }
    public void setPort(int port) {
    	this.port = port;
    }
    public int getPort() {
    	return port;
    }
    public void setPassword(String pwd) {
    	this.pwd = pwd;
    }
    public String getPassword() {
    	return pwd;
    }
    public void setResult(String flag) {
    	this.result = flag;
    }
    public String getResult() {
    	return result;
    }
    
    public void setReason(String reason) {
    	this.reason = reason;
    }
    public String getReason() {
    	return reason;
    }
    public void setChannelId(String ChannelId) {
    	this.ChannelId = ChannelId;
    }
    public String getChannelId() {
    	return ChannelId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
