package hariboten;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

class DiskFileLoader implements FileLoader {
	private String documentRoot;

	public DiskFileLoader(String documentRoot) {
		this.documentRoot = documentRoot;
    }

    @Override
	public InputStream open(String path) throws FileNotFoundException {
		String filename = new File(path).getName();
		if (filename.equals("")) {
			filename = "index.html";
		}
		return new FileInputStream(documentRoot + "/" + filename);
	}
}
