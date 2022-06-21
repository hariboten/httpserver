package com.example;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;

public class HttpRequest {
	private final String method;
	private final String path;
	private final String version;
	private final HashMap<String, String> headers;
	private final StringReader body;

	private HttpRequest() {
		this.method = "GET";
		this.path = "/";
		this.version = "HTTP/1.1";
		this.headers = new HashMap<String, String>();
		this.body = new StringReader("");
	}
}
