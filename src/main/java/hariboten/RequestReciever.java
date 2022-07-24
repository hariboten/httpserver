package hariboten;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class RequestReciever {
	private final BufferedReader in;

    public RequestReciever(InputStream in) {
		this.in = new BufferedReader(new InputStreamReader(in));
    }

    public String recv() throws IOException{
		String statusLine = in.readLine();
		String[] token = statusLine.split(" ");
		if (token.length != 3) {
			throw new IOException("recieve request with illegal statusLine.");
		}

		String httpMethod = token[0];
		if (!httpMethod.equals("GET")) {
			throw new IOException("Do not accept execpt GET request yet.");
		}
        return statusLine.split(" ")[1];
    }
}
