package com.buynearme.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Pair;
import com.buynearme.client.network.NetworkClient;
import com.om.server.Response;
import com.om.dm.Store;
import com.om.dm.Product;
import com.om.algorithm.Edge;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

public class AdminController {
    // FXML fields for main layout
    @FXML private VBox rootVBox;
    @FXML private TabPane tabPane;
    @FXML private Tab storesTab;
    @FXML private Tab nodesTab;
    @FXML private Tab edgesTab;

    // FXML fields for Algorithm Switcher
    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private Label currentAlgorithmLabel;

    // FXML fields for stores
    @FXML private TableView<Store> storesTable;
    @FXML private TableColumn<Store, Integer> storeIdColumn;
    @FXML private TableColumn<Store, String> storeNameColumn;
    @FXML private TableColumn<Store, String> storeLocationColumn;
    @FXML private TableColumn<Store, Void> storeProductsColumn;
    @FXML private TableColumn<Store, Void> storeDeleteColumn;

    // FXML fields for products
    @FXML private ComboBox<Store> productStoreCombo;
    @FXML private TextField productName;
    @FXML private TextField productPrice;

    // FXML fields for nodes
    @FXML private TextField nodeName;
    @FXML private TextField nodeX;
    @FXML private TextField nodeY;
    @FXML private TableView<Map.Entry<String, double[]>> nodesTable;
    @FXML private TableColumn<Map.Entry<String, double[]>, String> nodeNameColumn;
    @FXML private TableColumn<Map.Entry<String, double[]>, Double> nodeXColumn;
    @FXML private TableColumn<Map.Entry<String, double[]>, Double> nodeYColumn;
    @FXML private TableColumn<Map.Entry<String, double[]>, Void> nodeDeleteColumn;

