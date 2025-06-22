package com.buynearme.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class HomeController {
    
    @FXML
    private void onClientButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BuyNearMeApplication.class.getResource("ClientView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("BuyNearMe - Client");
        stage.setScene(scene);
    }
    
    @FXML
    private void onAdminButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BuyNearMeApplication.class.getResource("AdminView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("BuyNearMe - Admin");
        stage.setScene(scene);
    }
} 