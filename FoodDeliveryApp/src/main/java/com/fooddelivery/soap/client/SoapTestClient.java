package com.fooddelivery.soap.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Manual SOAP test client.
 * Sends raw XML SOAP requests and prints responses.
 * Run this after starting the app to test SOAP endpoints.
 */
public class SoapTestClient {

    private static final String SOAP_BASE = "http://localhost:9090/soap";

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║         SOAP Test Client                         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        testGetAllRestaurants();
        testSearchRestaurants("Burger");
        testFilterByCuisine("Chinese");
        testGetAllCuisineTypes();
        testGetMenu("RST-"); // will need real ID
        testValidateCoupon("WELCOME20", 500.0);
        // testGetOrder and testTrackOrder need real order IDs
    }

    // ── Restaurant Tests ─────────────────────────────────────────────────

    private static void testGetAllRestaurants() {
        printHeader("GET ALL RESTAURANTS");

        String xml = envelope(
                "http://fooddelivery.com/soap",
                "getAllRestaurants",
                ""
        );

        sendRequest(SOAP_BASE + "/restaurants", xml);
    }

    private static void testSearchRestaurants(String query) {
        printHeader("SEARCH RESTAURANTS: " + query);

        String xml = envelope(
                "http://fooddelivery.com/soap",
                "searchRestaurants",
                "<arg0>" + query + "</arg0>"
        );

        sendRequest(SOAP_BASE + "/restaurants", xml);
    }

    private static void testFilterByCuisine(String cuisine) {
        printHeader("FILTER BY CUISINE: " + cuisine);

        String xml = envelope(
                "http://fooddelivery.com/soap",
                "filterByCuisine",
                "<arg0>" + cuisine + "</arg0>"
        );

        sendRequest(SOAP_BASE + "/restaurants", xml);
    }

    private static void testGetAllCuisineTypes() {
        printHeader("GET ALL CUISINE TYPES");

        String xml = envelope(
                "http://fooddelivery.com/soap",
                "getAllCuisineTypes",
                ""
        );

        sendRequest(SOAP_BASE + "/restaurants", xml);
    }

    // ── Menu Tests ───────────────────────────────────────────────────────

    private static void testGetMenu(String restaurantId) {
        printHeader("GET MENU FOR: " + restaurantId);

        String xml = envelope(
                "http://fooddelivery.com/soap",
                "getMenu",
                "<arg0>" + restaurantId + "</arg0>"
        );

        sendRequest(SOAP_BASE + "/menu", xml);
    }

    // ── Order Tests ──────────────────────────────────────────────────────

    public static void testGetOrder(String orderId) {
        printHeader("GET ORDER: " + orderId);

        String xml = envelope(
                "http://fooddelivery.com/soap",
                "getOrder",
                "<arg0>" + orderId + "</arg0>"
        );

        sendRequest(SOAP_BASE + "/orders", xml);
    }

    public static void testTrackOrder(String orderId) {
        printHeader("TRACK ORDER: " + orderId);

        String xml = envelope(
                "http://fooddelivery.com/soap",
                "trackOrder",
                "<arg0>" + orderId + "</arg0>"
        );

        sendRequest(SOAP_BASE + "/orders", xml);
    }

    // ── Coupon Tests ─────────────────────────────────────────────────────

    private static void testValidateCoupon(String code, double subtotal) {
        printHeader("VALIDATE COUPON: " + code + " (subtotal: " + subtotal + ")");

        String xml = envelope(
                "http://fooddelivery.com/soap",
                "validateCoupon",
                "<arg0>" + code + "</arg0>" +
                        "<arg1>" + subtotal + "</arg1>"
        );

        sendRequest(SOAP_BASE + "/coupons", xml);
    }

    // ── Helper Methods ───────────────────────────────────────────────────

    private static String envelope(String namespace, String method, String body) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soapenv:Envelope\n" +
                "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:ns=\"" + namespace + "\">\n" +
                "  <soapenv:Header/>\n" +
                "  <soapenv:Body>\n" +
                "    <ns:" + method + ">\n" +
                "      " + body + "\n" +
                "    </ns:" + method + ">\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    private static void sendRequest(String urlStr, String soapXml) {
        System.out.println("── Request ──────────────────────────────────────");
        System.out.println("URL: " + urlStr);
        System.out.println();
        System.out.println(formatXml(soapXml));
        System.out.println();

        try {
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
            System.out.println("── Response (HTTP " + responseCode + ") ──────────────────────");

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

            System.out.println(formatXml(response.toString()));
            System.out.println();

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.err.println("Make sure the app is running first!");
            System.out.println();
        }
    }

    private static void printHeader(String title) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.printf("║  TEST: %-42s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static String formatXml(String xml) {
        try {
            StringBuilder formatted = new StringBuilder();
            int indent = 0;
            boolean inTag = false;

            for (int i = 0; i < xml.length(); i++) {
                char c = xml.charAt(i);

                if (c == '<') {
                    if (i + 1 < xml.length() && xml.charAt(i + 1) == '/') {
                        indent--;
                    }

                    if (!inTag && formatted.length() > 0) {
                        char last = formatted.charAt(formatted.length() - 1);
                        if (last == '>') {
                            formatted.append("\n");
                            formatted.append("  ".repeat(Math.max(0, indent)));
                        }
                    }

                    inTag = true;
                }

                formatted.append(c);

                if (c == '>') {
                    inTag = false;

                    if (i >= 1 && xml.charAt(i - 1) != '/') {
                        if (formatted.length() >= 2) {
                            int openIdx = formatted.lastIndexOf("<");
                            if (openIdx >= 0 && openIdx < formatted.length() - 1
                                    && formatted.charAt(openIdx + 1) != '/') {
                                indent++;
                            }
                        }
                    }
                }
            }

            return formatted.toString().trim();

        } catch (Exception e) {
            return xml;
        }
    }
}