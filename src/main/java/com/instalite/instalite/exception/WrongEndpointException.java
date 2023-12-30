package com.instalite.instalite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "WRONG_ENDPOINT")
public class WrongEndpointException extends RuntimeException {}
