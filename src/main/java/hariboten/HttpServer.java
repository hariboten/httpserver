package hariboten;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

class HttpServer implements Runnable {
	private final int port;
	private final String documentRoot;
	private ServerSocket server;

	public HttpServer(int port, String documentRoot) {
		this.port = port;
		this.documentRoot = documentRoot;
	}

	@Override
	public void run() {
		try (ServerSocket server = new ServerSocket()) {
			this.server = server;
			server.bind(new InetSocketAddress("0.0.0.0", port));

			while (true) {
				Socket socket = server.accept();
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();

				FileLoader fileLoader = new DiskFileLoader(documentRoot);

				new Thread(new WebServer(in, out, fileLoader))
						.start();
			}
		} catch (SocketException e) {
			return ;
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new UncheckedIOException(e);
		}
	}
	
	public void stop() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
