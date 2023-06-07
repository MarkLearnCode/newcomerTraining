package com.example.uploadingfiles;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.uploadingfiles.storage.FileSystemStorageService;
import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;

@Controller
public class FileUploadController {
	private final StorageService storageService;

	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}
//	@Autowired
//	private FileSystemStorageService fileSystemStorageService;

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {
//		Model model將物件傳送至Thymeleaf網頁(return 網頁名稱)，
//		model.addAttribute(變數名稱,內容)
		// 列出已上傳的檔案並將檔案連結傳送至Thymeleaf網頁

        // 使用 StorageService 的 loadAll 方法取得所有已上傳的檔案路徑
        // 將每個檔案路徑轉換為可存取的 URL 並加入至 model 中
//		MvcUriComponentsBuilder 是 Spring Framework 提供的一個實用工具類，
//		用於建構 URI 的組件。這裡使用 fromMethodName 方法
//		根據控制器類 (FileUploadController) 和方法名稱 (serveFile) 創建 URI。
		List<String> pathTest = new ArrayList<>();
		pathTest = storageService.loadAll()
				//這邊返回Stream<Path>
				.map(path -> MvcUriComponentsBuilder
						.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
						.build().encode().toUri().toString())
				.collect(Collectors.toList());
		pathTest.forEach(e->System.out.println(e));
//		return http://localhost:8080/files/NOTICE.txt
//		fromMethodName(FileUploadController.class, 
//		"serveFile", path.getFileName().toString()) 的目的是建立一個 URI，該 URI 將指向 FileUploadController 類別中的 serveFile 方法。
//		path.getFileName().toString() 
//		則是將檔案路徑的檔案名稱轉換為字串，作為 serveFile 方法的參數傳遞。

		
		model.addAttribute("files",
				storageService.loadAll()
						.map(path -> MvcUriComponentsBuilder
								.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
								.build().encode().toUri().toString())
						.collect(Collectors.toList()));
		return "uploadForm";
	}

	//下載
	@GetMapping("/files/{filename:.+}")
//	:.+ 為正則表達式
//	/files/document.txt  to  document.txt
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		// 提供下載特定檔案的功能
		System.out.println(filename);

        // 使用 StorageService 的 loadAsResource 方法讀取指定檔案的 Resource
        
		Resource file = storageService.loadAsResource(filename);
		// 回傳 ResponseEntity 物件，設定檔案下載的相關標頭資訊和內容
        
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	
	//上傳
	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		// 處理檔案上傳的功能

        // 使用 StorageService 的 store 方法將檔案儲存
		storageService.store(file);
		
		// 設定重導訊息，用於顯示成功上傳的訊息
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");
		// 重導至首頁
		return "redirect:/";
	}

	// 處理檔案不存在的異常
    // 回傳 404 Not Found 錯誤
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
