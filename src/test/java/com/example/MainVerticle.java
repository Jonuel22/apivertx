package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    // Crea un router para manejar las rutas de la API
    Router router = Router.router(vertx);

    // Define una ruta de prueba
    router.get("/api/hello").handler(ctx -> {
      ctx.response()
        .putHeader("content-type", "application/json")
        .end("{\"message\":\"Hello from Vert.x!\"}");
    });

    // Inicia el servidor HTTP en el puerto 8080
    vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        System.out.println("Servidor HTTP iniciado en el puerto 8080");
      } else {
        System.err.println("Error al iniciar el servidor: " + http.cause());
      }
    });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
