package com.example.REST_services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

//全域的異常處理器（Global Exception Handler）。它用於集中處理應用程序中的異常，並提供一致的響應行為。

@ControllerAdvice
class EmployeeNotFoundAdvice {

  @ResponseBody  //響應主體（Response Body）返回，而不會經過視圖解析
  @ExceptionHandler(EmployeeNotFoundException.class)  //處理該class的Exception
  @ResponseStatus(HttpStatus.NOT_FOUND) //返回404 NOT FOUND狀態碼
  String employeeNotFoundHandler(EmployeeNotFoundException ex) {
    return ex.getMessage();
  }
}
