package replyon;

public enum RocTMessageType {

	REGISTER(1),
	REGISTER_RESULT(2),
	BIND(3),
	BIND_RESULT(4),
	CONNECTED(5),
	DISCONNECTED(6),
	DATA(7),
	KEEPALIVE(8),
	PROXY_REGISTER(9),
	PROXY_REGISTER_RESULT(10),
	ERROR(11);

	private int code;

	RocTMessageType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static RocTMessageType converTotype(int code) {
		for (RocTMessageType item : RocTMessageType.values()) {
			if (item.code == code) {
				return item;
			}
		}
		return ERROR;
	}
}
