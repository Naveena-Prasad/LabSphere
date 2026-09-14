package backend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class LabServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/checkout", new CheckoutHandler());
        server.createContext("/api/items", new GetItemsHandler());
        server.createContext("/api/delete", new DeleteHandler());
        server.setExecutor(null);

        System.out.println("==========================================");
        System.out.println("LabSphere Java Backend Server started successfully");
        System.out.println("Access URL: http://localhost:8080/api/items");
        System.out.println("==========================================");
        server.start();
    }

    static class CheckoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    InputStream requestBody = exchange.getRequestBody();
                    String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

                    String eqId = extractJsonValue(body, "eqId");
                    if (eqId.isEmpty()) {
                        eqId = extractJsonValue(body, "id");
                    }
                    String remainingQtyStr = extractJsonValue(body, "remainingQty");
                    if (remainingQtyStr.isEmpty()) {
                        remainingQtyStr = extractJsonValue(body, "qty");
                    }
                    String lab = extractJsonValue(body, "lab");

                    int remainingQty = 0;
                    try {
                        remainingQty = Integer.parseInt(remainingQtyStr);
                    } catch (NumberFormatException ignored) {
                    }

                    String url = "jdbc:mysql://localhost:3306/labsphere_db";
                    String dbuser = "root";
                    String dbPassword = "";

                    Class.forName("com.mysql.cj.jdbc.Driver");

                    try (Connection conn = DriverManager.getConnection(url, dbuser, dbPassword)) {
                        String sql = "UPDATE inventory_items SET quantity = ? WHERE id = ? AND lab_name = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setInt(1, remainingQty);
                            pstmt.setString(2, eqId);
                            pstmt.setString(3, lab);
                            pstmt.executeUpdate();
                        }
                    }

                    sendJsonResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Checkout processed successfully\"}");

                } catch (ClassNotFoundException | SQLException e) {
                    sendJsonResponse(exchange, 500, "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    static class GetItemsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                StringBuilder jsonBuilder = new StringBuilder("[");
                String url = "jdbc:mysql://localhost:3306/labsphere_db";
                String dbuser = "root";
                String dbPassword = "";

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    try (Connection conn = DriverManager.getConnection(url, dbuser, dbPassword);
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT id, name, quantity, lab_name FROM inventory_items")) {
                        
                        boolean first = true;
                        while (rs.next()) {
                            if (!first) {
                                jsonBuilder.append(",");
                            }
                            jsonBuilder.append("{")
                                       .append("\"id\":\"").append(rs.getString("id")).append("\",")
                                       .append("\"equipment\":\"").append(rs.getString("name")).append("\",")
                                       .append("\"qty\":\"").append(rs.getInt("quantity")).append("\",")
                                       .append("\"lab\":\"").append(rs.getString("lab_name")).append("\"")
                                       .append("}");
                            first = false;
                        }
                    }
                } catch (ClassNotFoundException | SQLException ignored) {
                }

                jsonBuilder.append("]");
                sendJsonResponse(exchange, 200, jsonBuilder.toString());
            } 
            else if ("POST".equalsIgnoreCase(method)) {
                try {
                    InputStream requestBody = exchange.getRequestBody();
                    String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

                    String id = extractJsonValue(body, "id");
                    String equipment = extractJsonValue(body, "equipment");
                    String qtystr = extractJsonValue(body, "qty");
                    String lab = extractJsonValue(body, "lab");

                    int qty = 0;
                    try {
                        qty = Integer.parseInt(qtystr);
                    } catch (NumberFormatException ignored) {
                    }

                    String url = "jdbc:mysql://localhost:3306/labsphere_db";
                    String dbuser = "root";
                    String dbPassword = "";

                    Class.forName("com.mysql.cj.jdbc.Driver");

                    try (Connection conn = DriverManager.getConnection(url, dbuser, dbPassword)) {
                        String sql = "INSERT INTO inventory_items (id, name, quantity, lab_name) VALUES (?, ?, ?, ?) " +
                                     "ON DUPLICATE KEY UPDATE name = VALUES(name), quantity = VALUES(quantity), lab_name = VALUES(lab_name)";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, id);
                            pstmt.setString(2, equipment);
                            pstmt.setInt(3, qty);
                            pstmt.setString(4, lab);
                            pstmt.executeUpdate();
                        }
                    }

                    sendJsonResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Item successfully processed\"}");
                } catch (ClassNotFoundException | SQLException e) {
                    sendJsonResponse(exchange, 500, "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
                }
            } 
            else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    static class DeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod()) || "DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    InputStream requestBody = exchange.getRequestBody();
                    String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                    String id = extractJsonValue(body, "id");
                    String lab = extractJsonValue(body, "lab");

                    String url = "jdbc:mysql://localhost:3306/labsphere_db";
                    String dbuser = "root";
                    String dbPassword = "";

                    Class.forName("com.mysql.cj.jdbc.Driver");

                    try (Connection conn = DriverManager.getConnection(url, dbuser, dbPassword)) {
                        String sql;
                        if (lab != null && !lab.isEmpty()) {
                            sql = "DELETE FROM inventory_items WHERE id = ? AND lab_name = ?";
                        } else {
                            sql = "DELETE FROM inventory_items WHERE id = ?";
                        }
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, id);
                            if (lab != null && !lab.isEmpty()) {
                                pstmt.setString(2, lab);
                            }
                            pstmt.executeUpdate();
                        }
                    }

                    sendJsonResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Item deleted successfully\"}");

                } catch (ClassNotFoundException | SQLException e) {
                    sendJsonResponse(exchange, 500, "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private static void addCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private static String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return "";
            startIndex += searchKey.length();

            char firstChar = json.charAt(startIndex);
            if (firstChar == '"') {
                startIndex++;
                int endIndex = json.indexOf("\"", startIndex);
                return json.substring(startIndex, endIndex);
            } else {
                int endIndex = startIndex;
                while (endIndex < json.length() && json.charAt(endIndex) != ',' && json.charAt(endIndex) != '}') {
                    endIndex++;
                }
                return json.substring(startIndex, endIndex).trim();
            }
        } catch (Exception e) {
            return "";
        }
    }
}