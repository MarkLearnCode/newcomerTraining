package com.example.managingtransactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AppRunner implements CommandLineRunner {

	private final static Logger logger = LoggerFactory.getLogger(AppRunner.class);

//	private final BookingService bookingService;
//
//	public AppRunner(BookingService bookingService) {
//		this.bookingService = bookingService;
//	}
	@Autowired
	private BookingService bookingService;

	@Override
	public void run(String... args) throws Exception {
		bookingService.book("Alice", "Bob", "Carol");
		Assert.isTrue(bookingService.findAllBooks().size() == 3, "First booking should work with no problem");
		logger.info("Alice, Bob and Carol have been booked");

		try {
			bookingService.book("Chris", "Samuel");
		} catch (RuntimeException e) {
			logger.info("v--- The following exception is expect because 'Samuel' is too " + "big for the DB ---v");
			logger.error(e.getMessage());
		}

		for (String person : bookingService.findAllBooks()) {
			logger.info("So far, " + person + " is booked.");
		}
		logger.info("You shouldn't see Chris or Samuel. Samuel violated DB constraints, "
				+ "and Chris was rolled back in the same TX");
		//判斷是否符合條件，不符合既停止
		Assert.isTrue(bookingService.findAllBooks().size() == 3, "'Samuel' should have triggered a rollback");
		
		try {
			bookingService.book("Buddy", null);
		} catch (RuntimeException e) {
			logger.info("v--- The following exception is expect because null is not " + "valid for the DB ---v");
			logger.error(e.getMessage());
		}

		for (String person : bookingService.findAllBooks()) {
			logger.info("So far, " + person + " is booked.");
		}
		logger.info("You shouldn't see Buddy or null. null violated DB constraints, and "
				+ "Buddy was rolled back in the same TX");
		System.out.println("size = "+bookingService.findAllBooks().size());
		Assert.isTrue(bookingService.findAllBooks().size() == 3, "'null' should have triggered a rollback");
	}

}
