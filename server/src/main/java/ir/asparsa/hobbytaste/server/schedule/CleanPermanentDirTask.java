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
                        if (name.startsWith(StorageService.THUMBNAIL_PREFIX)) {
                            Optional<BannerModel> thumbnailUrl = bannerRepository.findByThumbnailName(name);
                            if (!thumbnailUrl.isPresent()) {
                                logger.info("Deleting thumbnail " + name);
                                storageService.moveFromPermanentToTrash(name, Strings.DEFAULT_LOCALE);
                            }
                        } else {
                            Optional<BannerModel> mainUrl = bannerRepository.findByMainName(name);
                            if (!mainUrl.isPresent()) {
                                logger.info("Deleting main " + name);
                                storageService.moveFromPermanentToTrash(name, Strings.DEFAULT_LOCALE);
                            }
                        }
                    }
                }
            } catch (NumberFormatException | SecurityException e) {
                logger.error("Couldn't delete file : " + file.getAbsolutePath(), e);
            }
        });
    }
}
