package com.example.notification;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.core.ApiFuture;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.FileInputStream;
import java.util.concurrent.Executors;

public class NotificationVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        // Inicializar Firebase con tu archivo de credenciales
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json"); // Asegúrate de que esta ruta sea correcta
        @SuppressWarnings("deprecation")
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        FirebaseApp.initializeApp(options);

        // Crear el router para manejar las rutas de la API
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());  // Para manejar cuerpos de solicitud POST

        // Endpoint para recibir la señal desde Quarkus
        router.post("/sendNotification").handler(ctx -> {
            // Obtener los datos del cuerpo de la solicitud
            @SuppressWarnings("deprecation")
            String email = ctx.getBodyAsJson().getString("email");
            @SuppressWarnings("deprecation")
            String messageBody = ctx.getBodyAsJson().getString("message");

            // Crear la notificación para Firebase
            Message message = Message.builder()
                    .putData("email", email)
                    .putData("message", messageBody)
                    .setTopic("global")  // Puedes cambiarlo a un topic o token específico si es necesario
                    .build();

            // Enviar la notificación de manera asíncrona
            ApiFuture<String> future = FirebaseMessaging.getInstance().sendAsync(message);

            // Manejar el futuro de Firebase con addListener
            future.addListener(() -> {
                try {
                    // Obtener el resultado del futuro
                    String response = future.get();
                    ctx.response().setStatusCode(200).end("Notificación enviada con éxito: " + response);
                } catch (Exception e) {
                    // Manejar cualquier excepción al enviar la notificación
                    ctx.response().setStatusCode(500).end("Error al enviar la notificación: " + e.getMessage());
                }
            }, Executors.newSingleThreadExecutor());
        });

        // Iniciar el servidor HTTP en el puerto 8081
        vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
            if (http.succeeded()) {
                System.out.println("Servidor de notificaciones iniciado en el puerto 8888");
            } else {
                System.out.println("Error al iniciar el servidor de Vert.x");
            }
        });
    }
}
