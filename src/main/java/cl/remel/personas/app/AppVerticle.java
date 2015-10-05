package cl.remel.personas.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.remel.personas.api.StatusVerticle;

public class AppVerticle extends AbstractVerticle {
	
	private static final Logger log = LoggerFactory.getLogger(AppVerticle.class);
	
	public static void main(String[] args) {
		 Vertx.vertx().deployVerticle(AppVerticle.class.getName());
	}
	public void start(){
		log.info("Starting AppVerticle");
		JsonObject config = new JsonObject();
		DeploymentOptions options = new DeploymentOptions().setConfig(config);
		vertx.deployVerticle(WebServerVerticle.class.getName(),options,res -> {
    		log.debug(WebServerVerticle.class.getName()+" deployed");
		});
		vertx.deployVerticle(StatusVerticle.class.getName(),options,res -> {
    		log.debug(StatusVerticle.class.getName()+" deployed");
		});
	}
	
	

}
