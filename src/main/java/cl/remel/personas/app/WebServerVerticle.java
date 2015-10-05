package cl.remel.personas.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServerVerticle extends AbstractVerticle {
	private static final Logger log = LoggerFactory.getLogger(WebServerVerticle.class);
	
	Router router = Router.router(vertx);

	private WebServerVerticle that = this;
	private EventBus eb;

	@Override
	public void start() {
		eb = vertx.eventBus();
		Router router = Router.router(vertx);
		
		router.route().handler(CorsHandler.create("*"));

		router.route().handler(BodyHandler.create());
		router.get("/status").handler(that::handleStatus);
		
		
		//API V1
		
		//Static
		router.route("/*").handler(StaticHandler.create());
				

		String portStr = System.getProperty("port")!=null ? System.getProperty("port") : "8080";
		vertx.createHttpServer().requestHandler(router::accept).listen(Integer.parseInt(portStr));
		log.info("Listening on port:"+portStr);
	}
	
	private void handleStatus(RoutingContext routingContext) {
	    HttpServerResponse response = routingContext.response();
	    eb.send("app.status", new JsonObject(), message -> {
	    	 if(message.succeeded()){
	    		 JsonObject status=(JsonObject) message.result().body();
	    		 response.putHeader("content-type", "application/json").end(status.encode());
	    	 }else{
	    		 sendError(500,response);
	    	 }
	    });
	}

	private void sendError(int statusCode, HttpServerResponse response) {
		sendError(statusCode,null,response);
	}

	private void sendError(int statusCode, String message, HttpServerResponse response) {
		response.setStatusCode(statusCode).end(message);
	}
}
