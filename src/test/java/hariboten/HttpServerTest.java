package hariboten;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;

import javax.management.RuntimeErrorException;

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
					<title>Hello, world!</title>
				</head>
				<body>
					Hello, world!
				</body>
			</html>
			""";
	private static final String ANOTHER_HTML = """
			<html>
				<head>
					<title>Another file</title>
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

	class StubFileLoader implements FileLoader {

		@Override
		public InputStream open(String path) throws FileNotFoundException {
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

	@Nested
	class OnTcpTest {
		HttpServer httpServer = new HttpServer(8080, DOCUMENT_ROOT);

		@BeforeEach
		public void startServer() {
			httpServer = new HttpServer(8080, DOCUMENT_ROOT);
			new Thread(httpServer).start();
		}

		@AfterEach
		private void stopServer() {
			httpServer.stop();
		}

		private HttpResponse<String> sendRequest(HttpRequest request, HttpClient client) {
			try {
				return client.send(request, BodyHandlers.ofString());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		private HttpRequest createRequest(String location) {
			return HttpRequest.newBuilder()
					.uri(URI.create("http://127.0.0.1:8080/" + location))
					.build();
		}

		private HttpClient createClient() {
			return HttpClient.newBuilder()
					.version(Version.HTTP_1_1)
					.build();
		}

		@Test
		public void testOnTcp() {
			HttpRequest request = createRequest("");
			HttpResponse<String> res = this.sendRequest(request, createClient());

			assertEquals(res.statusCode(), 200);
			assertEquals(res.body(), INDEX_HTML);
		}

		@Test
		public void testAnotherPath() {
			HttpRequest request = createRequest("/another.html");
			HttpResponse<String> res = this.sendRequest(request, createClient());

			assertEquals(res.statusCode(), 200);
			assertEquals(res.body(), ANOTHER_HTML);
		}

		@Test
		public void testDoubleRequest() {
			HttpClient client = createClient();
			HttpRequest request = createRequest("");
			HttpRequest anotherRequest = createRequest("/another.html");

			HttpResponse<String> res = this.sendRequest(request, client);
			HttpResponse<String> anoterResponse = this.sendRequest(anotherRequest, client);

			assertEquals(res.statusCode(), 200);
			assertEquals(res.body(), INDEX_HTML);

			assertEquals(anoterResponse.statusCode(), 200);
			assertEquals(anoterResponse.body(), ANOTHER_HTML);
		}

		@Test
		public void testKeepAlive() {
			try (Socket socket = new Socket("localhost", 8080);
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
				new StringReader(INDEX_HTML).transferTo(writer);
				new StringReader(ANOTHER_HTML).transferTo(writer);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
