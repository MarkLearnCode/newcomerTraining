package com.example.uploadingfiles;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.example.uploadingfiles.storage.FileSystemStorageService;
import com.example.uploadingfiles.storage.StorageProperties;
import com.example.uploadingfiles.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class UploadingFilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploadingFilesApplication.class, args);
	}
	//StorageService 為interface 裡面只寫了方法名稱，實作寫在FileSystemStorageService
	//但實際上在運行時，它會被實例化為 FileSystemStorageService 的物件
//	@Bean
//	CommandLineRunner init(StorageService storageService) {
//		return (args) -> {
//			storageService.deleteAll();
//			storageService.init();
//		};
//	}
	
	//FileSystemStorageService 為StorageService的實作類別，此寫法較不意搞混，且能快速查看方法
	@Bean
	CommandLineRunner init2(FileSystemStorageService fileSystemStorageService) {
		return (args) -> {
			fileSystemStorageService.deleteAll();
			fileSystemStorageService.init();
		};
	}
}
