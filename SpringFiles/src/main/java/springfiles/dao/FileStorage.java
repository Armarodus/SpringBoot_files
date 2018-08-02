package springfiles.dao;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

	public void store(MultipartFile file);

	public Resource download(String filename);

	public void deleteAll();

	public void delete(String filename);

	public Stream<Path> getPath();
}
