
package hariboten;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class RequestRecieverTest {
	private static final String REQUEST_ROOT = """
		GET / http/1.1
		host: localhost

		""";
	private static final String REQUEST_ANOTHER = """
		GET /another http/1.1
		host: localhost

		""";

	@Test
	public void testRecieveRequestPath() {
		InputStream in = new ByteArrayInputStream(REQUEST_ROOT.getBytes());
		RequestReciever reciever = new RequestReciever(in);
		String path = null;
		try {
			path = reciever.recv();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		String expected = "/";
		assertEquals(expected, path);
	}
	
	@Test
	public void testAnotherRequesetPath() {
		InputStream in = new ByteArrayInputStream(REQUEST_ANOTHER.getBytes());
		RequestReciever reciever = new RequestReciever(in);
		String path = null;
		try {
			path = reciever.recv();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		String expected = "/another";
		assertEquals(expected, path);
	}

	@Test
	public void testIllegalRequest() {
		final String illegalRequest = """
			GET
			host: localhost

			""";
		InputStream in = new ByteArrayInputStream(illegalRequest.getBytes());
		RequestReciever reciever = new RequestReciever(in);
		IOException e = assertThrows(IOException.class, () -> {reciever.recv();});
		assertEquals("recieve request with illegal statusLine.", e.getMessage());
	}

	@Test
	public void testPostRequest() {
		final String postRequest = """
			Post / http/1.1
			host: localhost
			Content-Type: txt
			Content-Length: 13

			some messages

			""";
		InputStream in = new ByteArrayInputStream(postRequest.getBytes());
		RequestReciever reciever = new RequestReciever(in);
		IOException e = assertThrows(IOException.class, () -> {reciever.recv();});
		assertEquals("Do not accept execpt GET request yet.", e.getMessage());
	}
}
