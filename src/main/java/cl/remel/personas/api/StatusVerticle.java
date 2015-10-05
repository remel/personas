package cl.remel.personas.api;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusVerticle extends AbstractVerticle {
	
	private static final Logger log = LoggerFactory.getLogger(StatusVerticle.class);
	private EventBus eb;
	
	@Override
	public void start() {
		 eb = vertx.eventBus();
		 eb.consumer("app.status",doStatus());
	}
	
	private Handler<Message<JsonObject>> doStatus() {
		return new Handler<Message<JsonObject>>(){
			
			class EventData {
				JsonObject mysqlStatus;
			}

			ConcurrentHashMap<Message<JsonObject>, EventData> eventData = new ConcurrentHashMap<Message<JsonObject>, EventData>();
			
			@Override
			public void handle(Message<JsonObject> event) {
				EventData ed=new EventData();
				eventData.put(event, ed);
				
				//TODO: Revisar los demas componentes
				eb.send("mysql.status", new JsonObject(), doneStatus("mysql",event));
				
			}

			private Handler<AsyncResult<Message<JsonObject>>> doneStatus(String componente, Message<JsonObject> event) {
				return new Handler<AsyncResult<Message<JsonObject>>>(){

					@Override
					public void handle(AsyncResult<Message<JsonObject>> message) {
						EventData ed=eventData.get(event);
						JsonObject resp=new JsonObject();
						if(message.succeeded()){
				    		 resp=(JsonObject) message.result().body();
				    	 }else{
				    		 log.error(componente+".status failed");
				    		 resp.put(componente+".status","failed. Cause: "+message.cause().getMessage());
				    	 }
						if(componente.equals("mysql")){
							ed.mysqlStatus=resp;
						}
						
						if(ed.mysqlStatus!=null){
							eventData.remove(event);
							JsonObject eventResp=new JsonObject()
								.put("app","ok")
								.put("mysql",ed.mysqlStatus);
							event.reply(eventResp);
						}else{
							eventData.put(event, ed);
						}
						
						
					}
					
				};
			}
			
		};
	}

}
