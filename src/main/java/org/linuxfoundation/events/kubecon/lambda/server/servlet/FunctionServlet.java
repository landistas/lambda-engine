package org.linuxfoundation.events.kubecon.lambda.server.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.linuxfoundation.events.kubecon.lambda.context.Context;
import org.linuxfoundation.events.kubecon.lambda.server.executor.FunctionExecutor;

public class FunctionServlet extends HttpServlet {

	private static final long serialVersionUID = -54708638576020722L;
	


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			Context ctx = new Context();
			FunctionExecutor.instance().execute(request.getInputStream(), response.getOutputStream(), ctx);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		OutputStream responseOutputStream = response.getOutputStream();
		responseOutputStream.write("Function server is up and running".getBytes());

		response.setStatus(200);
	}
}
