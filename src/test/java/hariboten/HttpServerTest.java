package hariboten;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;

class HttpServerTest {
	private static final String REQUEST_ROOT = """
		GET / HTTP/1.1
		host: localhost

		""";
	private static final String REQUEST_ANOTHER = """
		GET /another.html HTTP/1.1
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
	private static final String STATUS_LINE_200 = "HTTP/1.1 200 OK";
	private static final String HEADER = "Content-Type: text/html; charset=utf-8";
	private static final String EXPECT_ROOT = STATUS_LINE_200 + "\n" + HEADER + "\n\n" + INDEX_HTML;
	private static final String EXPECT_ANOTHER = STATUS_LINE_200 + "\n" + HEADER + "\n\n" + ANOTHER_HTML;
	private static final String DOCUMENT_ROOT = "/Users/pc220206/Documents/engineer_training/http/server/http_server/html/";


	class StubFileLoader implements FileLoader{

		@Override
		public InputStream open(String path) throws FileNotFoundException{
			if (path.equals("/")) {
				return new ByteArrayInputStream(INDEX_HTML.getBytes());
			}
			if (path.equals("/index.html")) {
				return new ByteArrayInputStream(INDEX_HTML.getBytes());
			}
			if (path.equals("/another.html")) {
				return new ByteArrayInputStream(ANOTHER_HTML.getBytes());
			}
			throw new FileNotFoundException();
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

		FileLoader fileLoader = new DiskFileLoader(DOCUMENT_ROOT);
		
		Runnable webserver = new WebServer(in, out, fileLoader);
		webserver.run();

		assertEquals(EXPECT_ROOT, out.toString());
	}

	@Test
	public void testLearnFile() {
		File file = new File("/");
		assertEquals("", file.getName());
		File index = new File("/index.html");
		assertEquals("index.html", index.getName());
		File parentpath = new File("../../../secret");
		assertEquals("secret", parentpath.getName());
		File endWithSlash = new File("/index.html/");
		assertEquals("index.html", endWithSlash.getName());
	}

	static class OnTcpTest {
		@BeforeEach
		public void startServer() {
			HttpServer httpServer = new HttpServer(8080, DOCUMENT_ROOT);
			new Thread(httpServer).start();
		}

		@Test
		public void testOnTcp() {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://127.0.0.1:8080"))
				.build();

			HttpResponse<String> res;
			try {
				res = HttpClient.newBuilder()
				.version(Version.HTTP_1_1)
				.build()
				.send(request, BodyHandlers.ofString());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			assertEquals(res.statusCode(), 200);
			assertEquals(res.body(), INDEX_HTML);
		}

		@Test
		public void testAnotherPath() {
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://127.0.0.1:8080/another.html"))
				.build();

			HttpResponse<String> res;
			try {
				res = HttpClient.newBuilder()
				.version(Version.HTTP_1_1)
				.build()
				.send(request, BodyHandlers.ofString());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			assertEquals(res.statusCode(), 200);
			assertEquals(res.body(), ANOTHER_HTML);
		}
	}
}
