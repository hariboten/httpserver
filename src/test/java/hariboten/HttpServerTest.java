package hariboten;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

class HttpServerTest {
	private static final String REQUEST_ROOT = """
		GET / http/1.1
		host: localhost
		""";
	private static final String INDEX_HTML = """
		<html>
			<head>
				<title>Hello, world!<title>
			</head>
			<body>
				Hello, world!
			</body>
		</html>
		""";
	private static final String EXPECT_ROOT = "http/1.1 200 OK\n\n" + INDEX_HTML;

	class StubFileLoader implements FileLoader{

		@Override
		public InputStream open(String path) {
			return new ByteArrayInputStream(INDEX_HTML.getBytes());
		}
	}

	@Test
	public void testResponseFromFile() {
		InputStream in = new ByteArrayInputStream(REQUEST_ROOT.getBytes());
		OutputStream out = new ByteArrayOutputStream();

		FileLoader fileLoader = new StubFileLoader();
		
		Runnable webserver = new WebServer(in, out, fileLoader);
		webserver.run();

		assertEquals(EXPECT_ROOT, out.toString());
	}

	@Test
	public void testHttpRequest() {
		InputStream in = new ByteArrayInputStream(REQUEST_ROOT.getBytes());
		OutputStream out = new ByteArrayOutputStream();
		
		Runnable webserver = new WebServer(in, out);
		webserver.run();

		assertEquals(EXPECT_ROOT, out.toString());
	}
}
