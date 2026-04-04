package com.fooddelivery.api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Lightweight embedded HTTP server exposing the food delivery REST API.
 *
 * Endpoints:
 *   GET /api/restaurants?area=&cuisine=&search=   – Restaurant discovery
 *   GET /api/menu?restaurantId=                   – Menu for a restaurant
 *   GET /api/order?orderId=                        – Full order details
 *   GET /api/track?orderId=                        – Live order tracking
 *   GET /api/cuisines                             – All cuisine types
 *   GET /api/coupon?code=&subtotal=               – Coupon validation
 */
public class ApiServer {

    private static final int PORT = 8080;
    private HttpServer server;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/restaurants", new RestaurantsHandler());
        server.createContext("/api/menu",        new MenuHandler());
        server.createContext("/api/order",       new OrderHandler());
        server.createContext("/api/track",       new TrackHandler());
        server.createContext("/api/cuisines",    new CuisinesHandler());
        server.createContext("/api/coupon",      new CouponHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  API Server running on http://localhost:" + PORT + "     ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  GET /api/restaurants?area=Dhanmondi             ║");
        System.out.println("║  GET /api/menu?restaurantId=<id>                 ║");
        System.out.println("║  GET /api/order?orderId=<id>                     ║");
        System.out.println("║  GET /api/track?orderId=<id>                     ║");
        System.out.println("║  GET /api/cuisines                               ║");
        System.out.println("║  GET /api/coupon?code=WELCOME20&subtotal=500     ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    public void stop() {
        if (server != null) server.stop(0);
    }
}
