package ir.asparsa.hobbytaste.server.storage;

import ir.asparsa.common.net.path.BannerServicePath;
import ir.asparsa.hobbytaste.server.exception.FilenameNotFoundErrorException;
import ir.asparsa.hobbytaste.server.exception.StorageException;
import ir.asparsa.hobbytaste.server.exception.StorageFileNotFoundException;
import ir.asparsa.hobbytaste.server.properties.ServerProperties;
import ir.asparsa.hobbytaste.server.properties.StorageProperties;
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
    public void store(
            MultipartFile file,
            String filename
    ) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            Files.copy(file.getInputStream(), loadTmp(filename));
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                        .filter(path -> !path.equals(this.rootLocation))
                        .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
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
    public Resource loadAsResource(String filename) {
        try {
            return getResource(filename, load(filename));
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override public Resource loadTmpAsResource(String filename) {
        try {
            return getResource(filename, loadTmp(filename));
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override public void moveFromTmpToPermanent(String filename) {
        Path source = loadTmp(filename);
        Path target = load(filename);
        try {
            Files.move(source, target);
        } catch (IOException e) {
            throw new StorageException("Failed to move tmp source to permanent store. File " + filename, e);
        }
    }

    private Resource getResource(
            String filename,
            Path file
    ) throws MalformedURLException {
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new StorageFileNotFoundException("Could not read file: " + filename);

        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
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


    public String getFilename(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new FilenameNotFoundErrorException("url is empty!");
        }
        try {
            String[] pathParts = new URL(url).getPath().split("/");
            if (pathParts.length < 1) {
                throw new FilenameNotFoundErrorException("Url path is " + url);
            }
            return pathParts[pathParts.length - 1];
        } catch (MalformedURLException e) {
            throw new FilenameNotFoundErrorException("Url cannot be parsed! " + url, e);
        }
    }
}