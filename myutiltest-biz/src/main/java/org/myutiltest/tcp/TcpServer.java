package org.myutiltest.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(8891);
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		while (true) {
			executorService.submit(new SockerHandler(serverSocket.accept()));
		}
	}

	static class SockerHandler implements Runnable {

		Socket socket;

		public SockerHandler(Socket socket) {
			super();
			this.socket = socket;
			try {
				socket.setTcpNoDelay(false);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					InputStream inputStream = socket.getInputStream();
					
					int av = inputStream.available();
					if (av == 0) {
						Thread.sleep(200);
						continue;
					}
					
					DataInputStream dataInputStream = new DataInputStream(inputStream);
					int len = dataInputStream.readInt();
					byte[] data = new byte[len];
					dataInputStream.readFully(data, 0, len);
					RpcRequest rpcRequest = (RpcRequest) TransportJDK.INSTANCE.decode(data);

					RpcResponse rpcResponse = new RpcResponse();
					rpcResponse.setRequestId(rpcRequest.getRequestId());
					rpcResponse.setResult("back:" + rpcRequest.getRequestId());

					System.out.println("back:" + rpcRequest.getRequestId());
					
					byte[] outData = TransportJDK.INSTANCE.encode(rpcResponse);

					OutputStream outputStream = socket.getOutputStream();
					DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
					dataOutputStream.writeInt(outData.length);
					dataOutputStream.write(outData);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
