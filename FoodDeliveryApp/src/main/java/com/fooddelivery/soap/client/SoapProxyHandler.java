package com.fooddelivery.soap.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Proxies SOAP requests from the browser to the SOAP server.
 * This avoids CORS issues when calling SOAP from JavaScript.
 */
public class SoapProxyHandler implements HttpHandler {

    private static final String SOAP_BASE = "http://localhost:9090/soap";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());

        String service = params.get("service");
        String body = params.get("body");

        if (service == null || body == null) {
            sendResponse(exchange, 400, "Missing 'service' or 'body' parameter");
            return;
        }

        String soapUrl = SOAP_BASE + "/" + service;

        try {
            String soapResponse = forwardToSoap(soapUrl, URLDecoder.decode(body, StandardCharsets.UTF_8));

            exchange.getResponseHeaders().set("Content-Type", "application/xml; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            byte[] bytes = soapResponse.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }

        } catch (Exception e) {
            String errorXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<error>\n" +
                    "  <message>" + escapeXml(e.getMessage()) + "</message>\n" +
                    "  <service>" + escapeXml(service) + "</service>\n" +
                    "</error>";

            sendResponse(exchange, 500, errorXml);
        }
    }

    private String forwardToSoap(String urlStr, String soapXml) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        conn.setRequestProperty("SOAPAction", "");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapXml.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int responseCode = conn.getResponseCode();

        BufferedReader reader;
        if (responseCode >= 200 && responseCode < 300) {
            reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );
        } else {
            reader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8)
            );
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
        }
        reader.close();

        return response.toString();
    }

    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/xml; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new LinkedHashMap<>();
        if (query == null || query.isBlank()) {
            return params;
        }

        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(
                        URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                );
            }
        }

        return params;
    }

    private String escapeXml(String str) {
        if (str == null) return "unknown error";
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}