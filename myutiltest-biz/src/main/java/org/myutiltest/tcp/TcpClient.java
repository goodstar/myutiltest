package org.myutiltest.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public final class TcpClient {

	static Socket socket;

	static BlockingQueue<RpcRequest> queue = new LinkedBlockingQueue<>();

	public static void main(String[] args) throws Exception {
		socket = new Socket("127.0.0.1", 8892);

		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
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

		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						RpcRequest rpcRequest = queue.take();
						byte[] data = TransportJDK.INSTANCE.encode(rpcRequest);
						DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
						dataOutputStream.writeInt(data.length);
						dataOutputStream.write(data);
						dataOutputStream.flush();
						socket.getOutputStream().flush();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		Hello hello = clientProxy(Hello.class);

		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		for (int i = 0; i < 50; i++) {
			final int k = i;
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					String ret = hello.sayHai(k + "");
					System.out.println(ret);
				}
			});
		}

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

						queue.put(rpcRequest);

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
