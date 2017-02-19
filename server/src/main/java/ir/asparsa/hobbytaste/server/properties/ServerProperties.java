package ir.asparsa.hobbytaste.server.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hadi
 * @since 2/19/2017 AD.
 */
@ConfigurationProperties("server")
public class ServerProperties {

    @Value("${server.realAddress}")
    private String serverAddress;
    @Value("${server.scheme}")
    private String scheme;


    public String getServerAddress() {
        return serverAddress;
    }

    public String getScheme() {
        return scheme;
    }
}
