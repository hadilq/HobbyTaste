package ir.asparsa.hobbytaste.server.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hadi
 * @since 12/1/2016 AD
 */
public class SkipPathRequestMatcher implements RequestMatcher {
    private OrRequestMatcher skipMatchers;
    private RequestMatcher processingMatcher;

    public SkipPathRequestMatcher(
            List<String> pathsToSkip,
            String processingPath
    ) {
        Assert.notNull(pathsToSkip);
        List<RequestMatcher> m = pathsToSkip.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList());
        skipMatchers = new OrRequestMatcher(m);
        processingMatcher = new AntPathRequestMatcher(processingPath);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !skipMatchers.matches(request) && processingMatcher.matches(request);
    }
}
