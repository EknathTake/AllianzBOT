package com.allianzbot.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Testcases for the controller layer
 * 
 * @author eknath.take
 *
 */
@Ignore
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitWebConfig(locations = "classpath:test-servlet-context.xml")
public class AllianzBotControllerTest {

	@Autowired
	private AllianzBotController classUnderTest;

	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {

		mockMvc = MockMvcBuilders.standaloneSetup(classUnderTest).build();
	}

	@Test
	public void testSearchSolr_SelectAll() throws Exception {
		mockMvc.perform(get("/search/document?q=").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is("pqrst")))
				.andExpect(jsonPath("$[0].content", is("Demo text for Apache Tika\n")));
	}

	@Test
	public void testSearchSolr_ByUserQuery() throws Exception {
		mockMvc.perform(get("/search/document?q=Demo+text+for+Apache+Tika\n")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$[0].id", is("pqrst")))
				.andExpect(jsonPath("$[0].content", is("Demo text for Apache Tika\n")));
	}

	@Test @Ignore
	public void testUploadFile() throws Exception {
		MockMultipartFile mockMultipartFile = new MockMultipartFile("document", "ex1.txt",
				MediaType.TEXT_PLAIN_VALUE, new FileInputStream("src/test/resources/docs/ex1.txt"));

		mockMvc.perform(MockMvcRequestBuilders.multipart("/extract/document")
				.file(mockMultipartFile))
				.andExpect(status().isOk());
	}

}
