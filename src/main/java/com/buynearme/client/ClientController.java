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
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class ClientController {

    @FXML
    private TableView<Store> storesTable;
    @FXML
    private TableColumn<Store, Integer> storeIdColumn;
    @FXML
    private TableColumn<Store, String> storeNameColumn;
    @FXML
    private TableColumn<Store, String> storeLocationColumn;
    @FXML
    private TableColumn<Store, Void> storeProductsColumn;

    private final NetworkClient networkClient = new NetworkClient();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        storeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        storeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        storeLocationColumn.setCellValueFactory(new PropertyValueFactory<>("locationId"));
        setupProductsColumn();

        onRefreshStoresClick(null);
    }

    private void setupProductsColumn() {
        storeProductsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white;");
                viewButton.setOnAction(event -> {
                    Store store = getTableView().getItems().get(getIndex());
                    if (store != null) {
                        showProductsDialog(store);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
    }

    private void showProductsDialog(Store store) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Products in " + store.getName());
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        if (store.getProducts() == null || store.getProducts().isEmpty()) {
            vbox.getChildren().add(new Label("No products available at this store."));
        } else {
            ListView<String> listView = new ListView<>();
            List<String> productInfo = store.getProducts().stream()
                    .map(p -> p.getName() + " - $" + String.format("%.2f", p.getPrice()))
                    .toList();
            listView.setItems(FXCollections.observableArrayList(productInfo));
            vbox.getChildren().add(listView);
        }

        dialog.getDialogPane().setContent(vbox);
        dialog.showAndWait();
    }

    @FXML
    void onRefreshStoresClick(ActionEvent event) {
        try {
            Response response = networkClient.getAllStores();
            if (response.isSuccess()) {
                List<Store> stores = gson.fromJson(gson.toJson(response.getBody()), new TypeToken<List<Store>>(){}.getType());
                storesTable.setItems(FXCollections.observableArrayList(stores));
            } else {
                System.err.println("Error fetching stores: " + response.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onHomeButtonClick(ActionEvent event) throws IOException {
        BuyNearMeApplication.showHome();
    }

    @FXML
    void onFindNearestStoreClick(ActionEvent event) throws IOException {
        BuyNearMeApplication.showFindNearestStoreView();
    }

    @FXML
    void onFindCheapestStoreClick(ActionEvent event) throws IOException {
        BuyNearMeApplication.showFindCheapestStoreView();
    }
} 