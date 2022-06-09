package com.example;

/**
 * Hello world!
 *
 */

import java.io.IOException;

public class App 
{
    public static void main( String[] args )
    {
	    EchoServer echoServer = new EchoServer();
	    try {
		    echoServer.start();
	    } catch(IOException e) {
		    System.err.println("an error occured");
	    }
    }
}
