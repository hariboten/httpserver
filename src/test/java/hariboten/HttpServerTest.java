package hariboten;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
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
	private static final String REQUEST_ANOTHER = """
		GET /another http/1.1
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
	private static final String ANOTHER_HTML = """
		<html>
			<head>
				<title>Another file<title>
			</head>
			<body>
				Hello, another world!
			</body>
		</html>
		""";
	private static final String STATUS_LINE_200 = "http/1.1 200 OK";
	private static final String EXPECT_ROOT = STATUS_LINE_200 + "\n\n" + INDEX_HTML;
	private static final String EXPECT_ANOTHER = STATUS_LINE_200 + "\n\n" + ANOTHER_HTML;

	class StubFileLoader implements FileLoader{

		@Override
		public InputStream open(String path) {
			if (path.equals("/")) {
				return new ByteArrayInputStream(INDEX_HTML.getBytes());
			}
			if (path.equals("/another")) {
				return new ByteArrayInputStream(ANOTHER_HTML.getBytes());
			}
			throw new RuntimeException(new IllegalArgumentException());
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
	public void testAnotherRequesetPath() {
		InputStream in = new ByteArrayInputStream(REQUEST_ANOTHER.getBytes());
		OutputStream out = new ByteArrayOutputStream();

		FileLoader fileLoader = new StubFileLoader();
		
		Runnable webserver = new WebServer(in, out, fileLoader);
		webserver.run();

		assertEquals(EXPECT_ANOTHER, out.toString());
	}

	@Test
	public void testDiskFile() {
		InputStream in = new ByteArrayInputStream(REQUEST_ROOT.getBytes());
		OutputStream out = new ByteArrayOutputStream();

		FileLoader fileLoader = new DiskFileLoader("./html");
		
		Runnable webserver = new WebServer(in, out, fileLoader);
		webserver.run();

		assertEquals(EXPECT_ROOT, out.toString());
	}
}
