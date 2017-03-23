package ir.asparsa.hobbytaste.server.storage;

import ir.asparsa.hobbytaste.server.properties.ServerProperties;
import ir.asparsa.hobbytaste.server.properties.StorageProperties;
import ir.asparsa.hobbytaste.server.resources.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * @author hadi
 * @since 3/22/2017 AD.
 */
public class StorageServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(StorageServiceTest.class);

    private StorageService storageService;
    private ServerProperties serverPropertiesMock;
    private StorageProperties storagePropertiesMock;

    @Before
    public void setup() {
        serverPropertiesMock = Mockito.mock(ServerProperties.class);
        storagePropertiesMock = Mockito.mock(StorageProperties.class);
        when(storagePropertiesMock.getLocation()).thenReturn("/some-dir");
        when(storagePropertiesMock.getTmpLocation()).thenReturn("/some-tmp-dir");
        when(serverPropertiesMock.getScheme()).thenReturn("http");
        when(serverPropertiesMock.getServerAddress()).thenReturn("domain.com");
        storageService = new FileSystemStorageService(serverPropertiesMock, storagePropertiesMock);
    }

    @Test
    public void fileNameToUrl() {
        String fileName = "slkfjnls.skdfj";
        String serverFileUrl = storageService.getServerFileUrl(fileName);
        String tmpServerFileUrl = storageService.getTmpServerFileUrl(fileName);
        logger.info("serverFileUrl: " + serverFileUrl);
        logger.info("tmpServerFileUrl: " + tmpServerFileUrl);
        assertEquals("filename", storageService.getFilename(serverFileUrl, Strings.DEFAULT_LOCALE), fileName);
        assertEquals("tmp filename", storageService.getFilename(tmpServerFileUrl, Strings.DEFAULT_LOCALE), fileName);
    }

    @Test
    public void urlToFileName() {
        String url = "http://domain.com/api/v1/banner/image/2347.02934";
        String tmpUrl = "http://domain.com/api/v1/banner/tmp/2347.02934";
        String filename = storageService.getFilename(url, Strings.DEFAULT_LOCALE);
        logger.info("filename: " + filename);
        assertEquals("url", storageService.getServerFileUrl(filename), url);
        assertEquals("tmp url", storageService.getTmpServerFileUrl(filename), tmpUrl);
    }
}
