package ir.asparsa.hobbytaste.server.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(
            MultipartFile file,
            String fileName,
            String locale
    );

    Stream<Path> loadAll(String locale);

    Path load(String filename);

    Path loadTmp(String filename);

    boolean exists(String filename);

    Resource loadAsResource(String filename, String locale);

    Resource loadTmpAsResource(String filename, String locale);

    void moveFromTmpToPermanent(String filename, String locale);

    void deleteAll();

    String getServerFileUrl(String fileName);

    String getTmpServerFileUrl(String fileName);

    String getFilename(String url, String locale);

}