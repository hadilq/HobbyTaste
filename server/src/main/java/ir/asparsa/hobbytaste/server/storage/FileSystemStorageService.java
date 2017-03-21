package ir.asparsa.hobbytaste.server.storage;

import ir.asparsa.common.net.path.BannerServicePath;
import ir.asparsa.hobbytaste.server.exception.FilenameNotFoundErrorException;
import ir.asparsa.hobbytaste.server.exception.StorageException;
import ir.asparsa.hobbytaste.server.exception.StorageFileNotFoundException;
import ir.asparsa.hobbytaste.server.properties.ServerProperties;
import ir.asparsa.hobbytaste.server.properties.StorageProperties;
import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final Path rootTmpLocation;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.rootTmpLocation = Paths.get(properties.getTmpLocation());
    }

    @Override
    public void init() {
        try {
            if (!rootLocation.toFile().exists()) {
                Files.createDirectory(rootLocation);
            }
            if (!rootTmpLocation.toFile().exists()) {
                Files.createDirectory(rootTmpLocation);
            }
        } catch (IOException e) {
            throw new StorageException(
                    "Could not initialize storage", e, Strings.CANNOT_INIT_STORAGE, Strings.DEFAULT_LOCALE);
        }
    }

    @Override
    public void store(
            MultipartFile file,
            String filename,
            String locale
    ) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename, Strings.EMPTY_FILE, locale);
            }
            Files.copy(file.getInputStream(), loadTmp(filename));
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e, Strings.CANNOT_STORE_FILE, locale);
        }
    }

    @Override
    public Stream<Path> loadAll(String locale) {
        try {
            return Files.walk(this.rootLocation, 1)
                        .filter(path -> !path.equals(this.rootLocation));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e, Strings.CANNOT_READ_STORED_FILES, locale);
        }
    }

    @Override public Stream<Path> loadAllTmp(String locale) {
        try {
            return Files.walk(this.rootLocation, 1)
                        .filter(path -> !path.equals(this.rootLocation));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e, Strings.CANNOT_READ_STORED_FILES, locale);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override public Path loadTmp(String filename) {
        return rootTmpLocation.resolve(filename);
    }

    @Override public boolean exists(String filename) {
        Path file = load(filename);
        try {
            Resource resource = new UrlResource(file.toUri());
            return resource.exists();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Resource loadAsResource(
            String filename,
            String locale
    ) {
        try {
            return getResource(filename, load(filename), locale);
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException(
                    "Could not read file: " + filename, e, Strings.CANNOT_READ_STORED_FILE, locale);
        }
    }

    @Override public Resource loadTmpAsResource(
            String filename,
            String locale
    ) {
        try {
            return getResource(filename, loadTmp(filename), locale);
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException(
                    "Could not read file: " + filename, e, Strings.CANNOT_READ_STORED_FILE, locale);
        }
    }

    @Override public void moveFromTmpToPermanent(
            String filename,
            String locale
    ) {
        Path source = loadTmp(filename);
        Path target = load(filename);
        try {
            Files.move(source, target);
        } catch (IOException e) {
            throw new StorageException(
                    "Failed to move tmp source to permanent store. File " + filename, e, Strings.CANNOT_MOVE_FILE,
                    locale);
        }
    }

    private Resource getResource(
            String filename,
            Path file,
            String locale
    ) throws MalformedURLException {
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new StorageFileNotFoundException(
                    "Could not read file: " + filename, Strings.CANNOT_READ_STORED_FILE, locale);

        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public String getServerFileUrl(String fileName) {
        return (serverProperties.getScheme() + "://" + serverProperties.getServerAddress() +
                WebSecurityConfig.ENTRY_POINT_API + "/" +
                BannerServicePath.SERVICE + BannerServicePath.IMAGE).replace("{filename:.+}", fileName);
    }


    public String getTmpServerFileUrl(String fileName) {
        return (serverProperties.getScheme() + "://" + serverProperties.getServerAddress() +
                WebSecurityConfig.ENTRY_POINT_API + "/" +
                BannerServicePath.SERVICE + BannerServicePath.TMP).replace("{filename}", fileName);
    }


    public String getFilename(
            String url,
            String locale
    ) {
        if (StringUtils.isEmpty(url)) {
            throw new FilenameNotFoundErrorException("url is empty!", Strings.FILENAME_NOT_FOUND, locale);
        }
        try {
            String[] pathParts = new URL(url).getPath().split("/");
            if (pathParts.length < 1) {
                throw new FilenameNotFoundErrorException("Url path is " + url, Strings.FILENAME_NOT_FOUND, locale);
            }
            return pathParts[pathParts.length - 1];
        } catch (MalformedURLException e) {
            throw new FilenameNotFoundErrorException(
                    "Url cannot be parsed! " + url, e, Strings.FILENAME_NOT_FOUND, locale);
        }
    }
}