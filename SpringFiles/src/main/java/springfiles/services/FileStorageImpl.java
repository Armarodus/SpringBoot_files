package springfiles.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import springfiles.dao.FileStorage;

@Service
public class FileStorageImpl implements FileStorage {

	private final Logger logger = LoggerFactory.getLogger(FileStorageImpl.class);// Зробити логи более детальними, юзати DEBUG,
																	// ERROR, INFO
	private final Path rootLocation = Paths.get("filestorage");

	@Override
	public void store(MultipartFile file) {
		try {
			logger.info("Try to load file {} to server ...",file.getName());
			Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
			logger.info(file.getName()+" Is loaded to server!");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}

	@Override
	public Resource download(String filename) {
		try {
			Path file = rootLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				logger.info("Return file {} ",filename);
				return resource;
			} else {
				logger.error("File {} not exists!",filename);
				throw new RuntimeException("FAIL!");
			}
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Error! -> message = " + e.getMessage());
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
		logger.info("All files deleted!");
	}

	@Override
	public Stream<Path> getPath() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("\"Failed to read stored file");
		}
	}

	@Override
	public void delete(String filename) {
		try {
			Path deleteFile = rootLocation.resolve(filename);
			FileSystemUtils.deleteRecursively(deleteFile);
			logger.info("File {} deleted!",filename);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

	}

}
