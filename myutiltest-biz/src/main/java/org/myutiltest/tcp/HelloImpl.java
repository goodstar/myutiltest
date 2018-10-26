package org.myutiltest.tcp;

import scala.util.Random;

public class HelloImpl implements Hello {

	@Override
	public String sayHai(String hai) {
		try {
			Thread.sleep(new Random().nextInt(1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "fuck:" + hai;
	}

}
