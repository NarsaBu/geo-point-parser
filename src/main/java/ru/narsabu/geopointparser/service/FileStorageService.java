package ru.narsabu.geopointparser.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import ru.narsabu.geopointparser.configuration.properties.FileStorageProperties;
import ru.narsabu.geopointparser.dto.Placemark;
import ru.narsabu.geopointparser.exception.FileStorageException;
import ru.narsabu.geopointparser.exception.MyFileNotFoundException;

@Service
@Slf4j
public class FileStorageService {

  private final FileStorageProperties properties;
  private final Path fileStorageLocation;
  private final KmlGenerator kmlGenerator;

  @Autowired
  public FileStorageService(FileStorageProperties fileStorageProperties, KmlGenerator kmlGenerator) {
    this.properties = fileStorageProperties;
    this.fileStorageLocation = Paths.get(properties.getUploadDir())
      .toAbsolutePath().normalize();
    this.kmlGenerator = kmlGenerator;

    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  public List<String> listAllUploads() {
    return FileUtils.listFiles(new File(properties.getUploadDir()), null, false).stream()
      .map(File::getName)
      .collect(Collectors.toList());
  }

  public Resource storeFile(String filename, List<Placemark> placemarks) {
    try {
      val filenameWithExtension = filename + ".kml";
      File file = new File(filenameWithExtension);
      kmlGenerator.createFile(filenameWithExtension, placemarks);

      Path targetLocation = this.fileStorageLocation.resolve(filename + ".kml");
      Files.copy(file.toPath(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      Files.delete(Paths.get("./" + filenameWithExtension));

      return loadFileAsResource(filenameWithExtension);
    } catch (IOException ex) {
      throw new FileStorageException("Could not store file " + filename + ". Please try again!", ex);
    }
  }

  public void deleteUploads() {
    try {
      FileUtils.cleanDirectory(new File(properties.getUploadDir()));
    } catch (IOException e) {
      log.error("error while delete uploads {}", e);
    }
  }

  public Resource loadFileAsResource(String fileName) {
    try {
      Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if(resource.exists()) {
        return resource;
      } else {
        throw new MyFileNotFoundException("File not found " + fileName);
      }
    } catch (MalformedURLException ex) {
      throw new MyFileNotFoundException("File not found " + fileName, ex);
    }
  }
}
