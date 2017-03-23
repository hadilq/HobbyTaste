package ir.asparsa.hobbytaste.server.security.config;

import ir.asparsa.common.net.path.BannerServicePath;
import ir.asparsa.common.net.path.UserServicePath;
import ir.asparsa.hobbytaste.server.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
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
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String ENTRY_POINT_API = "/api/v1";
    public static final String ENTRY_POINT_AUTHENTICATE = ENTRY_POINT_API + "/" + UserServicePath.SERVICE +
                                                           UserServicePath.AUTHENTICATE;
    public static final String ENTRY_POINT_BANNER = ENTRY_POINT_API + "/" + BannerServicePath.SERVICE +
                                                     BannerServicePath.IMAGE.replace("{filename:.+}", "*");
    public static final String ENTRY_POINT_TOKEN_BASED_AUTH = ENTRY_POINT_API + "/**";

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;
    @Autowired
    private JwtAuthenticationProvider authenticationProvider;
    @Autowired
    private JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        List<String> pathsToSkip = new ArrayList<>();
        pathsToSkip.add(ENTRY_POINT_AUTHENTICATE);
        pathsToSkip.add(ENTRY_POINT_BANNER);
        JwtAuthenticationTokenFilter authenticationTokenFilter =
                new JwtAuthenticationTokenFilter(new SkipPathRequestMatcher(pathsToSkip, ENTRY_POINT_TOKEN_BASED_AUTH));
        authenticationTokenFilter.setAuthenticationManager(authenticationManager());
        authenticationTokenFilter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
        authenticationTokenFilter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler);
        return authenticationTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()
                // All urls must be authenticated (filter for token always fires (/**)
                .authorizeRequests()
                .antMatchers(ENTRY_POINT_AUTHENTICATE).permitAll() // Authentication
                .antMatchers(ENTRY_POINT_BANNER).permitAll() // Banner
                .anyRequest().authenticated()
                .and()
                // Call our errorHandler if authentication/authorisation fails
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //.and()
        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // disable page caching
        httpSecurity.headers().cacheControl();
    }
}
