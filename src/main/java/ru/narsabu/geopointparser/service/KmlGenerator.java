package ru.narsabu.geopointparser.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.narsabu.geopointparser.dto.Placemark;

@Service
@Slf4j
public class KmlGenerator {

  public void createFile(String filename, List<Placemark> placemarks) {
    try {
      FileWriter myWriter = new FileWriter(filename);

      writeCommonInfo(myWriter);
      writePlacemarksInFile(myWriter, placemarks);
      writeEndOfFile(myWriter);

      myWriter.close();
    } catch (IOException e) {
      log.error("An error occurred: {}", e.getCause());
    }
  }

  private void writeCommonInfo(FileWriter writer) {
    try {
      writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n");
      writer.write("<kml>\n");
      writer.write("\t<Document>\n");
      writer.write("\t\t<name>\n");
      writer.write("\t\t\tGeofences\n");
      writer.write("\t\t</name>\n");

      log.info("Common info successfully wrote to the file.");
    } catch (IOException e) {
      log.error("An error occurred: {}", e.getCause());
    }
  }

  private void writePlacemarksInFile(FileWriter writer, List<Placemark> placemarks) {
    placemarks.forEach(placemark -> {
      try {
        writer.write("\t\t<Placemark>\n");
        writer.write("\t\t\t<name>\n");
        writer.write("\t\t\t\t" + placemark.getName() + "\n");
        writer.write("\t\t\t</name>\n");
        writer.write("\t\t\t<description color=\"99307b19\" width=\"5000.0\">\n");
        writer.write("\t\t\t\t" + placemark.getLatitude() + ", " + placemark.getLongitude() + "\n");
        writer.write("\t\t\t</description>\n");
        writer.write("\t\t\t<Point>\n");
        writer.write("\t\t\t\t<coordinates>\n");
        writer.write("\t\t\t\t\t" + placemark.getLongitude() + "," + placemark.getLatitude() + ",0\n");
        writer.write("\t\t\t\t</coordinates>\n");
        writer.write("\t\t\t</Point>\n");
        writer.write("\t\t</Placemark>\n");
      } catch (IOException e) {
        log.error("An error occurred: {}", e.getCause());
      }
    });
    log.info("Placemarks successfully wrote to the file.");
  }

  private void writeEndOfFile(FileWriter writer) {
    try {
      writer.write("\t</Document>\n");
      writer.write("</kml>\n");

      log.info("End of file successfully wrote to the file.");
    } catch (IOException e) {
      log.error("An error occurred: {}", e.getCause());
    }
  }
}