    // FXML fields for edges
    @FXML private ComboBox<String> edgeFromCombo;
    @FXML private ComboBox<String> edgeToCombo;
    @FXML private TextField edgeWeight;
    @FXML private TableView<Edge> edgesTable;
    @FXML private TableColumn<Edge, String> edgeFromColumn;
    @FXML private TableColumn<Edge, String> edgeToColumn;
    @FXML private TableColumn<Edge, Double> edgeWeightColumn;
    @FXML private TableColumn<Edge, Void> edgeDeleteColumn;

    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        loadInitialData();
        setupTabPaneListener();
        setupAlgorithmSwitcher();
    }

    private void setupTableColumns() {
        // Store columns
        storeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        storeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        storeLocationColumn.setCellValueFactory(new PropertyValueFactory<>("locationId"));
        
        // Setup products column with View button
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
        
        // Setup delete column with Delete button
        storeDeleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Store store = getTableView().getItems().get(getIndex());
                    if (store != null) {
                        deleteStore(store);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        
        // Node columns - using Map.Entry for the backend data structure
        nodeNameColumn.setCellValueFactory(cellData -> {
            String nodeName = cellData.getValue().getKey();
            return new javafx.beans.property.SimpleStringProperty(nodeName);
        });
        nodeXColumn.setCellValueFactory(cellData -> {
            double[] coords = cellData.getValue().getValue();
            return new javafx.beans.property.SimpleDoubleProperty(coords[0]).asObject();
        });
        nodeYColumn.setCellValueFactory(cellData -> {
            double[] coords = cellData.getValue().getValue();
            return new javafx.beans.property.SimpleDoubleProperty(coords[1]).asObject();
        });
        
        nodeDeleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Map.Entry<String, double[]> node = getTableView().getItems().get(getIndex());
                    if (node != null) {
                        deleteNode(node.getKey());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
        
        // Edge columns
        edgeFromColumn.setCellValueFactory(new PropertyValueFactory<>("fromNode"));
        edgeToColumn.setCellValueFactory(new PropertyValueFactory<>("toNode"));
        edgeWeightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        edgeDeleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Edge edge = getTableView().getItems().get(getIndex());
                    if (edge != null) {
                        deleteEdge(edge);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }

    private void setupComboBoxes() {
        // Setup store ComboBox to display store names
        productStoreCombo.setCellFactory(param -> new ListCell<Store>() {
            @Override
            protected void updateItem(Store item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (ID: " + item.getId() + ")");
                }
            }
        });
        
        productStoreCombo.setButtonCell(new ListCell<Store>() {
            @Override
            protected void updateItem(Store item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (ID: " + item.getId() + ")");
                }
            }
        });
    }

    private void loadInitialData() {
        loadStores();
        loadNodes();
        loadEdges();
    }

    @FXML
    private void onHomeButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BuyNearMeApplication.class.getResource("HomeView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("BuyNearMe - Store Locator System");
        stage.setScene(scene);
    }

    @FXML
    private void onRefreshStoresClick() {
        loadStores();
    }

    @FXML
    private void onAddStoreClick() {
        // Create custom dialog for store creation
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Store");
        dialog.setHeaderText("Enter store details");
        dialog.setResizable(true);

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the custom content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField storeNameField = new TextField();
        storeNameField.setPromptText("Store name");
        ComboBox<String> locationCombo = new ComboBox<>();
        locationCombo.setPromptText("Select location");
        locationCombo.setPrefWidth(200);

        // Load available locations
        try {
            Response response = NetworkClient.getAllNodes();
            if (response != null && response.isSuccess()) {
                List<String> nodes = gson.fromJson(gson.toJson(response.getBody()), 
                    new TypeToken<List<String>>(){}.getType());
                locationCombo.setItems(FXCollections.observableArrayList(nodes));
            }
        } catch (IOException e) {
            showError("Error loading locations: " + e.getMessage());
        }

        grid.add(new Label("Store Name:"), 0, 0);
        grid.add(storeNameField, 1, 0);
        grid.add(new Label("Location:"), 0, 1);
        grid.add(locationCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the store name field by default
        Platform.runLater(storeNameField::requestFocus);

        // Convert the result to a pair when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String storeName = storeNameField.getText().trim();
                String locationId = locationCombo.getValue();
                
                if (storeName.isEmpty()) {
                    showError("Please enter a store name");
                    return null;
                }
                if (locationId == null) {
                    showError("Please select a location");
                    return null;
                }
                
                return new Pair<>(storeName, locationId);
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            try {
                Response response = NetworkClient.addStore(pair.getKey(), pair.getValue());
                if (response != null && response.isSuccess()) {
                    showInfo("Store added successfully!");
                    loadStores();
                } else {
                    showError("Error adding store: " + (response != null ? response.getMessage() : "Unknown error"));
                }
            } catch (IOException e) {
                showError("Error adding store: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onAddProductClick() {
        try {
            // Validate input fields
            Store selectedStore = productStoreCombo.getValue();
            if (selectedStore == null) {
                showError("Please select a store");
                return;
            }
            
            if (productName.getText().trim().isEmpty() || 
                productPrice.getText().trim().isEmpty()) {
                showError("Please fill in Product Name and Price");
                return;
            }
            
            String name = productName.getText().trim();
            double price = Double.parseDouble(productPrice.getText());
            
            // Validate price
            if (price <= 0) {
                showError("Price must be greater than 0");
                return;
            }
            
            Response response = NetworkClient.addProductToStore(selectedStore.getId(), name, price);
            if (response.isSuccess()) {
                showInfo("Product added successfully!");
                productStoreCombo.getSelectionModel().clearSelection();
                productName.clear();
                productPrice.clear();
                loadStores();
            } else {
                showError("Error adding product: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid price");
        } catch (IOException e) {
            showError("Error adding product: " + e.getMessage());
        }
    }

    @FXML
    private void onRefreshNodesClick() {
        loadNodes();
    }

    @FXML
    private void onAddNodeClick() {
        try {
            String name = nodeName.getText();
            double x = Double.parseDouble(nodeX.getText());
            double y = Double.parseDouble(nodeY.getText());
            
            Response response = NetworkClient.addNode(name, x, y);
            if (response.isSuccess()) {
                showInfo("Node added successfully!");
                nodeName.clear();
                nodeX.clear();
                nodeY.clear();
                loadNodes();
                loadEdges(); // Refresh edge combos
            } else {
                showError("Error adding node: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for X and Y coordinates");
        } catch (IOException e) {
            showError("Error adding node: " + e.getMessage());
        }
    }

    @FXML
    private void onRefreshEdgesClick() {
        loadEdges();
    }

    @FXML
    private void onAddEdgeClick() {
        try {
            String from = edgeFromCombo.getValue();
            String to = edgeToCombo.getValue();
            double weight = Double.parseDouble(edgeWeight.getText());
            
            if (from == null || to == null) {
                showError("Please select both from and to nodes");
                return;
            }
            
            Response response = NetworkClient.addEdge(from, to, weight);
            if (response.isSuccess()) {
                showInfo("Edge added successfully!");
                edgeWeight.clear();
                loadEdges();
            } else {
                showError("Error adding edge: " + response.getMessage());
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid weight");
        } catch (IOException e) {
            showError("Error adding edge: " + e.getMessage());
        }
    }

    @FXML
    private void onClearAllDataClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear All Data");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This will delete all stores, products, nodes, and edges. This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Response backendResponse = NetworkClient.clearAllData();
                    if (backendResponse.isSuccess()) {
                        showInfo("All data cleared successfully!");
                        loadInitialData();
                    } else {
                        showError("Error clearing data: " + backendResponse.getMessage());
                    }
                } catch (IOException e) {
                    showError("Error clearing data: " + e.getMessage());
                }
            }
        });
    }

    private void loadStores() {
        try {
            Response response = NetworkClient.getAllStores();
            if (response.isSuccess()) {
                List<Store> stores = gson.fromJson(gson.toJson(response.getBody()), 
                    new TypeToken<List<Store>>(){}.getType());
                storesTable.setItems(FXCollections.observableArrayList(stores));
                productStoreCombo.setItems(FXCollections.observableArrayList(stores));
            }
        } catch (IOException e) {
            showError("Error loading stores: " + e.getMessage());
        }
    }

    private void loadNodes() {
        try {
            // First try to get nodes with coordinates
            Response response = NetworkClient.getAllNodesWithCoordinates();
            if (response.isSuccess()) {
                Map<String, double[]> nodesWithCoords = gson.fromJson(gson.toJson(response.getBody()), 
                    new TypeToken<Map<String, double[]>>(){}.getType());
                
                // Convert to list for table display
                List<Map.Entry<String, double[]>> nodes = nodesWithCoords.entrySet().stream().toList();
                nodesTable.setItems(FXCollections.observableArrayList(nodes));
                
                // Update edge combos with node names
                List<String> nodeNames = nodesWithCoords.keySet().stream().toList();
                edgeFromCombo.setItems(FXCollections.observableArrayList(nodeNames));
                edgeToCombo.setItems(FXCollections.observableArrayList(nodeNames));
            } else {
                // Fallback to just node names if coordinates method not available
                response = NetworkClient.getAllNodes();
                if (response.isSuccess()) {
                    List<String> nodeNames = gson.fromJson(gson.toJson(response.getBody()), 
                        new TypeToken<List<String>>(){}.getType());
                    
                    // Update edge combos
                    edgeFromCombo.setItems(FXCollections.observableArrayList(nodeNames));
                    edgeToCombo.setItems(FXCollections.observableArrayList(nodeNames));
                    
                    // Create entries with placeholder coordinates
                    List<Map.Entry<String, double[]>> nodes = nodeNames.stream()
                        .map(name -> Map.entry(name, new double[]{0.0, 0.0}))
                        .toList();
                    
                    nodesTable.setItems(FXCollections.observableArrayList(nodes));
                }
            }
        } catch (IOException e) {
            showError("Error loading nodes: " + e.getMessage());
        }
    }

    private void loadEdges() {
        try {
            Response response = NetworkClient.getAllEdges();
            if (response.isSuccess()) {
                Map<String, List<Edge>> edgesMap = gson.fromJson(gson.toJson(response.getBody()), 
                    new TypeToken<Map<String, List<Edge>>>(){}.getType());
                // Convert map to list for table display
                List<Edge> allEdges = edgesMap.values().stream()
                    .flatMap(List::stream)
                    .toList();
                edgesTable.setItems(FXCollections.observableArrayList(allEdges));
            }
        } catch (IOException e) {
            showError("Error loading edges: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showProductsDialog(Store store) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Products in " + store.getName());
        dialog.setHeaderText("Manage products for " + store.getName());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setResizable(true);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        if (store.getProducts() == null || store.getProducts().isEmpty()) {
            content.getChildren().add(new Label("No products available at this store."));
        } else {
            ListView<Product> productListView = new ListView<>();
            ObservableList<Product> productItems = FXCollections.observableArrayList(store.getProducts());
            productListView.setItems(productItems);

            productListView.setCellFactory(param -> new ListCell<>() {
                private final Label productLabel = new Label();
                private final Button deleteButton = new Button("Delete");
                private final Region spacer = new Region();
                private final HBox hbox = new HBox(10);

                {
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    hbox.getChildren().addAll(productLabel, spacer, deleteButton);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> {
                        Product product = getItem();
                        if (product != null) {
                            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmDialog.setTitle("Delete Product");
                            confirmDialog.setHeaderText("Delete '" + product.getName() + "' from '" + store.getName() + "'?");
                            confirmDialog.setContentText("This action cannot be undone.");

                            confirmDialog.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    try {
                                        Response res = NetworkClient.removeProductFromStore(store.getId(), product.getName());
                                        if (res.isSuccess()) {
                                            showInfo("Product '" + product.getName() + "' removed successfully!");
                                            productListView.getItems().remove(product);
                                            loadStores();
                                        } else {
                                            showError("Error removing product: " + res.getMessage());
                                        }
                                    } catch (IOException e) {
                                        showError("Network error while removing product: " + e.getMessage());
                                    }
                                }
                            });
                        }
                    });
                }

                @Override
                protected void updateItem(Product product, boolean empty) {
                    super.updateItem(product, empty);
                    if (empty || product == null) {
                        setGraphic(null);
                    } else {
                        productLabel.setText(product.getName() + " - $" + String.format("%.2f", product.getPrice()));
                        setGraphic(hbox);
                    }
                }
            });

            content.getChildren().add(productListView);
        }

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void deleteStore(Store store) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Store");
        alert.setHeaderText("Are you sure you want to delete '" + store.getName() + "'?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Response response = NetworkClient.deleteStore(store.getId());
                if (response != null && response.isSuccess()) {
                    showInfo("Store deleted successfully!");
                    loadStores();
                } else {
                    showError("Error deleting store: " + (response != null ? response.getMessage() : "Unknown error"));
                }
            } catch (IOException e) {
                showError("Error deleting store: " + e.getMessage());
            }
        }
    }

    private void deleteNode(String nodeName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Node");
        alert.setHeaderText("Are you sure you want to delete node '" + nodeName + "'?");
        alert.setContentText("This will also remove all edges connected to this node. This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Response response = NetworkClient.removeNode(nodeName);
                if (response != null && response.isSuccess()) {
                    showInfo("Node deleted successfully!");
                    loadNodes(); // Reload nodes and edges
                } else {
                    showError("Error deleting node: " + (response != null ? response.getMessage() : "Unknown error"));
                }
            } catch (IOException e) {
                showError("Error deleting node: " + e.getMessage());
            }
        }
    }

    private void deleteEdge(Edge edge) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Edge");
        alert.setHeaderText("Are you sure you want to delete the edge between '" + edge.getFromNode() + "' and '" + edge.getToNode() + "'?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Response response = NetworkClient.removeEdge(edge.getFromNode(), edge.getToNode());
                if (response != null && response.isSuccess()) {
                    showInfo("Edge deleted successfully!");
                    loadEdges();
                } else {
                    showError("Error deleting edge: " + (response != null ? response.getMessage() : "Unknown error"));
                }
            } catch (IOException e) {
                showError("Error deleting edge: " + e.getMessage());
            }
        }
    }

    private void setupTabPaneListener() {
        // Set initial background
        updateBackground(storesTab); 

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            updateBackground(newTab);
        });
    }

    private void updateBackground(Tab selectedTab) {
        String imageUrl = "";
        if (selectedTab == storesTab) {
            imageUrl = "/images/admin-stores-bg.jpg";
        } else if (selectedTab == nodesTab) {
            imageUrl = "/images/admin-nodes-bg.jpg";
        } else if (selectedTab == edgesTab) {
            imageUrl = "/images/admin-edges-bg.jpg";
        }
        
        if (!imageUrl.isEmpty()) {
            String style = "-fx-background-image: url('" + imageUrl + "'); " +
                           "-fx-background-size: cover; " +
                           "-fx-background-position: center;";
            rootVBox.setStyle(style);
        }
    }

    private void setupAlgorithmSwitcher() {
        algorithmComboBox.setItems(FXCollections.observableArrayList("Dijkstra", "A*"));
        // Load initial value, and ONLY after that, add the listener.
        // This prevents the listener from firing on the initial programmatic value set.
        loadCurrentAlgorithm(() -> {
            algorithmComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                if (newValue != null && !newValue.equals(oldValue)) {
                    updateAlgorithm(newValue);
                }
            });
        });
    }

    private void loadCurrentAlgorithm() {
        loadCurrentAlgorithm(null);
    }

    private void loadCurrentAlgorithm(Runnable onFinished) {
        new Thread(() -> {
            try {
                Response response = NetworkClient.sendRequest("algorithm/getCurrent", new HashMap<>());
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess() && response.getBody() != null) {
                        String currentAlgo = response.getBody().toString();
                        if (currentAlgo.contains("Dijkstra")) {
                            currentAlgorithmLabel.setText("(Current: Dijkstra)");
                            algorithmComboBox.setValue("Dijkstra");
                        } else if (currentAlgo.contains("AStar")) {
                            currentAlgorithmLabel.setText("(Current: A*)");
                            algorithmComboBox.setValue("A*");
                        } else {
                            currentAlgorithmLabel.setText("(Current: Unknown)");
                        }
                    } else {
                        currentAlgorithmLabel.setText("(Current: Unknown)");
                    }
                    
                    if (onFinished != null) {
                        onFinished.run();
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    currentAlgorithmLabel.setText("(Current: Error)");
                     if (onFinished != null) {
                        onFinished.run();
                    }
                });
            }
        }).start();
    }

    private void updateAlgorithm(String algorithm) {
        String endpoint = "";
        if ("Dijkstra".equals(algorithm)) {
            endpoint = "algorithm/useDijkstra";
        } else if ("A*".equals(algorithm)) {
            endpoint = "algorithm/useAStar";
        } else {
            return; // Should not happen
        }

        final String finalEndpoint = endpoint;
        new Thread(() -> {
            try {
                Response response = NetworkClient.sendRequest(finalEndpoint, new HashMap<>());
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        showInfo("Successfully switched algorithm to " + algorithm);
                        loadCurrentAlgorithm(); // Refresh current algorithm display
                    } else {
                        showError("Failed to switch algorithm: " + (response != null ? response.getMessage() : "Unknown error"));
                        loadCurrentAlgorithm(); // Re-sync with server state
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Error connecting to server: " + e.getMessage());
                    loadCurrentAlgorithm(); // Re-sync with server state
                });
            }
        }).start();
    }
} 