package hariboten;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class WebServer implements Runnable {
	private final InputStream in;
	private final OutputStream out;
	private final FileLoader fileLoader;
	private static final String STATUS_LINE_200 = "http/1.1 200 OK";

	public WebServer(InputStream in, OutputStream out, FileLoader fileLoader) {
		this.in = in;
		this.out = new BufferedOutputStream(out);
		this.fileLoader = fileLoader;
    }


    @Override
	public void run() {
		try {
			new ByteArrayInputStream((STATUS_LINE_200 + "\n\n").getBytes())
				.transferTo(out);
			fileLoader.open("/")
				.transferTo(out);
			out.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
