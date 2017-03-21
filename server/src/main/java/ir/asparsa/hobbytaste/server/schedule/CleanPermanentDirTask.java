package ir.asparsa.hobbytaste.server.schedule;

import ir.asparsa.hobbytaste.server.database.model.BannerModel;
import ir.asparsa.hobbytaste.server.database.repository.StoreBannerRepository;
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
import java.util.Optional;

/**
 * @author hadi
 * @since 3/21/2017 AD.
 */
@Component
public class CleanPermanentDirTask {

    private final static Logger logger = LoggerFactory.getLogger(CleanPermanentDirTask.class);

    @Autowired
    StorageService storageService;
    @Autowired
    StoreBannerRepository bannerRepository;

    @Async
    @Scheduled(initialDelay = 6000, fixedDelay = 604800000)
    public void doCleaning() {
        logger.info("Start cleaning");
        storageService.loadAll(Strings.DEFAULT_LOCALE).forEach(path -> {
            File file = path.toFile();
            logger.info("Path: " + file.getAbsolutePath());
            try {
                if (file.exists()) {
                    String name = file.getName();
                    logger.info("Name: " + name);
                    if (!StringUtils.isEmpty(name)) {
                        String url = storageService.getServerFileUrl(name);
                        logger.info("Url: " + url);
                        if (name.startsWith(StorageService.THUMBNAIL_PREFIX)) {
                            logger.info("Deleting " + name);
                            removeFile(path, bannerRepository.findByThumbnailUrl(url));
                        } else {
                            logger.info("Deleting " + name);
                            removeFile(path, bannerRepository.findByMainUrl(url));
                        }
                    }
                }
            } catch (IOException | NumberFormatException | SecurityException e) {
                logger.error("Couldn't delete file : " + file.getAbsolutePath(), e);
            }
        });
    }

    private void removeFile(
            Path path,
            Optional<BannerModel> banner
    ) throws IOException {
        if (!banner.isPresent()) {
            Files.delete(path);
        }
    }
}
