package hariboten;

import java.io.FileInputStream;
import java.io.InputStream;

class DiskFileLoader implements FileLoader {
	String documentRoot;

	public DiskFileLoader(String documentRoot) {
		this.documentRoot = documentRoot;
    }

    @Override
	public InputStream open(String path) {
		return new FileInputStream(documentRoot + path);
	}
}
