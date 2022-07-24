package hariboten;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String DOCUMENT_ROOT = "/Users/pc220206/Documents/engineer_training/http/server/http_server/html/";

    public static void main( String[] args )
    {
		HttpServer httpServer = new HttpServer(8080, DOCUMENT_ROOT);
		new Thread(httpServer).start();
    }
}
