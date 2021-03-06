package cn.linkmore.server;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExtendEmbeddedServletContainerFactory extends TomcatEmbeddedServletContainerFactory {
	protected void customizeConnector(Connector connector) {
		super.customizeConnector(connector);
		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler(); 
		protocol.setMaxConnections(5000); 
		protocol.setMaxThreads(5000);
		protocol.setConnectionTimeout(60000);
	}
}
