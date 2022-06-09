package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;

public class EchoServer {
	private final int portNumber;

	public EchoServer() {
		this.portNumber = 8080;
	}
	public EchoServer(int portNumber) {
		this.portNumber = portNumber;
	}

	public void start() throws IOException {
		System.out.println("start echo server: port = " + this.portNumber);

		ServerSocket socket = new ServerSocket();
		socket.bind(new InetSocketAddress("0.0.0.0", portNumber));

		while (true) {
			Socket accepted = socket.accept();
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
							accepted.getInputStream()))
				OutputStream out = accepted.getOutputStream();
				byte[] buf = new byte[4096];
				int len = in.read(buf);
				out.write(buf, 0, len);

			} finally {
				accepted.close();
			}
		}
	}
}
