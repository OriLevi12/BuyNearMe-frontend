package com.buynearme.client;

import com.buynearme.client.network.NetworkClient;
import com.google.gson.Gson;
import com.om.dm.Product;
import com.om.dm.Store;
import com.om.server.Response;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class FindCheapestStoreController {

    @FXML
    private TextField cheapestProductName;

    @FXML
    private TextArea cheapestResultArea;

    private final NetworkClient networkClient = new NetworkClient();
    private final Gson gson = new Gson();

    @FXML
    void onFindCheapestClick(ActionEvent event) {
        String productName = cheapestProductName.getText();
        if (productName.isEmpty()) {
            cheapestResultArea.setText("Please enter a product name.");
            return;
        }
        try {
            Response response = networkClient.findCheapestStoreWithProduct(productName);
            if (response.isSuccess()) {
                Store store = gson.fromJson(gson.toJson(response.getBody()), Store.class);
                if (store != null) {
                    StringBuilder result = new StringBuilder("Cheapest Store Found:\n");
                    result.append("ID: ").append(store.getId()).append("\n");
                    result.append("Name: ").append(store.getName()).append("\n");
                    result.append("Location: ").append(store.getLocationId()).append("\n");
                    result.append("Products:\n");
                    for(Product p : store.getProducts()) {
                        if(p.getName().equalsIgnoreCase(productName)) {
                            result.append("  - ").append(p.getName()).append(": $").append(p.getPrice()).append(" (Cheapest!)").append("\n");
                        }
                    }
                    cheapestResultArea.setText(result.toString());
                } else {
                    cheapestResultArea.setText("No store found with that product.");
                }
            } else {
                cheapestResultArea.setText("Error: " + response.getMessage());
            }
        } catch (IOException e) {
            cheapestResultArea.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    void onBackButtonClick(ActionEvent event) throws IOException {
        BuyNearMeApplication.showClientView();
    }
} 