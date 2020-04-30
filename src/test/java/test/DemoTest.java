package test;

import org.apache.log4j.Logger;
import org.junit.Test;

public class DemoTest {
	
	private  static Logger logger = Logger.getLogger(DemoTest.class);

	public static void main (String[] args) {
		logger.info("************ 日志配置成功   ***********");
	}
	
	@Test
	public void junitTest() {
		logger.info("************ Junit测试成功  ***********");
	}
}
