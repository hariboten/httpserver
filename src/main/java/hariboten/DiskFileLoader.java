package hariboten;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

class DiskFileLoader implements FileLoader {
	private String documentRoot;
	private static final String ROOT = "/";

	public DiskFileLoader(String documentRoot) {
		this.documentRoot = documentRoot;
    }

    @Override
	public InputStream open(String path) throws FileNotFoundException {
		if (path.equals(ROOT)) {
			path = "/index.html";
		}
		return new FileInputStream(documentRoot + path);
	}
}
