package com.tuyu;

import com.tuyu.service.AService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LearnTxPropagationApplicationTests {

	@Autowired
	private AService aService;

	@Test
	public void contextLoads() {

		aService.a();
	}

}
