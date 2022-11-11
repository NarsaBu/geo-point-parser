package ru.narsabu.geopointparser.api.http;

import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.narsabu.geopointparser.converter.FileToXlsxConverter;
import ru.narsabu.geopointparser.service.FileStorageService;
import ru.narsabu.geopointparser.service.XlsxFilePlacemarkCaptureService;

@RestController
@RequestMapping(path = "/main-controller")
@RequiredArgsConstructor
@Slf4j
public class MainController {

  private final FileToXlsxConverter converter;
  private final XlsxFilePlacemarkCaptureService xlsxFilePlacemarkCaptureService;
  private final FileStorageService fileStorageService;

  @GetMapping("/list-all")
  public List<String> listAllUploads() {
    return fileStorageService.listAllUploads();
  }

  @GetMapping("/{filename}")
  public ResponseEntity<Resource> getFileFromUploads(@RequestParam String filename) {
    val resource = fileStorageService.loadFileAsResource(filename);

    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType("application/octet-stream"))
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
      .body(resource);
  }

  @PostMapping(path = "parse-xml-to-kml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Resource> parseXmlToKml(@RequestParam(name = "file") MultipartFile file) throws IOException {
    val convertedFile = converter.convert(file);
    val filename = file.getOriginalFilename().split(".xlsx")[0];
    val placemarks = xlsxFilePlacemarkCaptureService.readXlsxFile(filename, convertedFile);
    val resource =  fileStorageService.storeFile(filename, placemarks);


    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType("application/octet-stream"))
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
      .body(resource);
  }

  @DeleteMapping
  public void deleteAllUploads() {
    fileStorageService.deleteUploads();
  }
}
