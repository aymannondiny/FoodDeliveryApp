package com.fooddelivery.api;

import com.fooddelivery.api.coupon.CouponHandler;
import com.fooddelivery.api.coupon.CouponResponseMapper;
import com.fooddelivery.api.coupon.CouponValidator;
import com.fooddelivery.api.coupon.DefaultCouponValidator;
import com.fooddelivery.api.menu.DefaultMenuReader;
import com.fooddelivery.api.menu.MenuHandler;
import com.fooddelivery.api.menu.MenuReader;
import com.fooddelivery.api.menu.MenuResponseMapper;
import com.fooddelivery.api.order.DefaultOrderReader;
import com.fooddelivery.api.order.OrderHandler;
import com.fooddelivery.api.order.OrderReader;
import com.fooddelivery.api.restaurant.CuisinesHandler;
import com.fooddelivery.api.restaurant.DefaultRestaurantReader;
import com.fooddelivery.api.restaurant.RestaurantReader;
import com.fooddelivery.api.restaurant.RestaurantResponseMapper;
import com.fooddelivery.api.restaurant.RestaurantsHandler;
import com.fooddelivery.api.tracking.DefaultTrackingReader;
import com.fooddelivery.api.tracking.TrackHandler;
import com.fooddelivery.api.tracking.TrackingReader;
import com.fooddelivery.api.tracking.TrackingResponseMapper;
import com.fooddelivery.infrastructure.bootstrap.AppContext;
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

        AppContext context = AppContext.create();

        RestaurantReader restaurantReader =
                new DefaultRestaurantReader(context.restaurantQueryService());

        MenuReader menuReader =
                new DefaultMenuReader(context.menuQueryService());

        OrderReader orderReader =
                new DefaultOrderReader(context.getOrderByIdUseCase());

        TrackingReader trackingReader =
                new DefaultTrackingReader(
                        context.getOrderByIdUseCase(),
                        context.findRiderByIdUseCase()
                );

        CouponValidator couponValidator =
                new DefaultCouponValidator(context.couponValidationUseCase());

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
                new OrderHandler(orderReader, new com.fooddelivery.api.order.OrderResponseMapper())
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

        // SOAP test UI
        server.createContext("/soap-test", new com.fooddelivery.soap.client.SoapTestPageHandler());
        server.createContext("/soap-proxy", new com.fooddelivery.soap.client.SoapProxyHandler());

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