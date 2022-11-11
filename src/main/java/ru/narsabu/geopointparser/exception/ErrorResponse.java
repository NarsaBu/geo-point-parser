package ru.narsabu.geopointparser.exception;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import lombok.Value;

@Value
public class ErrorResponse {

  Collection<Error> errors;

  public ErrorResponse(Error error) {
    this.errors = Collections.singletonList(error);
  }

  public ErrorResponse(Collection<Error> errors) {
    this.errors = Objects.requireNonNullElse(errors, Collections.emptyList());
  }
}
