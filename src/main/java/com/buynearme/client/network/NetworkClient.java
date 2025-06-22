package com.buynearme.client.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.om.dm.Store;
import com.om.dm.Product;
import com.om.algorithm.Edge;
import com.om.server.Request;
import com.om.server.Response;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class NetworkClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private static final Gson gson = new GsonBuilder().create();

    public static Response sendRequest(String action, Map<String, Object> body) throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Prepare headers
            Map<String, String> headers = new HashMap<>();
            headers.put("action", action);

            // Create request object
            Request<Map<String, Object>> request = new Request<>(headers, body);

            // Convert to JSON and send
            String jsonRequest = gson.toJson(request);
            writer.println(jsonRequest);
            writer.println(); // Empty line to signal end of message

            // Read response
            String responseLine;
            StringBuilder responseBuilder = new StringBuilder();
            while ((responseLine = reader.readLine()) != null) {
                responseBuilder.append(responseLine);
            }

            String responseJson = responseBuilder.toString();
            return gson.fromJson(responseJson, Response.class);
        }
    }

    // Store operations
    public static Response addStore(String name, String locationId) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("locationId", locationId);
        return sendRequest("store/add", body);
    }

    public static Response getStore(int id) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return sendRequest("store/get", body);
    }

    public static Response getAllStores() throws IOException {
        return sendRequest("store/getAll", new HashMap<>());
    }

    public static Response updateStore(int id, String name, String locationId) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("name", name);
        body.put("locationId", locationId);
        return sendRequest("store/update", body);
    }

    public static Response deleteStore(int id) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return sendRequest("store/delete", body);
    }

    // Product operations
    public static Response addProductToStore(int storeId, String productName, double price) throws IOException {
        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("price", price);

        Map<String, Object> body = new HashMap<>();
        body.put("storeId", storeId);
        body.put("product", product);
        return sendRequest("store/addProduct", body);
    }

    public static Response removeProductFromStore(int storeId, String productName) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("storeId", storeId);
        body.put("productName", productName);
        return sendRequest("store/removeProduct", body);
    }

    public static Response getStoreProducts(int storeId) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("storeId", storeId);
        return sendRequest("store/getProducts", body);
    }

    public static Response updateProductInStore(int storeId, int productId, String productName, double price) throws IOException {
        Map<String, Object> product = new HashMap<>();
        product.put("id", productId);
        product.put("name", productName);
        product.put("price", price);

        Map<String, Object> body = new HashMap<>();
        body.put("storeId", storeId);
        body.put("product", product);
        return sendRequest("store/updateProduct", body);
    }

    // Search operations
    public static Response findNearestStoreWithProduct(String location, String productName) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("location", location);
        body.put("productName", productName);
        return sendRequest("store/findNearest", body);
    }

    public static Response findCheapestStoreWithProduct(String productName) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("productName", productName);
        return sendRequest("store/findCheapest", body);
    }

    // Graph operations
    public static Response addNode(String nodeName, double x, double y) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("nodeName", nodeName);
        body.put("x", x);
        body.put("y", y);
        return sendRequest("graph/addNode", body);
    }

    public static Response addEdge(String from, String to, double weight) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("weight", weight);
        return sendRequest("graph/addEdge", body);
    }

    public static Response removeNode(String nodeName) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("nodeName", nodeName);
        return sendRequest("graph/removeNode", body);
    }

    public static Response getAllNodes() throws IOException {
        return sendRequest("graph/getNodes", new HashMap<>());
    }

    public static Response getAllNodesWithCoordinates() throws IOException {
        return sendRequest("graph/getNodesWithCoordinates", new HashMap<>());
    }

    public static Response removeEdge(String from, String to) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        return sendRequest("graph/removeEdge", body);
    }

    public static Response getAllEdges() throws IOException {
        return sendRequest("graph/getEdges", new HashMap<>());
    }

    public static Response clearAllData() throws IOException {
        return sendRequest("graph/clearAllData", new HashMap<>());
    }
} 