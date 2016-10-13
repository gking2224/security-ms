package me.gking2224.securityms.web.mvc;

import static java.lang.String.valueOf;
import static me.gking2224.common.utils.test.JsonMvcTestHelper.doGet;
import static me.gking2224.common.utils.test.JsonMvcTestHelper.doPost;
import static me.gking2224.common.utils.test.JsonMvcTestHelper.doPut;
import static me.gking2224.common.utils.test.JsonMvcTestHelper.responseContent;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import me.gking2224.common.utils.JsonUtil;
import me.gking2224.common.utils.test.JsonMvcTestHelper;
import me.gking2224.securityms.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles({"embedded","web"})
@ContextConfiguration(classes=WebAppTestConfiguration.class)
@Transactional
@Rollback
@Sql
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
    public void testGetAllThings() throws Exception {
        doGet(this.mockMvc, "/things", JsonMvcTestHelper::expectOK)
            .andDo(JsonMvcTestHelper::responseContent)
            .andExpect(jsonPath("$.length()").value(greaterThan(0)))
            .andDo((m)->assertEquals("/things/1", json.getFilterValue(responseContent(m), "$.[?(@._id == '1')].location")));
    }

    @Test
    public void testNewThing() throws Exception {
        String name = "User";
        User u = new User();
        u.setUsername(name);
        ResultActions result = doPost(this.mockMvc, u, "/things", JsonMvcTestHelper::expectOK);
        result
            .andExpect(jsonPath("$._id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(hasToString(valueOf(name))))
            .andExpect(jsonPath("$.location").isNotEmpty());
    }

    @Test
    public void testGetSingleThing() throws Exception {
        String name = "Test User";
        long id = 1L;
        String address = "/things/" + id;
        ResultActions result = doGet(this.mockMvc, address, JsonMvcTestHelper::expectOK);
        result
            .andExpect(jsonPath("$._id").value(hasToString(valueOf(id))))
            .andExpect(jsonPath("$.name").value(equalTo(name)))
            .andExpect(jsonPath("$.location").value(equalTo(address)));
    }

    @Test
    public void testUpdateThing() throws Exception {
        String newName = "User.x";
        long id = 1L;
        User u = new User();
        u.setUsername(newName);
        
        doPut(this.mockMvc, u, "/things/" + id, JsonMvcTestHelper::expectOK)
            .andExpect(jsonPath("$._id").value(hasToString(valueOf(id))))
            .andExpect(jsonPath("$.name").value(equalTo(newName)))
            .andExpect(jsonPath("$.location").isNotEmpty());
    }
}