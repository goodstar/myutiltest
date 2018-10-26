package org.myutiltest.tcp;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

public class RpcInvoker {

	public static Map<String, RpcInvoker> InvokerMaps = Maps.newConcurrentMap();

	private volatile RpcResponse response;

	private CountDownLatch latch = new CountDownLatch(1);

	public void notifyReq() {
		latch.countDown();
	}
	
	public static void put(String requestId, RpcInvoker rpcInvoker) {
		InvokerMaps.put(requestId, rpcInvoker);
	}

	public RpcResponse getResponse(long timeout, TimeUnit unit) {
		try {
			latch.await(timeout, unit);
		} catch (InterruptedException e) {
			// ignored
		}
		return response;
	}

	public void setResponse(RpcResponse response) {
		this.response = response;
	}

}
