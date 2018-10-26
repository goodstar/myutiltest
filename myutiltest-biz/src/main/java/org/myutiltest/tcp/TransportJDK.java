package org.myutiltest.tcp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TransportJDK implements Transport {

	public static TransportJDK INSTANCE = new TransportJDK();

	@Override
	public byte[] encode(Object object) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.flush();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public Object decode(byte[] data) {
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return objectInputStream.readObject();
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	}

}
