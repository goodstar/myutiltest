package org.myutiltest.biz;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws Exception {
		try {
			File parent = new File("/Users/lixing/books/");
			File[] arr = parent.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".txt");
				}
			});

			for (File tmp : arr) {
				System.out.println(tmp.getAbsolutePath());
				System.out.println("===========start============");
				String content = IOUtils.toString(new FileInputStream(tmp));
				System.out.println(content.substring(0, content.indexOf("START")));
				System.out.println("===========end=============");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
