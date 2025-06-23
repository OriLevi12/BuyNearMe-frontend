# BuyNearMe UI

This is the user interface for the BuyNearMe application. It is a JavaFX desktop client that communicates with the [BuyNearMe backend](https://github.com/OriLevi12/BuyNearMe) to provide users with functionality to find stores and products.

## Screenshots

### 🏠 Home Screen
![Home Screen](docs/screenshots/home-screen.png)
*Main menu with options to access Client or Admin functionality*

### 👤 Client View
![Client View](docs/screenshots/client-view.png)
*Store listing interface with background image and navigation options*

### 💰 Find Cheapest Store
![Find Cheapest Store](docs/screenshots/find-cheapest-store.png)
*Search interface for finding the most affordable store for specific products*

### 🔍 Find Nearest Store
![Find Nearest Store](docs/screenshots/find-nearest-store.png)
*Location-based search to find the closest store*

### 🛠 Admin Dashboard

#### 🏬 Manage Stores & Products
![Admin Stores Tab](docs/screenshots/admin-stores-tab.png)
*Store and product management interface with tables and forms*

#### 🗺 Manage Graph (Nodes)
![Admin Nodes Tab](docs/screenshots/admin-nodes-tab.png)
*Node management for the graph structure with coordinate inputs*

#### 🛤 Manage Graph (Edges)
![Admin Edges Tab](docs/screenshots/admin-edges-tab.png)
*Edge management for connecting nodes in the graph structure*

## Prerequisites

- Java Development Kit (JDK) 11 or higher.
- Maven 3.6 or higher (or you can use the included Maven Wrapper).

## How to Run

1. **Start the Backend Server**
   Before running the UI, you need to start the backend server.

   a. Clone the backend repository (if you haven't already):
   ```sh
   git clone https://github.com/OriLevi12/BuyNearMe.git
   ```
   b. Navigate into the backend project directory:
   ```sh
   cd BuyNearMe
   ```
   c. The backend project has manual dependencies. Make sure you have followed the setup instructions in its `README.md` to place the required `.jar` files in the `lib/` directory.

   d. Run the server using the following command from the `BuyNearMe` directory:
   ```sh
   java -cp "lib/*;src/main/java" com.om.Main
   ```
   Keep this terminal open. The backend server is now running and ready to accept connections from the UI.

2. **Clone and Run the UI**
   In a **new terminal window**, clone this repository and run the UI application.
   ```sh
   git clone https://github.com/OriLevi12/BuyNearMe-frontend.git
   cd BuyNearMe-frontend
   ```

3. **Run the application using the Maven Wrapper:**
   
   On macOS/Linux:
   ```sh
   ./mvnw javafx:run
   ```

   On Windows:
   ```sh
   ./mvnw.cmd javafx:run
   ```

The application will now start and connect to the backend.

## 📁 Project Structure

```
BuyNearMe-frontend/
├── .mvn/                      # Maven Wrapper configuration
├── docs/                      # Documentation and assets
│   └── screenshots/           # UI screenshots for README
├── src/
│   ├── main/
│   │   ├── java/com/buynearme/client/ # Java source code for the application
│   │   │   ├── network/               # Handles network communication with the backend API
│   │   │   │   └── NetworkClient.java
│   │   │   ├── AdminController.java
│   │   │   ├── ClientController.java
│   │   │   ├── FindCheapestStoreController.java
│   │   │   ├── FindNearestStoreController.java
│   │   │   ├── HomeController.java
│   │   │   └── BuyNearMeApplication.java # Main JavaFX application entry point
│   │   │
│   │   └── resources/
│   │       ├── com/buynearme/client/  # FXML views and CSS stylesheets
│   │       │   ├── AdminView.fxml
│   │       │   ├── ClientView.fxml
│   │       │   ├── FindCheapestStoreView.fxml
│   │       │   ├── FindNearestStoreView.fxml
│   │       │   ├── HomeView.fxml
│   │       │   └── style.css          # Global stylesheet
│   │       │
│   │       └── images/                # Background images and assets for the UI
│
├── .gitignore                 # Specifies intentionally untracked files to ignore
├── mvnw & mvnw.cmd            # Maven Wrapper scripts for building and running the project
├── pom.xml                    # Maven project configuration (dependencies, build plugins)
└── README.md                  # This file
```

## 📬 Contact Info
**Ori Levi**  
📧 Email: Leviori1218@gmail.com  
🐙 GitHub: [OriLevi12](https://github.com/OriLevi12) 