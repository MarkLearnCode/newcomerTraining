package com.example.REST_services;

public class EmployeeNotFoundException extends RuntimeException {

	EmployeeNotFoundException(Long id) {
		super("Could not find employee " + id);
	}

}
