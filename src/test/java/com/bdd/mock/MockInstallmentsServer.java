package com.bdd.mock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Standalone mock server simulating 'installments.jar' for local interview practice.
 * Serves both Web UI (Login, Dashboard with Installments Table) and REST API (/api/plans).
 * Uses pure Java standard library (com.sun.net.httpserver) with zero extra dependencies.
 */
public class MockInstallmentsServer {

    private static final int PORT = 8080;
    private static HttpServer server;
    private static final List<String> plans = new ArrayList<>();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    static {
        // Seeded plan IDs as mentioned in the interview kit
        plans.add("{\"id\":\"PLAN-101\",\"customer\":\"Alice Smith\",\"totalAmount\":1200.00,\"installments\":12,\"monthlyDue\":100.00,\"status\":\"ACTIVE\"}");
        plans.add("{\"id\":\"PLAN-102\",\"customer\":\"Bob Johnson\",\"totalAmount\":600.00,\"installments\":6,\"monthlyDue\":100.00,\"status\":\"PAID\"}");
        plans.add("{\"id\":\"PLAN-103\",\"customer\":\"Charlie Brown\",\"totalAmount\":2400.00,\"installments\":24,\"monthlyDue\":100.00,\"status\":\"PENDING\"}");
    }

    public static void main(String[] args) throws IOException {
        startServer();
        System.out.println("======================================================");
        System.out.println(" Mock installments.jar Server started on port " + PORT);
        System.out.println(" Web UI:  http://localhost:" + PORT + "/login");
        System.out.println(" REST API: http://localhost:" + PORT + "/api/plans");
        System.out.println(" Credentials: testuser@example.com / testpassword");
        System.out.println("======================================================");
    }

    public static synchronized void startServer() throws IOException {
        if (server != null) return;

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        // Web UI Handlers
        server.createContext("/", new LoginHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/dashboard", new DashboardHandler());

        // API Handlers
        server.createContext("/api/plans", new ApiPlansHandler());

        server.start();
    }

    public static synchronized void stopServer() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    // ==========================================
    // Handlers
    // ==========================================

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String errorMessage = null;

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseFormData(body);
                String user = params.getOrDefault("username", "").trim();
                String pass = params.getOrDefault("password", "").trim();

                if (user.isEmpty()) {
                    errorMessage = "Username is required!";
                } else if (!EMAIL_PATTERN.matcher(user).matches()) {
                    errorMessage = "Username must be a valid email address!";
                } else if (pass.isEmpty()) {
                    errorMessage = "Password is required!";
                } else if ("testuser@example.com".equalsIgnoreCase(user) && "testpassword".equals(pass)) {
                    exchange.getResponseHeaders().set("Location", "/dashboard");
                    exchange.sendResponseHeaders(302, -1);
                    return;
                } else {
                    errorMessage = "Invalid username or password!";
                }
            }

