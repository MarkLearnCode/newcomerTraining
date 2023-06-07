package com.example.uploadingfiles.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}
	
//	MultipartFile 是 Spring 框架提供的介面，用於處理 HTTP 請求中的檔案上傳。它封裝了上傳的檔案數據，並提供了一些便捷的方法來操作這些檔案。
//
//	MultipartFile 介面提供了以下常用方法：
//
//	String getName(): 獲取檔案參數的名稱。
//	String getOriginalFilename(): 獲取上傳的原始檔案名稱。
//	String getContentType(): 獲取上傳檔案的內容類型。
//	boolean isEmpty(): 判斷檔案是否為空。
//	long getSize(): 獲取檔案的大小，以字節為單位。
//	byte[] getBytes(): 將檔案的內容以字節數組的形式返回。
//	InputStream getInputStream(): 獲取檔案的輸入流，用於讀取檔案內容。

	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
			}
			Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
		}
	}
//	Files:
//	createFile(Path path, FileAttribute<?>... attrs): 創建一個新的空檔案。
//	createDirectory(Path dir, FileAttribute<?>... attrs): 創建一個新的目錄。
//	delete(Path path): 刪除指定的檔案或目錄。
//	copy(Path source, Path target, CopyOption... options): 將一個檔案或目錄複製到目標位置。
//	move(Path source, Path target, CopyOption... options): 將一個檔案或目錄移動到目標位置。
//	readAllBytes(Path path): 讀取檔案的所有字節內容，返回字節數組。
//	readAllLines(Path path, Charset cs): 讀取檔案的所有行內容，返回字符串的列表。
//	write(Path path, byte[] bytes, OpenOption... options): 將字節數組寫入指定的檔案。
//	write(Path path, Iterable<? extends CharSequence> lines, Charset cs, OpenOption... options): 將字符串列表寫入指定的檔案。

//	Files.walk() 是 Files 類別中的一個方法，用於遞迴地遍歷指定目錄下的所有檔案和子目錄。
//	static Stream<Path> walk(Path start, int maxDepth, FileVisitOption... options)
//	start：起始目錄的路徑，表示從哪個目錄開始遍歷。
//	maxDepth：指定遞迴的最大深度，控制遍歷的層級數。設為 1 表示僅遍歷起始目錄本身，不包括子目錄；設為 Integer.MAX_VALUE 表示遍歷所有子目錄。
//	options：遍歷的選項，例如可以指定 FileVisitOption.FOLLOW_LINKS 來遍歷符號連結指向的檔案。
	
	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
					.filter(path -> !path.equals(this.rootLocation))
					.map(path -> this.rootLocation.relativize(path));
//			this.rootLocation.relativize(path)) 轉換成相對路徑
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
//		resolve 是 java.nio.file.Path 類的一個方法，用於將相對路徑或子路徑解析為當前路徑的結果。
		//相當於轉成絕對路徑
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if(resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
//		deleteRecursively(File file): 遞迴刪除指定的檔案或目錄，包括其中的所有內容。
//		copyRecursively(File src, File dest): 遞迴複製源檔案或目錄到目標位置，包括其中的所有內容。
//		moveRecursively(File src, File dest): 遞迴移動源檔案或目錄到目標位置，包括其中的所有內容。
//		cleanDirectory(File directory): 清空指定目錄下的所有內容，但保留該目錄本身。
	}

	@Override
	public void init() {
		try {
			Files.createDirectory(rootLocation);
			//創建資料夾
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
