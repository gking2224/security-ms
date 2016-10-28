package me.gking2224.securityms.web.mvc;

import static me.gking2224.common.utils.test.JsonMvcTestHelper.doPost;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import me.gking2224.common.utils.JsonUtil;
import me.gking2224.common.utils.test.JsonMvcTestHelper;
import me.gking2224.securityms.TestInitializer;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles("web")
@ContextConfiguration(name="securityms", classes=WebAppTestConfiguration.class, initializers={TestInitializer.class})
@Transactional
@SpringBootTest()
@Rollback
@Sql("../../SingleUser.sql")
public class SecurityControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    
    private JsonUtil json;
    
    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        json = new JsonUtil();
    }

    @Test
    public void testAuthenticate() throws Exception {
        doPost(this.mockMvc, testCredentials(), "/authenticate", JsonMvcTestHelper::expectOK)
            .andDo(JsonMvcTestHelper::responseContent)
            .andExpect(jsonPath("$.roles.length()").value(greaterThan(0)))
            .andExpect(jsonPath("$.permissions.length()").value(greaterThan(0)))
            .andExpect(jsonPath("$.name").value(notNullValue()))
            .andExpect(jsonPath("$.authenticated").value(isA(Boolean.class)))
            .andExpect(jsonPath("$.credentials").value(notNullValue()));
    }

    private String testCredentials() {
        return testCredentials("test", "password");
    }

    private String testCredentials(String username, String password) {
        return String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
    }
}