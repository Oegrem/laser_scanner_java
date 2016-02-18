package tcp_server;

import java.io.Serializable;

public class TransmissionObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int id = -1;

	public Object data;

	public TransmissionObject(int _id) {
		id = _id;
		data = null;
	}

	public TransmissionObject(int _id, Object _data) {
		id = _id;
		data = _data;
	}

}
