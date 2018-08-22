package com.saffrontech.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
//import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.net.URI;

public class MainClass {

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);

        // https://github.com/saffron-technology/vertx-eventbusbridge
        
        //server
// events specific to THOPs are made available over the bridge
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions options = new BridgeOptions();
        options.addOutboundPermitted(new PermittedOptions().setAddress("test"));
        options.addInboundPermitted(new PermittedOptions().setAddress("test"));
/*        sockJSHandler.bridge(options, ws -> {
            vertx.eventBus().consumer("test", message -> {
//                System.out.println("message consumed by the handler hadoc");
                ws.socket().write(Buffer.buffer(new JsonObject().put("type", "publish").put("address", "test").put("body", "hello").toString()));
//                System.out.println("message wrote again in the socket");
            });
        });*/
//        sockJSHandler.bridge(options,new EventHandler<BridgeEvent>());
        sockJSHandler.bridge(options);


        router.route("/bridge/*").handler(sockJSHandler);
        vertx.createHttpServer().requestHandler(router::accept).listen(8765, (res) -> {
            System.out.println("I'm here to serve");
        });

        //client
        EventBusBridge.connect(URI.create("http://localhost:8765/bridge"), eb -> {
            eb.registerHandler("test", msg -> System.out.println("I got a message:" + msg.body()));
//            eb.publish("test", "hello");
        });

        //server keeps talking
        while (true) {
            vertx.eventBus().publish("test", "sdfsdfs");
            Thread.sleep(1000);
        }
    }
}
