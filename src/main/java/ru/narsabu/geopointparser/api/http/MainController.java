package ru.narsabu.geopointparser.api.http;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
