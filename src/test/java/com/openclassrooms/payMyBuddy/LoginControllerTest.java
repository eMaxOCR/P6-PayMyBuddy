package com.openclassrooms.payMyBuddy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org. springframework. test.web.servlet. request.MockMvcRequestBuilders.get;
import static org.springframework. test.web.servlet. result.MockMvcResultHandlers.print;
import static org. springframework. test.web. servlet. result. MockMvcResultMatchers. status;
import org.junit.jupiter.api.Test;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;


@SpringBootTest
@AutoConfigureMockMvc //Used to simulate web application and execute "http" requests.
public class LoginControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	/**
	 * Check if login page
	 * */
	@Test 
	public void shouldReturnDefaultMessageTest() throws Exception {
		mockMvc.perform(get("/login"))
			.andDo(print())
			.andExpect(status().isOk());
	}
	
	/**
	 * With wrong password
	 * */
	@Test 
	public void userLoginFailedTest() throws Exception {
		mockMvc.perform(formLogin("/login")
				.user("user")
				.password("wrongpassword"))
				.andExpect(unauthenticated());

	}
	

}
