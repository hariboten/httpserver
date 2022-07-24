package hariboten;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

class HttpServerTest {
	@Test
	public void testHttpRequest() {
		String request = """
			GET / http/1.1
			host: localhost
			""";
		String expect = """
			http/1.1 200 OK

			<html>
				<head>
					<title>Hello, world!<title>
				</head>
				<body>
					Hello, world!
				</body>
			</html>
			""";

		InputStream in = new ByteArrayInputStream(request.getBytes());
		OutputStream out = new ByteArrayOutputStream();
		
		Runnable webserver = new WebServer();
		webserver.run();

		assertEquals(expect, out.toString());
	}
}
