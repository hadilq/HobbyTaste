package ir.asparsa.hobbytaste.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.asparsa.common.net.dto.AuthenticateDto;
import ir.asparsa.common.net.path.UserServicePath;
import ir.asparsa.hobbytaste.server.Application;
import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.security.*;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfigMock;
import ir.asparsa.hobbytaste.server.storage.StorageService;
import ir.asparsa.hobbytaste.server.util.JwtTokenUtil;
import ir.asparsa.hobbytaste.server.util.RequestLogUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author hadi
 * @since 3/22/2017 AD.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserRestController.class)
@EnableAutoConfiguration(exclude = {
        WebSecurityConfig.class,
        JwtAuthenticationProvider.class
})
@ContextConfiguration(classes = {
        Application.class,
        WebSecurityConfigMock.class,
        JwtAuthenticationEntryPoint.class,
        JwtAuthenticationProviderMock.class,
        JwtAuthenticationFailureHandler.class
})
@WebAppConfiguration
public class UserRestControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private FilterChainProxy filterChainProxy;
    @Autowired
    private JwtAuthenticationProviderMock jwtAuthenticationProviderMock;
    @Autowired
    private JwtAuthenticationTokenFilterMock jwtAuthenticationTokenFilterMock;

    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private RequestLogUtil requestLogUtil;
    @MockBean
    private StorageService storageService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;


    private JacksonTester<AuthenticateDto> json;

    @Before
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Possibly configure the mapper
        JacksonTester.initFields(this, objectMapper);


        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .dispatchOptions(true)
                .addFilter(filterChainProxy, "/*")
                .apply(springSecurity())
                .build();
    }

    @Test
    public void authenticationTest() throws Exception {
        long hashCode = new Random().nextLong();

        String token = "token";

        AccountModel accountModel = new AccountModel("testUser", hashCode, "USER");
        given(accountRepository.findByHashCode(hashCode))
                .willReturn(Optional.of(accountModel));
        given(jwtTokenUtil.generateToken(accountModel))
                .willReturn(token);

        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" +
                     UserServicePath.SERVICE + UserServicePath.AUTHENTICATE)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content("{\"hashCode\":" + hashCode + ", \"token\":\"slkdfjvn\"}"))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                       .andReturn();

        AuthenticateDto authenticateDto = this.json.parse(result.getResponse().getContentAsString()).getObject();
        AuthenticateDto expected = new AuthenticateDto(token, accountModel.getUsername());
        assertThat(authenticateDto.getToken()).isEqualTo(expected.getToken());
        assertThat(authenticateDto.getUsername()).isEqualTo(expected.getUsername());
    }

    @Test
    public void changeUsernameTest() throws Exception {
        long hashCode = new Random().nextLong();

        String token = "token";
        jwtAuthenticationTokenFilterMock.setToken(token);

        AccountModel accountModel = new AccountModel("testUser", hashCode, "USER");
        jwtAuthenticationProviderMock.setParsedUser(accountModel);

        given(jwtTokenUtil.generateToken(accountModel))
                .willReturn(token);

        String newUsername = "newUsername";
        MvcResult result = this.mockMvc.perform(
                post(WebSecurityConfig.ENTRY_POINT_API + "/" +
                     UserServicePath.SERVICE + UserServicePath.USERNAME.replace("{hashCode}", Long.toString(hashCode)))
                        .param("new", newUsername))
                                       .andExpect(status().isOk())
                                       .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                                       .andReturn();

        AuthenticateDto authenticateDto = this.json.parse(result.getResponse().getContentAsString()).getObject();
        AuthenticateDto expected = new AuthenticateDto(token, accountModel.getUsername());
        assertThat(authenticateDto.getToken()).isEqualTo(expected.getToken());
        assertThat(authenticateDto.getUsername()).isEqualTo(newUsername);
    }
}
