package com.example.REST_services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

	@Override
	public EntityModel<Employee> toModel(Employee employee) {

		WebMvcLinkBuilder linkToSelf = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(EmployeeController.class).one(employee.getId()));

		// 初始化MODEL
		EntityModel<Employee> model = EntityModel.of(employee, linkToSelf.withSelfRel());

		Link linkToAll = WebMvcLinkBuilder.linkTo(EmployeeController.class).slash("employees").withRel("employees");
		Link linkOne = WebMvcLinkBuilder.linkTo(EmployeeController.class).slash("testLink").withRel("This is test");
		Link linkEmployeeProfile = WebMvcLinkBuilder.linkTo(EmployeeController.class).slash("profile")
				.slash("employeeData").slash(employee.getId()).withRel("profileLink");
		model.add(linkToAll);
		model.add(linkOne);
		model.add(linkEmployeeProfile);

		return model;

		// 教學寫法 linkTo(methodOn在 @RestController無法使用 在@Controller中可以
//    return EntityModel.of(employee,
//        linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
//        linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));

//    EntityModel<Employee> model = EntityModel.of(employee);
//    WebMvcLinkBuilder linkToSelf = WebMvcLinkBuilder.linkTo(
//        WebMvcLinkBuilder.methodOn(EmployeeController.class).one(id)
//    );
//    model.add(linkToSelf.withSelfRel());
//
//    Link linkToAll = WebMvcLinkBuilder.linkTo(EmployeeController.class).slash("employees").withRel("employees");
//    model.add(linkToAll);

	}
}
