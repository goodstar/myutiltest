package org.myutiltest.tcp;

public interface Transport {

	byte[] encode(Object object);
	
	Object decode(byte[] data);
}
