package hariboten;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WebServer implements Runnable {
	private final InputStream in;
	private final OutputStream out;
	private final FileLoader fileLoader;
	private static final String STATUS_LINE_200 = "HTTP/1.1 200 OK";

	public WebServer(InputStream in, OutputStream out, FileLoader fileLoader) {
		this.in = in;
		this.out = new BufferedOutputStream(out);
		this.fileLoader = fileLoader;
    }

	private InputStream createHeader() {
		return new ByteArrayInputStream(
				(STATUS_LINE_200 + "\n" + "Content-Type: text/html; charset=utf-8" + "\n\n")
				.getBytes());
	}

	private void send(InputStream header, InputStream body) throws IOException {
			header.transferTo(out);
			body.transferTo(out);
	}

    @Override
	public void run() {
		try {
			RequestReciever requestReciever = new RequestReciever(in);
			String path = requestReciever.recv();

			InputStream header = createHeader();
			InputStream body = fileLoader.open(path);

			send(header, body);

			out.close();
			in.close();
		} catch (IOException e) {
			System.err.println(e);
			return ;
		}
	}
}
