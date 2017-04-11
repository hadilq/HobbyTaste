package ir.asparsa.hobbytaste.server.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    String FILE_NAME_SPLITTER = "-";
    String THUMBNAIL_PREFIX = "thumbnail";

    void init();

    void store(
            MultipartFile file,
            String fileName,
            String locale
    );

    Stream<Path> loadAll(String locale);

    Stream<Path> loadAllTmp(String locale);

    Path load(String filename);

    Path loadTmp(String filename);

    Path loadTrash(String filename);

    boolean exists(String filename);

    Resource loadAsResource(
            String filename,
            String locale
    );

    Resource loadTmpAsResource(
            String filename,
            String locale
    );

    void moveFromTmpToPermanent(
            String filename,
            String locale
    );

    void moveFromPermanentToTrash(
            String filename,
            String locale
    );

    void deleteAll();

    String getServerFileUrl(String fileName);

    String getTmpServerFileUrl(String fileName);

    String getFilename(
            String url,
            String locale
    );

}