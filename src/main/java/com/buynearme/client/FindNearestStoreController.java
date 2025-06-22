package com.buynearme.client;

import com.buynearme.client.network.NetworkClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.om.dm.Product;
import com.om.dm.Store;
import com.om.server.Response;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class FindNearestStoreController {

    @FXML
    private TextField nearestProductName;

    @FXML
    private ComboBox<String> nearestLocationCombo;

    @FXML
    private TextArea nearestResultArea;

    private final NetworkClient networkClient = new NetworkClient();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        loadLocations();
    }

    private void loadLocations() {
        try {
            Response response = networkClient.getAllNodes();
            if (response.isSuccess()) {
                List<String> locations = gson.fromJson(gson.toJson(response.getBody()), new TypeToken<List<String>>(){}.getType());
                nearestLocationCombo.setItems(FXCollections.observableArrayList(locations));
            } else {
                nearestResultArea.setText("Error loading locations: " + response.getMessage());
            }
        } catch (IOException e) {
            nearestResultArea.setText("Error loading locations: " + e.getMessage());
        }
    }

    @FXML
    void onFindNearestClick(ActionEvent event) {
        String productName = nearestProductName.getText();
        String location = nearestLocationCombo.getValue();

        if (productName.isEmpty() || location == null) {
            nearestResultArea.setText("Please enter a product name and select a location.");
            return;
        }

        try {
            Response response = networkClient.findNearestStoreWithProduct(location, productName);
            if(response.isSuccess()) {
                Store store = gson.fromJson(gson.toJson(response.getBody()), Store.class);
                if (store != null) {
                    StringBuilder result = new StringBuilder();
                    result.append("✅ Found nearest store!\n\n");
                    result.append("🏪 Store: ").append(store.getName()).append("\n");
                    result.append("📍 Location: ").append(store.getLocationId()).append("\n");
                    result.append("📏 Distance: ").append(String.format("%.2f", store.getDistanceToStore())).append(" units\n");

                    if (store.getPathToStore() != null && !store.getPathToStore().isEmpty()) {
                        result.append("🛣️ Path: ").append(String.join(" → ", store.getPathToStore())).append("\n");
                    }

                    result.append("\n📦 Products:\n");
                    for (Product product : store.getProducts()) {
                        if (product.getName().equalsIgnoreCase(productName)) {
                            result.append("  • ").append(product.getName()).append(" - $").append(product.getPrice()).append("\n");
                        }
                    }

                    nearestResultArea.setText(result.toString());
                } else {
                    nearestResultArea.setText("❌ No store found with the specified product");
                }
            } else {
                nearestResultArea.setText("❌ Error: " + response.getMessage());
            }
        } catch (IOException e) {
            nearestResultArea.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    void onBackButtonClick(ActionEvent event) throws IOException {
        BuyNearMeApplication.showClientView();
    }
} 