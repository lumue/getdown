package io.github.lumue.getdown.webapp.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages SseEmitters for an Endpoint
 */
public class SseEmitters {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SseEmitters.class);
	private final List<SseEmitter> emitters = new ArrayList<>();
	
	public SseEmitter newEmitter(){
		SseEmitter emitter = new SseEmitter(6000000L);
		
		this.emitters.add(emitter);
		emitter.onCompletion(() -> {
			synchronized (this.emitters) {
				this.emitters.remove(emitter);
			}
		});
		
		emitter.onTimeout(emitter::complete);
		
		return emitter;
	}
	
	public <T>  void sendMessage(Message<T> message){
		List<SseEmitter> activeEmitters = new ArrayList<>(emitters);
		activeEmitters.forEach(sseEmitter -> {
			try {
				sseEmitter.send(message);
			} catch (Throwable e) {
				sseEmitter.completeWithError(e);
				LOGGER.error("Error sending Message to "+sseEmitter,e);
			}
		});
	}
	
}
