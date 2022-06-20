package com.example;

import java.util.HashMap;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;


public class HttpResponse {
	private final String version;
	private final int status;
	private final String statusString;
	private final HashMap<String, String> headers;
	private final InputStream body;

	private HttpResponse(BuildReady builder) {
		this.version = builder.version;
		this.status = builder.status;
		this.statusString = builder.statusString;
		this.headers = builder.headers;
		this.body = builder.body;
	}

	public void sendResponse(OutputStream out) throws IOException {
		BufferedOutputStream outputStream = new BufferedOutputStream(out);
		new ByteArrayInputStream(
				new StringBuilder()
				.append(version)
				.append(" ")
				.append(status)
				.append(" ")
				.append(statusString)
				.toString()
				.getBytes(StandardCharsets.UTF_8))
			.transferTo(outputStream);

		for (HashMap.Entry<String, String> entry: headers.entrySet()) {
			new ByteArrayInputStream(
					new StringBuilder()
					.append(entry.getKey())
					.append(": ")
					.append(entry.getValue())
					.toString()
					.getBytes())
				.transferTo(outputStream);
		}

		body.transferTo(outputStream);
		outputStream.close();
	}


	public static class Builder {
		private final String version;

		public Builder() {
			this("HTTP/1.1");
		}

		public Builder(String version) {
			this.version = version;
		}

		public BuilderWithStatusLine status(int status, String statusString) {
			return new BuilderWithStatusLine(this, status, statusString);
		}
	}

	public static class BuilderWithStatusLine {
		private final Builder version;
		private final int status;
		private final String statusString;

		private BuilderWithStatusLine(Builder builder, int status, String statusString) {
			this.version = builder;
			this.status = status;
			this.statusString = statusString;
		}
		
		public BuilderWithHeaders header(String key, String value) {
			var headers = new HashMap<String, String>();
			headers.put(key, value);
			return new BuilderWithHeaders(this, headers);
		}
	}

	public static class BuilderWithHeaders {
		private final BuilderWithStatusLine statusLine;
		private HashMap<String, String> headers;

		private BuilderWithHeaders(BuilderWithStatusLine builder, HashMap<String, String> headers) {
			this.statusLine = builder;
			this.headers = headers;
		}

		public BuilderWithHeaders header(String key, String value) {
			this.headers.put(key, value);
			return this;
		}

		public BuildReady body(InputStream body) {
			return new BuildReady(this, body);
		}
	}

	public static class BuildReady {
		private final String version;
		private final int status;
		private final String statusString;
		private final HashMap<String, String> headers;
		private final InputStream body;

		private BuildReady(BuilderWithHeaders builder, InputStream body) {
			this.version = builder.statusLine.version.version;
			this.status = builder.statusLine.status;
			this.statusString = builder.statusLine.statusString;
			this.headers = builder.headers;
			this.body = body;
		}
		
		public HttpResponse build() {
			return new HttpResponse(this);
		}
	}

}
