package ir.asparsa.hobbytaste.server.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    @Value("${storage.location}")
    private String location;
    @Value("${storage.location.tmp}")
    private String tmpLocation;

    public String getLocation() {
        return location;
    }

    public String getTmpLocation() {
        return tmpLocation;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTmpLocation(String tmpLocation) {
        this.tmpLocation = tmpLocation;
    }
}