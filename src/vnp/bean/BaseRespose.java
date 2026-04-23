package vnp.bean;

import java.io.Serializable;

public class BaseRespose implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String status;

	public BaseRespose(String status) {
		super();
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
