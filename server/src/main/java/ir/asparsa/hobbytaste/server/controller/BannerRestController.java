package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.BannerDto;
import ir.asparsa.common.net.path.BannerServicePath;
import ir.asparsa.hobbytaste.server.exception.StorageFileNotFoundException;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.storage.StorageService;
import ir.asparsa.hobbytaste.server.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

/**
 * @author hadi
 * @since 1/17/2017 AD.
 */
@RestController
@RequestMapping(WebSecurityConfig.ENTRY_POINT_API + "/" + BannerServicePath.SERVICE) class BannerRestController {

    private final static Logger logger = LoggerFactory.getLogger(BannerRestController.class);

    @Autowired
    StorageService storageService;
    @Autowired
    ImageUtil imageUtil;

    @Value("${server.realAddress}")
    private String serverAddress;
    @Value("${server.scheme}")
    private String scheme;

    public BannerRestController() {
    }

    @RequestMapping(value = BannerServicePath.IMAGE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename) {
        logger.info("Server file " + filename);
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @RequestMapping(value = BannerServicePath.TMP, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> serveTmpFile(@PathVariable("filename") String filename) {
        logger.info("Server tmp file " + filename);
        Resource file = storageService.loadTmpAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @RequestMapping(method = RequestMethod.POST)
    public BannerDto handleFileUpload(
            @RequestParam("file") MultipartFile file
    ) {
        logger.info("Server file " + file.getName() + ", Content type " + file.getContentType() + ", file size " +
                    file.getSize());

        String fileName;
        do {
            fileName = "" + System.currentTimeMillis() + "-" + file.getName().hashCode() + ".jpeg";
        } while (storageService.exists(fileName));
        storageService.store(file, fileName);
        String mainUrl = storageService.getTmpServerFileUrl(fileName);
        String thumbnailUrl = mainUrl;
        String thumbnailFileName = "thumbnail-" + fileName;

        BufferedImage thumbnail = imageUtil.scale(file);
        if (thumbnail != null) {
            imageUtil.saveImage(thumbnail, storageService.loadTmp(thumbnailFileName).toFile());
            thumbnailUrl = storageService.getTmpServerFileUrl(thumbnailFileName);
        }

        logger.info("File saved to " + mainUrl + ", thumbnail is " + thumbnailUrl);
        return new BannerDto(mainUrl, thumbnailUrl);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
