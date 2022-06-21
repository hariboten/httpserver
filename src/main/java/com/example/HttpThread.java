package com.example;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public abstract class HttpThread implements Runnable{
	private final InputStream in;
	private final OutputStream out;
	private final String docRoot;
	
	public HttpThread(final InputStream in, final OutputStream out, final String docRoot) {
		this.in = in;
		this.out = out;
		this.docRoot = docRoot;
	}

	public void run() {
		try {
			while (true) {
				var request = recieveRequest(in);
				var file = openFile(request, docRoot);
				sendResponse(request, file, out);
			}
		} catch (final IOException e) {
			handleIOException(e);
		}
	}

	abstract HttpRequest recieveRequest(final InputStream in) throws IOException;
	abstract InputStream openFile(final HttpRequest request, final String docRoot) throws IOException;
	abstract void sendResponse(final HttpRequest request, final InputStream file, final OutputStream out) throws IOException;
	abstract void handleIOException(final IOException e);
}
