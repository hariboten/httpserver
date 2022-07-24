package hariboten;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

class WebServer implements Runnable {
	private final InputStream in;
	private final OutputStream out;

	WebServer(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}


	@Override
	public void run() {
		String fakeOut = """
		http/1.1 200 OK

		<html>
			<head>
				<title>Hello, world!<title>
			</head>
			<body>
				Hello, world!
			</body>
		</html>
		""";

		final PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
		writer.print(fakeOut);
		writer.close();
	}
}
