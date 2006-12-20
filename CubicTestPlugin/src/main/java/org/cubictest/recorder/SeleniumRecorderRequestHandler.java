package org.cubictest.recorder;

import java.io.IOException;

import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.AbstractHttpHandler;

public class SeleniumRecorderRequestHandler extends AbstractHttpHandler {
    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
    	System.out.println("Recording: " + request.toString());
    	request.setHandled(true);
	}
}
