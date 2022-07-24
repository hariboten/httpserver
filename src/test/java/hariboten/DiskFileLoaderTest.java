
package hariboten;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;



class DiskFileLoaderTest {
	private static final String DOCUMENT_ROOT = "/Users/pc220206/Documents/engineer_training/http/server/http_server/html/";
	@Test
	public void testDirectoryTraversal() {
		FileLoader fileLoader = new DiskFileLoader(DOCUMENT_ROOT);
		
		assertThrows(FileNotFoundException.class, () -> {
			fileLoader.open("../secret");
		});
	}
}
