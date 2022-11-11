package ru.narsabu.geopointparser.converter;

import java.io.IOException;
import java.io.InputStream;
import lombok.val;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.narsabu.geopointparser.exception.FileNotAcceptableException;

@Component
public class FileToXlsxConverter {

  public Sheet convert(MultipartFile file) throws IOException {
    val fileName = file.getOriginalFilename();

    if (fileName == null) {
      throw new EmptyFileException();
    }

    if (fileName.endsWith(".xlsx")) {
      try (InputStream excelIs = file.getInputStream()) {
        try (Workbook wb = WorkbookFactory.create(excelIs)) {
          return wb.getSheetAt(0);
        }
      }
    }

    throw new FileNotAcceptableException("File with filename " + fileName + "cannot be converted to xlsx");
  }
}
