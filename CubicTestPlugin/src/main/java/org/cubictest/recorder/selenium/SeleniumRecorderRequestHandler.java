package org.cubictest.recorder.selenium;

import java.io.IOException;

import org.cubictest.recorder.IRecorder;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.AbstractHttpHandler;

public class SeleniumRecorderRequestHandler extends AbstractHttpHandler {
    private final IRecorder recorder;

	public SeleniumRecorderRequestHandler(IRecorder recorder) {
		this.recorder = recorder;
	}

	public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
    	System.out.println("Recording: " + request.toString());
    	
    	request.setHandled(true);
	}
}
