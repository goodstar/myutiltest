package org.myutiltest.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public final class RpcProxy {

	static Socket socket;
	
	interface Hello {
		String sayHai(String hai);
	}
	

	public static void main(String[] args) throws Exception {
		socket = new Socket("127.0.0.1", 8891);
		
		Hello hello = clientProxy(Hello.class);
		String ret = hello.sayHai("hai");
		System.out.println(ret);
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						
						System.out.println("hahaha");
						InputStream inputStream = socket.getInputStream();
						DataInputStream dataInputStream = new DataInputStream(inputStream);
						int len = dataInputStream.readInt();
						byte[] data = new byte[len];
						dataInputStream.readFully(data, 0, len);
						RpcResponse rpcResponse = (RpcResponse) TransportJDK.INSTANCE.decode(data);
						RpcInvoker rpcInvoker = RpcInvoker.InvokerMaps.get(rpcResponse.getRequestId());
						if (rpcInvoker != null) {
							rpcInvoker.setResponse(rpcResponse);
							rpcInvoker.notifyReq();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		Thread.sleep(Integer.MAX_VALUE);
	}

	public static final <T> T clientProxy(Class<T> serviceClazz) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { serviceClazz }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						RpcRequest rpcRequest = new RpcRequest();
						rpcRequest.setArgs(args);
						rpcRequest.setClazz(serviceClazz);
						rpcRequest.setMethodName(method.getName());
						rpcRequest.setParameterTypes(method.getParameterTypes());
						rpcRequest.setRequestId(UUID.randomUUID().toString());

						// send tcp call
						byte[] data = TransportJDK.INSTANCE.encode(rpcRequest);
						DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
						dataOutputStream.writeInt(data.length);
						dataOutputStream.write(data);
						dataOutputStream.flush();
						
						RpcInvoker rpcInvoker = new RpcInvoker();
						RpcInvoker.InvokerMaps.put(rpcRequest.getRequestId(), rpcInvoker);
						RpcResponse rpcResponse = rpcInvoker.getResponse(20, TimeUnit.SECONDS);
						if (rpcResponse.getThrowable() != null) {
							throw rpcResponse.getThrowable();
						}
						return rpcResponse.getResult();
					}
				});
	}

}
