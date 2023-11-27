package org.aruiz;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CronometroApp extends Application {

    private Label labelTiempo;
    private Button btnIniciar, btnDetenerReanudar, btnReiniciar;
    private boolean enEjecucion = false;
    private boolean detenido = false;
    private long milisegundos = 0;
    private Thread hiloCronometro;

    @Override
    public void start(Stage stage) {
        labelTiempo = new Label("Tiempo: 00:00:00.000");
        labelTiempo.setStyle("-fx-text-fill: aqua; -fx-font-size: 20px;");

        btnIniciar = new Button("Iniciar");
        btnIniciar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnIniciar.setOnAction(e -> iniciarCronometro());

        btnDetenerReanudar = new Button("Detener");
        btnDetenerReanudar.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white;");
        btnDetenerReanudar.setOnAction(e -> detenerReanudarCronometro());

        btnReiniciar = new Button("Reiniciar");
        btnReiniciar.setStyle("-fx-background-color: #4169E1; -fx-text-fill: white;");
        btnReiniciar.setDisable(true); // Inicialmente desactivado
        btnReiniciar.setOnAction(e -> reiniciarCronometro());

        HBox botonesLayout = new HBox(10, btnIniciar, btnDetenerReanudar, btnReiniciar);
        botonesLayout.setAlignment(Pos.CENTER);

        VBox cronometroLayout = new VBox(10, labelTiempo);
        cronometroLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, cronometroLayout, botonesLayout);
        layout.setStyle("-fx-background-color: #2C3E50; -fx-padding: 20;");
        layout.setPrefSize(400, 200);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setTitle("Cronómetro");
        stage.show();
    }

    private void iniciarCronometro() {
        if (!enEjecucion) {
            enEjecucion = true;
            detenido = false;
            btnIniciar.setDisable(true);
            btnDetenerReanudar.setText("Detener");
            btnReiniciar.setDisable(true);

            milisegundos = 0;
            long startTime = System.currentTimeMillis();

            hiloCronometro = new Thread(() -> {
                while (enEjecucion) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!detenido) {
                        long elapsedTime = System.currentTimeMillis() - startTime;
                        milisegundos = elapsedTime;
                        Platform.runLater(() -> labelTiempo.setText("Tiempo: " + formatarTiempo(elapsedTime)));
                    }
                }

                Platform.runLater(() -> {
                    btnIniciar.setDisable(false);
                    btnDetenerReanudar.setText("Detener");
                    btnReiniciar.setDisable(false);
                });
            });
            hiloCronometro.setDaemon(true);
            hiloCronometro.start();
        }
    }

    private String formatarTiempo(long milisegundos) {
        long totalSegundos = milisegundos / 1000;
        long horas = totalSegundos / 3600;
        long minutos = (totalSegundos % 3600) / 60;
        long segundos = totalSegundos % 60;
        long restoMilisegundos = milisegundos % 1000;

        return String.format("%02d:%02d:%02d.%03d", horas, minutos, segundos, restoMilisegundos);
    }

    private void detenerReanudarCronometro() {
        if (enEjecucion) {
            detenido = !detenido;
            if (detenido) {
                btnDetenerReanudar.setText("Reanudar");
                btnReiniciar.setDisable(false);
            } else {
                btnDetenerReanudar.setText("Detener");
                btnReiniciar.setDisable(true);
            }
        }
    }

    private void reiniciarCronometro() {
        enEjecucion = false;
        detenido = false;
        btnIniciar.setDisable(false);
        btnDetenerReanudar.setText("Detener");
        btnReiniciar.setDisable(true);
        try {
            if (hiloCronometro != null) {
                hiloCronometro.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> labelTiempo.setText("Tiempo: 00:00:00.000"));
    }

    public static void main(String[] args) {
        launch();
    }
}
