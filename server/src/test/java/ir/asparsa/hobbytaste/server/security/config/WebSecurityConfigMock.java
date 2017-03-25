package ir.asparsa.hobbytaste.server.security.config;

import ir.asparsa.hobbytaste.server.security.JwtAuthenticationProviderMock;
import ir.asparsa.hobbytaste.server.security.JwtAuthenticationTokenFilterMock;
import ir.asparsa.hobbytaste.server.security.SkipPathRequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.util.Arrays;
import java.util.List;

/**
 * @author hadi
 * @since 12/1/2016 AD
 */
@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigMock extends BaseSecurityConfig {

    @Autowired
    private JwtAuthenticationProviderMock authenticationProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }

    @Bean
    public JwtAuthenticationTokenFilterMock authenticationTokenFilterBean() throws Exception {
        List<String> pathsToSkip = getPathsToSkip();
        JwtAuthenticationTokenFilterMock authenticationTokenFilter = new JwtAuthenticationTokenFilterMock(
                new SkipPathRequestMatcher(pathsToSkip, WebSecurityConfig.ENTRY_POINT_TOKEN_BASED_AUTH));
        setupAuthenticationFilter(authenticationTokenFilter);
        return authenticationTokenFilter;
    }

    @Override protected AbstractAuthenticationProcessingFilter authenticationTokenFilter() throws Exception {
        return authenticationTokenFilterBean();
    }
}
