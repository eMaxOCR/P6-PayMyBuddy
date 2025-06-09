package com.openclassrooms.payMyBuddy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.openclassrooms.payMyBuddy.controllers.LoginController;

@SpringBootTest
class PayMyBuddyApplicationTests {
	
	@Autowired
	private LoginController logingController;

	@Test //Testing Login controller
	void contextLoads() {
		assertThat(logingController).isNotNull();
	}

}
