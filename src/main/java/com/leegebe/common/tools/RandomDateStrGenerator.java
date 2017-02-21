package com.leegebe.common.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 * 随机日期字符串生成器
 */
public class RandomDateStrGenerator {
	
	private static final Random R = new Random();

	private static final String FORMAT = "yyyyMMddHHmmssSSS";
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);

	
	/**
	 * 生成随机字符串
	 * @param length
	 * @return
	 */
	public static String generate(int length){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < length; i ++){
			sb.append(R.nextInt(10));
		}
		return sb.toString();
	}

	public static String generateDateNumber(){
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String result=dateFormat.format(new Date());
		return result;
	}

}
