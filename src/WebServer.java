import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import database.DatabaseConnection;
import model.ClaimRequest;
import model.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * WebServer.java
 * Pure Java HTTP Server listening on http://localhost:3000
 * Serves Neo-Brutalist Dashboard at / and OOP & DSA Architecture Showcase at /oop.
 * Connects directly to CampusDrop, Java Collections, and Oracle SQL Database.
 * Non-blocking, fast O(1) in-memory reads synchronized with Oracle SQL DB writes.
 */
public class WebServer {

    private static final int PORT = 3000;
    private static final CampusDrop system = new CampusDrop();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Static Web UI Handlers
        server.createContext("/", new StaticFileHandler("index.html"));
        server.createContext("/oop", new StaticFileHandler("oop.html"));
        server.createContext("/oop.html", new StaticFileHandler("oop.html"));

        // REST API Handlers
        server.createContext("/api/stats", new StatsHandler());
        server.createContext("/api/items", new ItemsHandler());
        server.createContext("/api/matches", new MatchesHandler());
        server.createContext("/api/claims", new ClaimsQueueHandler());
        server.createContext("/api/history", new HistoryHandler());
        server.createContext("/api/report-lost", new ReportLostHandler());
        server.createContext("/api/report-found", new ReportFoundHandler());
        server.createContext("/api/claim", new SubmitClaimHandler());
        server.createContext("/api/process-claim", new ProcessClaimHandler());
        server.createContext("/api/return-item", new ReturnItemHandler());
        server.createContext("/api/delete-item", new DeleteItemHandler());
        server.createContext("/api/undo", new UndoHandler());
        server.createContext("/api/demo-seed", new DemoSeedHandler());

