package hariboten;

import java.io.InputStream;

interface FileLoader {
	public InputStream open(String path);
}
