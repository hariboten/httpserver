import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public abstract class HttpThread implements Runnable{
	private final InputStream in;
	private final String docRoot;
	
	public HttpThread(InputStream in, String docRoot) {
		this.in = in;
		this.docRoot = docRoot;
	}

	public void run() {
		while (true) {
			var request = recieveRequest(in);
			var file = openFile(request, docRoot);
			sendResponse(request, file);
		}
	}

	abstract HttpRequest recieveRequest(InputStream in) throws IOException;
	abstract InputStream openFile(HttpRequest request, String docRoot) throws IOException;
	abstract void sendResponse(HttpRequest request, InputStream file) throws IOException;
}