        server.setExecutor(null);
        System.out.println("==================================================");
        System.out.println("   CAMPUS DROP WEB SERVER STARTED LOCALLY");
        System.out.println("==================================================");
        System.out.println("   Dashboard UI at : http://localhost:" + PORT);
        System.out.println("   OOP Guide UI at : http://localhost:" + PORT + "/oop");
        System.out.println("   Powered by Java Collections + Oracle SQL DB");
        System.out.println("==================================================");
        server.start();
    }

    // =========================================================================
    // HANDLERS
    // =========================================================================

    static class StaticFileHandler implements HttpHandler {
        private final String defaultFileName;

        public StaticFileHandler(String defaultFileName) {
            this.defaultFileName = defaultFileName;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String fileName = defaultFileName;

            if (path.endsWith(".html")) {
                fileName = path.substring(1);
            }

            File file = new File(fileName);
            if (!file.exists()) {
                file = new File("C:\\Users\\shaki\\OneDrive\\Desktop\\CAMPUS DROP\\" + fileName);
            }

            if (file.exists()) {
                byte[] bytes = new byte[(int) file.length()];
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(bytes);
                }
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                String error = "<html><body><h1>File " + fileName + " not found</h1></body></html>";
                sendJsonResponse(exchange, 404, error);
            }
        }
    }

    static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Item> all = system.getAllItemsList();
            int total = all.size();
            int lost = 0, found = 0, active = 0, claimed = 0, returned = 0;
            for (Item item : all) {
                if ("LOST".equals(item.getType())) lost++;
                if ("FOUND".equals(item.getType())) found++;
                if ("ACTIVE".equals(item.getStatus())) active++;
                else if ("CLAIMED".equals(item.getStatus())) claimed++;
                else if ("RETURNED".equals(item.getStatus())) returned++;
            }
            int pendingClaims = system.getQueueCount();
            int historyCount = system.getHistoryCount();
            boolean oracleOnline = system.isOracleConnected();

            String json = String.format(
                "{\"totalItems\":%d,\"lostItems\":%d,\"foundItems\":%d,\"activeItems\":%d,\"claimedItems\":%d,\"returnedItems\":%d,\"pendingClaims\":%d,\"historyCount\":%d,\"oracleOnline\":%b}",
                total, lost, found, active, claimed, returned, pendingClaims, historyCount, oracleOnline
            );
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class ItemsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Item> all = system.getAllItemsList();
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Item item : all) {
                if (!first) sb.append(",");
                sb.append(itemToJson(item));
                first = false;
            }
            sb.append("]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class MatchesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Item> all = system.getAllItemsList();
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;

            for (Item lost : all) {
                if ("LOST".equals(lost.getType()) && "ACTIVE".equals(lost.getStatus())) {
                    for (Item found : all) {
                        if ("FOUND".equals(found.getType()) && "ACTIVE".equals(found.getStatus())) {
                            boolean nameMatch = lost.getName().equalsIgnoreCase(found.getName()) ||
                                                lost.getName().toLowerCase().contains(found.getName().toLowerCase()) ||
                                                found.getName().toLowerCase().contains(lost.getName().toLowerCase());
                            boolean catMatch = lost.getCategory().equalsIgnoreCase(found.getCategory());
                            boolean locMatch = lost.getLocation().equalsIgnoreCase(found.getLocation());

                            if (nameMatch && (catMatch || locMatch)) {
                                if (!first) sb.append(",");
                                sb.append(String.format("{\"lostId\":%d,\"lostName\":\"%s\",\"lostLocation\":\"%s\",\"lostBy\":\"%s\",\"foundId\":%d,\"foundName\":\"%s\",\"foundLocation\":\"%s\",\"foundBy\":\"%s\",\"category\":\"%s\"}",
                                        lost.getId(), escapeJson(lost.getName()), escapeJson(lost.getLocation()), escapeJson(lost.getReportedBy()),
                                        found.getId(), escapeJson(found.getName()), escapeJson(found.getLocation()), escapeJson(found.getReportedBy()),
                                        escapeJson(found.getCategory())));
                                first = false;
                            }
                        }
                    }
                }
            }
            sb.append("]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class ClaimsQueueHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<ClaimRequest> claims = system.getClaimQueueList();
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (ClaimRequest c : claims) {
                if (!first) sb.append(",");
                sb.append(claimToJson(c));
                first = false;
            }
            sb.append("]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Item> history = system.getRecoveryHistoryList();
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Item item : history) {
                if (!first) sb.append(",");
                sb.append(itemToJson(item));
                first = false;
            }
            sb.append("]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class ReportLostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                String name = extractJsonVal(body, "name");
                String category = extractJsonVal(body, "category");
                String location = extractJsonVal(body, "location");
                String date = extractJsonVal(body, "date");
                String studentName = extractJsonVal(body, "studentName");

                int id = system.reportLostItem(name, category, location, date, studentName);
                String json = String.format("{\"success\":true,\"id\":%d}", id);
                sendJsonResponse(exchange, 200, json);
            }
        }
    }

    static class ReportFoundHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                String name = extractJsonVal(body, "name");
                String category = extractJsonVal(body, "category");
                String location = extractJsonVal(body, "location");
                String date = extractJsonVal(body, "date");
                String finderName = extractJsonVal(body, "finderName");

                int id = system.reportFoundItem(name, category, location, date, finderName);
                String json = String.format("{\"success\":true,\"id\":%d}", id);
                sendJsonResponse(exchange, 200, json);
            }
        }
    }

    static class SubmitClaimHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                int itemId = Integer.parseInt(extractJsonVal(body, "itemId"));
                String studentName = extractJsonVal(body, "studentName");
                String studentId = extractJsonVal(body, "studentId");
                String claimReason = extractJsonVal(body, "claimReason");

                boolean ok = system.submitClaim(itemId, studentName, studentId, claimReason);
                String json = String.format("{\"success\":%b,\"message\":\"%s\"}", ok, ok ? "Submitted" : "Item not found or already claimed");
                sendJsonResponse(exchange, 200, json);
            }
        }
    }

    static class ProcessClaimHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                boolean approve = Boolean.parseBoolean(extractJsonVal(body, "approve"));
                system.processClaim(approve);
                sendJsonResponse(exchange, 200, "{\"success\":true}");
            }
        }
    }

    static class ReturnItemHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                int itemId = Integer.parseInt(extractJsonVal(body, "itemId"));
                boolean ok = system.returnItem(itemId);
                String json = String.format("{\"success\":%b,\"message\":\"%s\"}", ok, ok ? "Returned" : "Item must have status 'CLAIMED' to be returned");
                sendJsonResponse(exchange, 200, json);
            }
        }
    }

    static class DeleteItemHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                int itemId = Integer.parseInt(extractJsonVal(body, "itemId"));
                boolean ok = system.deleteItem(itemId);
                String json = String.format("{\"success\":%b,\"message\":\"%s\"}", ok, ok ? "Item deleted" : "Item not found");
                sendJsonResponse(exchange, 200, json);
            }
        }
    }

    static class UndoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String action = system.popLastAction();
                if (action != null) {
                    sendJsonResponse(exchange, 200, String.format("{\"success\":true,\"action\":\"%s\"}", escapeJson(action)));
                } else {
                    sendJsonResponse(exchange, 200, "{\"success\":false,\"message\":\"No actions in Stack to undo\"}");
                }
            }
        }
    }

    static class DemoSeedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Add a matching pair and claim for instant demonstration
                int lostId = system.reportLostItem("AirPods Pro", "Electronics", "Library 2nd Floor", "25/09/2026", "Aakash Patel");
                int foundId = system.reportFoundItem("AirPods Pro Case", "Electronics", "Library", "25/09/2026", "Security Guard");
                system.submitClaim(foundId, "Aakash Patel", "24CS102", "White case with serial engraving on inside");

                sendJsonResponse(exchange, 200, String.format("{\"success\":true,\"message\":\"Demo data populated: Lost #%d, Found #%d, Claim queued!\"}", lostId, foundId));
            }
        }
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        Scanner s = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static String extractJsonVal(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();

        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) {
            start++;
        }

        int end = start;
        while (end < json.length() && json.charAt(end) != '"' && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }
        return json.substring(start, end).trim();
    }

    private static String itemToJson(Item item) {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"category\":\"%s\",\"location\":\"%s\",\"date\":\"%s\",\"reportedBy\":\"%s\",\"type\":\"%s\",\"status\":\"%s\"}",
            item.getId(), escapeJson(item.getName()), escapeJson(item.getCategory()), escapeJson(item.getLocation()),
            escapeJson(item.getDate()), escapeJson(item.getReportedBy()), item.getType(), item.getStatus()
        );
    }

    private static String claimToJson(ClaimRequest c) {
        return String.format(
            "{\"itemId\":%d,\"studentName\":\"%s\",\"studentId\":\"%s\",\"claimReason\":\"%s\"}",
            c.getItemId(), escapeJson(c.getStudentName()), escapeJson(c.getStudentId()), escapeJson(c.getClaimReason())
        );
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", "");
    }
}
