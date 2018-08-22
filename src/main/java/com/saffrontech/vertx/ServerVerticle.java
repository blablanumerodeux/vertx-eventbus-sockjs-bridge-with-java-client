package com.saffrontech.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class ServerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx = Vertx.vertx();
        Router router = Router.router(vertx);

// events specific to THOPs are made available over the bridge
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions options = new BridgeOptions();
        options.addOutboundPermitted(new PermittedOptions().setAddress("test"));
        options.addInboundPermitted(new PermittedOptions().setAddress("test"));
        sockJSHandler.bridge(options);

        router.route("/bridge/*").handler(sockJSHandler);
        vertx.createHttpServer().requestHandler(router::accept).listen(8765, (res) -> {
            System.out.println("I'm here to serve");
        });
    }
}
