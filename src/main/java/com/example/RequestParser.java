package com.example;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

public class RequestParser {
	private final String method;
	private final String path;
	private final String version;
	private HashMap<String, String> headers;
	private static final String GET = "GET";
	private static final String HOST_HEADER = "Host";

	private RequestParser(String method, String path, String version) {
		this.method = method;
		this.path = path;
		this.version = version;
	}

	public RequestParser readRequest(BufferedReader reader) {
		try {
			RequestParser request = parse_method_and_version(reader.readLine());
			reader.lines()
				.takeWhile(s -> {return (s != "");})
				.map(s -> parse_header(s))
				.forEach((pair) -> {
					this.headers.put(pair.left, pair.right);
				});
			/* if (!headers.containsKey(HOST_HEADER)) {
			}
			*/
			return request;
		} catch (IOException e) {
		}
	}

	private RequestParser parse_method_and_version(String req) {
		String[] splitted = req.split(" ", 3);
		/*if (splitted.length != 3) {
		}
		*/
		return new RequestParser(splitted[0], splitted[1], splitted[2]);
	}

	private Pair<String, String> parse_header(String line) {
		String[] splitted = line.split(":", 2);
		/*if (splitted.length != 2) {
		}
		*/
		return (new Pair<String, String>(splitted[0].trim(), splitted[1].trim()));
	}
}