            String errorHtml = (errorMessage != null)
                ? "<div id=\"errorMessage\" class=\"alert\" style=\"background: #ef4444; color: #fff; padding: 0.75rem; border-radius: 6px; margin-bottom: 1.25rem; font-size: 0.85rem; font-weight: 500; text-align: center;\">" 
                  + errorMessage + "</div>"
                : "";

            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Installments App - Login</title>
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0f172a; color: #f8fafc; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; }
                        .card { background: #1e293b; padding: 2.5rem; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.5); width: 340px; border: 1px solid #334155; }
                        h2 { margin-top: 0; color: #38bdf8; text-align: center; }
                        label { display: block; margin-top: 1rem; font-size: 0.85rem; color: #94a3b8; }
                        input { width: 100%; padding: 0.65rem; margin-top: 0.25rem; border: 1px solid #475569; border-radius: 6px; background: #0f172a; color: #fff; box-sizing: border-box; }
                        button { width: 100%; padding: 0.75rem; margin-top: 1.5rem; border: none; border-radius: 6px; background: #0284c7; color: #fff; font-weight: 600; cursor: pointer; transition: background 0.2s; }
                        button:hover { background: #0369a1; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h2>Installments App</h2>
                        """ + errorHtml + """
                        <form method="POST" action="/login" novalidate>
                            <label for="username">Email Address</label>
                            <input type="email" id="username" name="username" placeholder="testuser@example.com">
                            <label for="password">Password</label>
                            <input type="password" id="password" name="password" placeholder="testpassword">
                            <button type="submit" id="loginButton">Sign In</button>
                        </form>
                    </div>
                </body>
                </html>
                """;
            sendResponse(exchange, 200, "text/html", html);
        }

        private Map<String, String> parseFormData(String formData) {
            Map<String, String> map = new HashMap<>();
            if (formData == null || formData.isEmpty()) return map;
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length > 0) {
                    String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                    String val = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
                    map.put(key, val);
                }
            }
            return map;
        }
    }

    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Installments Dashboard</title>
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0f172a; color: #f8fafc; padding: 2rem; margin: 0; }
                        .container { max-width: 900px; margin: 0 auto; background: #1e293b; padding: 2rem; border-radius: 12px; border: 1px solid #334155; }
                        h1 { color: #38bdf8; margin-top: 0; }
                        table { width: 100%; border-collapse: collapse; margin-top: 1.5rem; }
                        th, td { text-align: left; padding: 0.85rem; border-bottom: 1px solid #334155; }
                        th { background: #0f172a; color: #94a3b8; font-size: 0.85rem; text-transform: uppercase; }
                        .badge { padding: 0.25rem 0.6rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; }
                        .badge-active { background: #065f46; color: #34d399; }
                        .badge-paid { background: #1e3a8a; color: #60a5fa; }
                        .badge-pending { background: #78350f; color: #fbbf24; }
                        .btn-pay { background: #0284c7; color: #fff; border: none; padding: 0.35rem 0.75rem; border-radius: 4px; cursor: pointer; }
                        .btn-pay:hover { background: #0369a1; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Installment Plans Overview</h1>
                        <table id="installmentsTable">
                            <thead>
                                <tr>
                                    <th>Plan ID</th>
                                    <th>Customer</th>
                                    <th>Total Amount</th>
                                    <th>Installments</th>
                                    <th>Monthly Due</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td class="plan-id">PLAN-101</td>
                                    <td>Alice Smith</td>
                                    <td>$1,200.00</td>
                                    <td>12</td>
                                    <td>$100.00</td>
                                    <td><span class="badge badge-active">ACTIVE</span></td>
                                    <td><button class="btn-pay" id="pay-PLAN-101">Pay Now</button></td>
                                </tr>
                                <tr>
                                    <td class="plan-id">PLAN-102</td>
                                    <td>Bob Johnson</td>
                                    <td>$600.00</td>
                                    <td>6</td>
                                    <td>$100.00</td>
                                    <td><span class="badge badge-paid">PAID</span></td>
                                    <td><span>Completed</span></td>
                                </tr>
                                <tr>
                                    <td class="plan-id">PLAN-103</td>
                                    <td>Charlie Brown</td>
                                    <td>$2,400.00</td>
                                    <td>24</td>
                                    <td>$100.00</td>
                                    <td><span class="badge badge-pending">PENDING</span></td>
                                    <td><button class="btn-pay" id="pay-PLAN-103">Pay Now</button></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </body>
                </html>
                """;
            sendResponse(exchange, 200, "text/html", html);
        }
    }

    static class ApiPlansHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                if (path.endsWith("/PLAN-101")) {
                    sendResponse(exchange, 200, "application/json", plans.get(0));
                    return;
                } else if (path.endsWith("/PLAN-102")) {
                    sendResponse(exchange, 200, "application/json", plans.get(1));
                    return;
                } else if (path.endsWith("/PLAN-103")) {
                    sendResponse(exchange, 200, "application/json", plans.get(2));
                    return;
                } else {
                    // All plans
                    String json = "[" + String.join(",", plans) + "]";
                    sendResponse(exchange, 200, "application/json", json);
                    return;
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String newPlan = "{\"id\":\"PLAN-104\",\"status\":\"CREATED\",\"data\":" + body + "}";
                plans.add(newPlan);
                sendResponse(exchange, 201, "application/json", newPlan);
                return;
            }

            sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}");
        }
    }

    private static void sendResponse(HttpExchange exchange, int status, String contentType, String content) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
