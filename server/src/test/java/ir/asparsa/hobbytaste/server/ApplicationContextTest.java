package ir.asparsa.hobbytaste.server;

import ir.asparsa.hobbytaste.server.security.JwtAuthenticationEntryPoint;
import ir.asparsa.hobbytaste.server.security.JwtAuthenticationFailureHandler;
import ir.asparsa.hobbytaste.server.security.JwtAuthenticationProvider;
import ir.asparsa.hobbytaste.server.security.JwtAuthenticationProviderMock;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfigMock;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author hadi
 * @since 3/23/2017 AD.
 */
@Configuration
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
public class ApplicationContextTest {

}
