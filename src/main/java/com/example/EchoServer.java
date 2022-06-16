package com.example;

import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.stream.Stream;

public class EchoServer {
	private final int portNumber;

	public EchoServer() {
		this.portNumber = 8080;
	}
	public EchoServer(int portNumber) {
		this.portNumber = portNumber;
	}

	private void task(Socket soc) {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
							soc.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter writer = new PrintWriter(
							soc.getOutputStream(), true);

				writer.println("Hello, World!");
				System.out.println("Hello, World!");

				/*
				reader.lines().forEach(line -> {
					writer.println(line);
					System.out.println(line);
				});
				*/

				reader.close();
				writer.close();
			} catch (IOException ignore) {
			}finally {
				try {
					soc.close();
				} catch (IOException ignore) {
				}
			}
	}

	public void start() throws IOException {
		System.out.println("start echo server: port = " + this.portNumber);

		ServerSocket socket = new ServerSocket();
		socket.bind(new InetSocketAddress("0.0.0.0", portNumber));

		while (true) {
			Socket accepted = socket.accept();

			new Thread() {
				@Override
				public void run() {
					task(accepted);
				}
			}.start();
		}
	}
}
