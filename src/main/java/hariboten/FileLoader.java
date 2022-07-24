package hariboten;

import java.io.FileNotFoundException;
import java.io.InputStream;

interface FileLoader {
	public InputStream open(String path) throws FileNotFoundException;
}
