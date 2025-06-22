package com.buynearme.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BuyNearMeApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showHome();
    }

    public static void showHome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BuyNearMeApplication.class.getResource("HomeView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setTitle("BuyNearMe - Store Locator System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showAdminView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BuyNearMeApplication.class.getResource("AdminView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setTitle("BuyNearMe - Admin Dashboard");
        primaryStage.setScene(scene);
    }

    public static void showClientView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BuyNearMeApplication.class.getResource("ClientView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        primaryStage.setTitle("BuyNearMe - Client Dashboard");
        primaryStage.setScene(scene);
    }

    public static void showFindNearestStoreView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BuyNearMeApplication.class.getResource("FindNearestStoreView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setTitle("BuyNearMe - Find Nearest Store");
        primaryStage.setScene(scene);
    }

    public static void showFindCheapestStoreView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BuyNearMeApplication.class.getResource("FindCheapestStoreView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setTitle("BuyNearMe - Find Cheapest Store");
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
} 