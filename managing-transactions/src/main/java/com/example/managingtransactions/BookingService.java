package com.example.managingtransactions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//類似@Service(為@Component的子類)
@Component
public class BookingService {
	private final static Logger logger = LoggerFactory.getLogger(BookingService.class);
//	private final JdbcTemplate jdbcTemplate;

//	應該可改寫取代LINE 15 21-25
	@Autowired 
	JdbcTemplate jdbcTemplate = new JdbcTemplate();

//	public BookingService(JdbcTemplate jdbcTemplate) {
//		this.jdbcTemplate = jdbcTemplate;
//	}

	@Transactional
	// 成功全部更新、失敗全部回滾 確保一致性
	public void book(String... persons) {
		for (String person : persons) {
			logger.info("Booking " + person + " in a seat...");
			jdbcTemplate.update("insert into BOOKINGS(FIRST_NAME) values (?)", person);
		}
	}

	public List<String> findAllBooks() {
		return jdbcTemplate.query("select FIRST_NAME from BOOKINGS", 
				(rs, rowNum) -> rs.getString("FIRST_NAME"));
	}

}
