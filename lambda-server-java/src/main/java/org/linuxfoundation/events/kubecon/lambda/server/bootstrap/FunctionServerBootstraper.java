package org.linuxfoundation.events.kubecon.lambda.server.bootstrap;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.linuxfoundation.events.kubecon.lambda.server.executor.FunctionExecutor;
import org.linuxfoundation.events.kubecon.lambda.server.servlet.FunctionServlet;

public class FunctionServerBootstraper {
	
	
	public static void main(String[] args) {
		try {
			String functionFullName = System.getenv("FUNCTION_ENTRYPOINT");

			FunctionExecutor.setup(functionFullName);

			QueuedThreadPool threadPool = new QueuedThreadPool();
			threadPool.setMaxThreads(500);

			SslContextFactory sslContextFactory = new SslContextFactory(true);
			SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
			
			
			Server server = new Server(threadPool);
			ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(), sslConnectionFactory);
			http.setPort(9443);

			server.addConnector(http);

			ServletHandler handler = new ServletHandler();
			server.setHandler(handler);
			handler.addServletWithMapping(FunctionServlet.class, "/*");

			server.start();

			server.join();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
