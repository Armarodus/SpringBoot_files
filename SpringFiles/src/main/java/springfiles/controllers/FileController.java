package springfiles.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import springfiles.models.FileInfo;
import springfiles.services.FileStorageImpl;

@RestController
public class FileController {

	@Autowired
	FileStorageImpl fileStorageImpl;

	@PostMapping("/")
	public String uploadMultipartFile(@RequestParam("uploadfile") MultipartFile file, Model model) {
		try {
			fileStorageImpl.store(file);
			model.addAttribute("message", "File uploaded successfully! -> filename = " + file.getOriginalFilename());
		} catch (Exception e) {
			model.addAttribute("message", "Fail! -> uploaded filename: " + file.getOriginalFilename());
		}
		return "multipartfile/uploadform.html";
	}

	@GetMapping("/files")
	public String getListFiles(Model model) {

		List<FileInfo> fileInfos = fileStorageImpl.loadFiles().map(path -> {
			String filename = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(FileController.class, "downloadFile", path.getFileName().toString()).build()
					.toString();
			return new FileInfo(filename, url);
		}).collect(Collectors.toList());

		model.addAttribute("files", fileInfos);
		String paths = "";
		for (FileInfo filePaths : fileInfos) {
			paths += (filePaths.getFilename() + "\n");
		}
		return paths;
	}

	/*
	 * Download Files
	 */
	@GetMapping("/files/{filename}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
		Resource file = fileStorageImpl.loadFile(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	
	@DeleteMapping(value = "{fileName}")
	public void deleteFile(@PathVariable String fileName) {
		fileStorageImpl.deleteFile(fileName);
	}
	
	
}
