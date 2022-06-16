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
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;

public class EchoServer {
	private final int portNumber;
	private static final String PATH = "./www/index.html";
	private static final int BUFF_SIZE = 1024;

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
				/* PrintWriter writer = new PrintWriter(
							soc.getOutputStream(), true);
							*/

				BufferedOutputStream out = new BufferedOutputStream(soc.getOutputStream());

				byte[] buff = new byte[BUFF_SIZE];
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(PATH));
				int readBytes;
				while ((readBytes = in.read(buff)) != -1) {
					out.write(buff, 0, readBytes);
				}
				out.flush();
				out.close();
				in.close();

				/*
				reader.lines().forEach(line -> {
					writer.println(line);
					System.out.println(line);
				});
				*/

				reader.close();
				//writer.close();
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
