package com.example.REST_services;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

  @Override
  public EntityModel<Order> toModel(Order order) {

    // Unconditional links to single-item resource and aggregate root

//    EntityModel<Order> orderModel = EntityModel.of(order,
//            linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
//            linkTo(methodOn(OrderController.class).all()).withRel("orders"));
	  WebMvcLinkBuilder linkSelf = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).one(order.getId()));
	  
	// 初始化MODEL
			EntityModel<Order> orderModel = EntityModel.of(order, linkSelf.withSelfRel());

			Link linkToAll = WebMvcLinkBuilder.linkTo(EmployeeController.class).slash("orders").withRel("orders");
			orderModel.add(linkToAll);
	  
//	  EntityModel<Order> orderModel = EntityModel.of(order,
//	            linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
//	            linkTo(methodOn(OrderController.class).all()).withRel("orders"));

    // Conditional links based on state of the order

    if (order.getStatus() == Status.IN_PROGRESS) {
      orderModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
      orderModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).complete(order.getId())).withRel("complete"));

//      orderModel.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
    }

    return orderModel;
  }
}
