package com.example;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
							accepted.getInputStream()));
				
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(
							accepted.getOutputStream()));
				reader.lines().forEach(line -> {
					try {
						writer.write(line, 0, line.length());
					} catch (IOException e){
						System.err.println("IOException write error");
					}
					System.out.println(line);
				});
				reader.close();
				writer.close();
				//byte[] buf = new byte[4096];
				//int len = in.read(buf);
				//out.write(buf, 0, len);

			} finally {
				accepted.close();
			}
		}
	}
}
