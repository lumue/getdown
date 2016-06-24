package io.github.lumue.getdown.app.springboot.web;

import io.github.lumue.getdown.core.download.job.ThrottelingDownloadJobEventTap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import reactor.bus.EventBus;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/downloads");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOrigins("*")
				.withSockJS();
	}

	@Bean
	public ThrottelingDownloadJobEventTap throttelingDownloadJobEventTap(EventBus eventbus,
			@Value("${getdown.websocket.broadcastinterval.downloads}") long throttleinterval) {
		return new ThrottelingDownloadJobEventTap(eventbus, "ws-downloads", throttleinterval);
	}


}