package org.myutiltest.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("all")
public class TcpServer {

	static ExecutorService executorService = Executors.newFixedThreadPool(5);

	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(8892);
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		while (true) {
			executorService.submit(new SockerHandler(serverSocket.accept()));
		}
	}

	static class ReadTask implements Runnable {

		InputStream inputStream = null;
		BlockingQueue<RpcResponse> Queue = null;

		@Override
		public void run() {
			while (true) {
				try {
					DataInputStream dataInputStream = new DataInputStream(inputStream);
					int len = dataInputStream.readInt();
					byte[] data = new byte[len];
					dataInputStream.readFully(data, 0, len);
					RpcRequest rpcRequest = (RpcRequest) TransportJDK.INSTANCE.decode(data);

					executorService.submit(new Runnable() {

						@Override
						public void run() {
							try {
								RpcResponse rpcResponse = new RpcResponse();
								rpcResponse.setRequestId(rpcRequest.getRequestId());
								String className = rpcRequest.getClazz().getName() + "Impl";
								Object object = Class.forName(className).newInstance();
								rpcResponse.setResult(object.getClass()
										.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes())
										.invoke(object, rpcRequest.getArgs()));
								Queue.add(rpcResponse);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static class WriteTask implements Runnable {

		OutputStream outputStream = null;

		BlockingQueue<RpcResponse> Queue = null;

		@Override
		public void run() {
			while (true) {
				try {
					RpcResponse rpcResponse = Queue.take();
					byte[] outData = TransportJDK.INSTANCE.encode(rpcResponse);
					DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
					dataOutputStream.writeInt(outData.length);
					dataOutputStream.write(outData);
					dataOutputStream.flush();
					outputStream.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	static class SockerHandler implements Runnable {

		Socket socket;

		public SockerHandler(Socket socket) {
			super();
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				WriteTask writeTask = new WriteTask();
				ReadTask readTask = new ReadTask();
				BlockingQueue<RpcResponse> queue = new ArrayBlockingQueue<>(100000);
				writeTask.outputStream = socket.getOutputStream();
				writeTask.Queue = queue;
				readTask.inputStream = socket.getInputStream();
				readTask.Queue = queue;

				new Thread(writeTask).start();
				new Thread(readTask).start();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
