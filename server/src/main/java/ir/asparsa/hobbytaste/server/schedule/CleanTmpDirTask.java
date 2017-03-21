package ir.asparsa.hobbytaste.server.schedule;

import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.storage.StorageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author hadi
 * @since 3/21/2017 AD.
 */
@Component
public class CleanTmpDirTask {

    private final static Logger logger = LoggerFactory.getLogger(CleanTmpDirTask.class);

    private static final long EXPIRATION_TIME = 604800000; // One week

    @Autowired
    StorageService storageService;

    @Async
    @Scheduled(initialDelay = 5000, fixedDelay = EXPIRATION_TIME)
    public void doCleaning() {
        logger.info("Start cleaning");
        long current = System.currentTimeMillis();
        storageService.loadAllTmp(Strings.DEFAULT_LOCALE).forEach(path -> {
            File file = path.toFile();
            logger.info("Path: " + file.getAbsolutePath());
            try {
                if (file.exists()) {
                    String name = path.toFile().getName();
                    if (!StringUtils.isEmpty(name)) {
                        String[] parts = name.split(StorageService.FILE_NAME_SPLITTER);
                        if (parts.length >= 1 && !StorageService.THUMBNAIL_PREFIX.equals(parts[0])) {
                            removeFile(current, path, name, parts[0]);
                        } else if (parts.length >= 2 && StorageService.THUMBNAIL_PREFIX.equals(parts[0])) {
                            removeFile(current, path, name, parts[1]);
                        }
                    }
                }
            } catch (IOException | NumberFormatException | SecurityException e) {
                logger.error("Couldn't delete file : " + file.getAbsolutePath(), e);
            }
        });
    }

    private void removeFile(
            long current,
            Path path,
            String name,
            String part
    ) throws IOException {
        long timeDate = Long.parseLong(part);
        if (current - timeDate > EXPIRATION_TIME) {
            logger.info("Deleting " + name);
            Files.delete(path);
        }
    }
}
