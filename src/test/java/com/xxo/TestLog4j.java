package com.xxo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TestLog4j {

	private Logger logger = LoggerFactory.getLogger(TestLog4j.class) ;
	@Test
	public void testLog()  {

		String url = "http://item.jd.com/1748005.html" ;
		Random random = new Random();
		while(true){
			try {
				logger.info( "当前时间:{},URL:"+url+",响应时间:{}",
						System.currentTimeMillis() ,
						random.nextInt(10)+1);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void test1(){
		Random random = new Random();
		//while ( true )
			System.out.println(random.nextInt(10));
	}
}
