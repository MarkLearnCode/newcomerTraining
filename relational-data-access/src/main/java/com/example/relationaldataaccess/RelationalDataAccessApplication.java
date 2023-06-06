package com.example.relationaldataaccess;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class RelationalDataAccessApplication implements CommandLineRunner {
	//CommandLineRunner 是一個接口，用於定義在 Spring Boot 應用程序啟動後執行的程式碼。
    //通常用於初始化數據或執行一些啟動任務。

	private static final Logger log = LoggerFactory.getLogger(RelationalDataAccessApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RelationalDataAccessApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;
	
//	改寫CommandLineRunner的run內容作為啟動boot的入口 ，String...為可接受多量變數
	@Override
	public void run(String... strings) throws Exception {

		log.info("Creating tables");

//		可以用於執行任意的 SQL 語句
		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
				.map(name -> name.split(" ")).collect(Collectors.toList());

		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

		// 批量更新數據，當對應資料來源與待更新值不同時會出現錯誤
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

		log.info("Querying for customer records where first_name = 'Josh':");

		// jdbcTemplate.query(SQL語句,查詢值,(resultSet,rowNum)的回傳值)
		// rowNum為查詢成功時產生數值，由0開始
		jdbcTemplate.query("SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
				new Object[] { "Josh" }, (rs, rowNum) -> {
					Customer customer = new Customer(rs.getLong("id"), rs.getString("first_name"),
							rs.getString("last_name"));
					log.info("query rowNum : " + rowNum);
					return customer;
				}).forEach(customer -> log.info(customer.toString()));
	}
}
