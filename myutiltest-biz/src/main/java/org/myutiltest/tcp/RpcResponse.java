package org.myutiltest.tcp;

import java.io.Serializable;

public class RpcResponse implements Serializable {

	private static final long serialVersionUID = -2937737554205323292L;
	
	private String requestId;
	private Throwable throwable;
	private Object result;
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
}
