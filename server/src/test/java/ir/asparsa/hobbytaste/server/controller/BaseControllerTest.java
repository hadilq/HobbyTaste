package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.hobbytaste.server.Application;
import ir.asparsa.hobbytaste.server.security.*;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfigMock;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * @author hadi
 * @since 3/25/2017 AD.
 */
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
public abstract class BaseControllerTest {


    @Autowired
    protected WebApplicationContext context;
    @Autowired
    protected FilterChainProxy filterChainProxy;
    @Autowired
    protected JwtAuthenticationProviderMock jwtAuthenticationProviderMock;
    @Autowired
    protected JwtAuthenticationTokenFilterMock jwtAuthenticationTokenFilterMock;

    protected MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .dispatchOptions(true)
                .addFilter(filterChainProxy, "/*")
                .apply(springSecurity())
                .build();
    }
}
