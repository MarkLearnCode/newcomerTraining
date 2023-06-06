package com.example.schedulingtasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
	
//	@Scheduled()
//	fixedRate = 5000	每隔 5 秒執行一次
//	fixedDelay = 5000	任務執行完成後，再過 5 秒後再次執行
//	initialDelay = 5000, fixedRate = 5000	延遲 5 秒後首次執行，然後每隔 5 秒執行一次
//	cron = "0 0 8 * * MON-FRI"	每週一至週五的早上 8 點執行一次
	
	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		log.info("The time is now {}",dateFormat.format(new Date()));
	}
}
