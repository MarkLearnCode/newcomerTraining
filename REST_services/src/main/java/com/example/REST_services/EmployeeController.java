package com.example.REST_services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {

	private final EmployeeRepository repository;

	private final EmployeeModelAssembler assembler;

	EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {

		this.repository = repository;
		this.assembler = assembler;
	}

//  private final EmployeeRepository repository;
//
//  EmployeeController(EmployeeRepository repository) {
//    this.repository = repository;
//  }

//  @GetMapping("/employees")
//  List<Employee> all() {
//    return repository.findAll();
//  }

	// 已廢棄
//  @GetMapping("/employees")
//  CollectionModel<EntityModel<Employee>> all() {	  
//    List<EntityModel<Employee>> employees = repository.findAll().stream()
//        .map(employee -> EntityModel.of(employee,
//            linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
//            linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
//        .collect(Collectors.toList());
//
//    return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
//  }

	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all() {
		List<EntityModel<Employee>> employees = repository.findAll().stream()
				.map(employee -> assembler.toModel(employee)).collect(Collectors.toList());

//      EntityModel.of(employee,
//            WebMvcLinkBuilder.linkTo(EmployeeController.class).slash("employees").slash(employee.getId()).withSelfRel(),
//            WebMvcLinkBuilder.linkTo(EmployeeController.class).slash("employees").withRel("employees")))
		return CollectionModel.of(employees);
//      return CollectionModel.of(employees, WebMvcLinkBuilder.linkTo(EmployeeController.class).withRel("homeLink"));
	}

	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {
		repository.save(newEmployee);
		EntityModel<Employee> entityModel = assembler.toModel(newEmployee);

		return ResponseEntity //
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(entityModel);
	}

	// Single item

//  @GetMapping("/employees/{id}")
//  Employee one(@PathVariable Long id) {
//    
//    return repository.findById(id)
//      .orElseThrow(() -> new EmployeeNotFoundException(id));
//  }

	@GetMapping("/employees/{id}")
	EntityModel<Employee> one(@PathVariable Long id) {

		Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
		return assembler.toModel(employee);
//	  EntityModel model = EntityModel.of(employee);

//	  改用EmployeeModelAssembler
//    Employee employee = repository.findById(id) 
//        .orElseThrow(() -> new EmployeeNotFoundException(id));
//    
//    EntityModel<Employee> model = EntityModel.of(employee);
//    WebMvcLinkBuilder linkToSelf = WebMvcLinkBuilder.linkTo(
//        WebMvcLinkBuilder.methodOn(EmployeeController.class).one(id)
//    );
//    model.add(linkToSelf.withSelfRel());
//
//    Link linkToAll = WebMvcLinkBuilder.linkTo(EmployeeController.class).slash("employees").withRel("employees");
//    model.add(linkToAll);

//    return model;

//    linkTo及methodOn好像被棄用了，沒有此方法
//    return EntityModel.of(employee,
//    	linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
//        linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}

	@PutMapping("/employees/{id}")
	ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

		  Employee updatedEmployee = repository.findById(id) //
		      .map(employee -> {
		        employee.setName(newEmployee.getName());
		        employee.setRole(newEmployee.getRole());
		        return repository.save(employee);
		      }) //
		      .orElseGet(() -> {
		        newEmployee.setId(id);
		        return repository.save(newEmployee);
		      });

		  EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

		  return ResponseEntity //
		      .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
		      .body(entityModel);
		}
		
		// Optional<T> findById(ID id);
//		return repository.findById(id).map(employee -> {
//			employee.setName(newEmployee.getName());
//			employee.setRole(newEmployee.getRole());
//			return repository.save(employee);
//		}).orElseGet(() -> {
//			newEmployee.setId(id);
//			return repository.save(newEmployee);
//		});
//	}

	@DeleteMapping("/employees/{id}")
	ResponseEntity<?> deleteEmployee(@PathVariable Long id) {

		  repository.deleteById(id);

		  return ResponseEntity.noContent().build();
		}
//	void deleteEmployee(@PathVariable Long id) {
//		repository.deleteById(id);
//	}
}
