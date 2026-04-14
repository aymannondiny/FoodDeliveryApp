package com.fooddelivery.api;

import com.fooddelivery.api.coupon.*;
import com.fooddelivery.api.menu.*;
import com.fooddelivery.api.order.*;
import com.fooddelivery.api.restaurant.*;
import com.fooddelivery.api.tracking.*;
import com.fooddelivery.service.CouponService;
import com.fooddelivery.service.MenuService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.service.RiderService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Lightweight embedded HTTP server exposing the food delivery REST API.
 */
public class ApiServer {

    private static final int PORT = 8080;

    private HttpServer server;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        RestaurantReader restaurantReader =
                new DefaultRestaurantReader(RestaurantService.getInstance());

        MenuReader menuReader =
                new DefaultMenuReader(MenuService.getInstance());

        OrderReader orderReader =
                new DefaultOrderReader(OrderService.getInstance());

        TrackingReader trackingReader =
                new DefaultTrackingReader(OrderService.getInstance(), RiderService.getInstance());

        CouponValidator couponValidator =
                new DefaultCouponValidator(CouponService.getInstance());

        server.createContext(
                "/api/restaurants",
                new RestaurantsHandler(restaurantReader, new RestaurantResponseMapper())
        );

        server.createContext(
                "/api/menu",
                new MenuHandler(menuReader, new MenuResponseMapper())
        );

        server.createContext(
                "/api/order",
                new OrderHandler(orderReader, new OrderResponseMapper())
        );

        server.createContext(
                "/api/track",
                new TrackHandler(trackingReader, new TrackingResponseMapper())
        );

        server.createContext(
                "/api/cuisines",
                new CuisinesHandler(restaurantReader)
        );

        server.createContext(
                "/api/coupon",
                new CouponHandler(couponValidator, new CouponResponseMapper())
        );

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
        if (server != null) {
            server.stop(0);
        }
    }
}