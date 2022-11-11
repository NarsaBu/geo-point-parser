package ru.narsabu.geopointparser.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import ru.narsabu.geopointparser.dto.Placemark;

@Service
public class XlsxFilePlacemarkCaptureService {

  public List<Placemark> readXlsxFile(String filename, Sheet sheet) {
      int totalRows = sheet.getLastRowNum();

      List<Placemark> placemarks = new ArrayList<>();

      for (int i = 0; i < totalRows; i++) {
        int counter = 1;
        if (sheet.getRow(i) != null) {
          if (sheet.getRow(i).getCell(0) != null) {
            String name = filename + " " + counter++;
            Double latitude = Double.parseDouble(sheet.getRow(i).getCell(7).toString());
            Double longitude = Double.parseDouble(sheet.getRow(i).getCell(8).toString());
            placemarks.add(new Placemark(name, latitude, longitude));
          }
        }
      }

      return placemarks;
  }

}
