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
    @Value("${storage.location.trash}")
    private String trashLocation;

    private String userDir = System.getProperty("user.dir") + "/";

    public String getLocation() {
        return userDir + location;
    }

    public String getTmpLocation() {
        return userDir + tmpLocation;
    }

    public String getTrashLocation() {
        return userDir + trashLocation;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTmpLocation(String tmpLocation) {
        this.tmpLocation = tmpLocation;
    }
}