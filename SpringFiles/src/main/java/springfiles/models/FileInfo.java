package springfiles.models;

import lombok.Data;

@Data
public class FileInfo {

	private String filename;
	private String url;
	
	public FileInfo(String filename, String url) {
		super();
		this.filename = filename;
		this.url = url;
	}
	
	
}
