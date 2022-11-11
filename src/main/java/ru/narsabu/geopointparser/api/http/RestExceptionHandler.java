package ru.narsabu.geopointparser.api.http;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EmptyFileException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.narsabu.geopointparser.exception.ErrorResponse;
import ru.narsabu.geopointparser.exception.FileNotAcceptableException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

  @ResponseBody
  @ExceptionHandler(IOException.class)
  @ResponseStatus(BAD_REQUEST)
  public ErrorResponse ioException(IOException e) {
    log.warn(e.getMessage(), e);
    return new ErrorResponse(singletonList(new Error(null, e)));
  }

  @ResponseBody
  @ExceptionHandler(FileNotAcceptableException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse fileNotAcceptableException(FileNotAcceptableException e) {
    log.warn(e.getMessage(), e);
    return new ErrorResponse(singletonList(new Error(null, e)));
  }

  @ExceptionHandler(EmptyFileException.class)
  @ResponseBody
  @ResponseStatus(BAD_REQUEST)
  public ErrorResponse emptyFileException(EmptyFileException e) {
    log.error(e.getMessage(), e);
    return new ErrorResponse(new Error(null, e));
  }
}