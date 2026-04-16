package com.fooddelivery.soap;

import com.fooddelivery.infrastructure.bootstrap.AppContext;
import com.fooddelivery.soap.service.CouponWebService;
import com.fooddelivery.soap.service.MenuWebService;
import com.fooddelivery.soap.service.OrderWebService;
import com.fooddelivery.soap.service.RestaurantWebService;
import jakarta.xml.ws.Endpoint;

/**
 * Embedded SOAP server exposing food delivery web services.
 * Runs alongside the REST API server.
 */
public class SoapServer {

    private static final String BASE_URL = "http://localhost:9090/soap";

    private Endpoint restaurantEndpoint;
    private Endpoint menuEndpoint;
    private Endpoint orderEndpoint;
    private Endpoint couponEndpoint;

    public void start(AppContext context) {
        RestaurantWebService restaurantWs = new RestaurantWebService(
                context.restaurantQueryService()
        );

        MenuWebService menuWs = new MenuWebService(
                context.menuQueryService()
        );

        OrderWebService orderWs = new OrderWebService(
                context.getOrderByIdUseCase(),
                context.findRiderByIdUseCase()
        );

        CouponWebService couponWs = new CouponWebService(
                context.couponValidationUseCase()
        );

        restaurantEndpoint = Endpoint.publish(BASE_URL + "/restaurants", restaurantWs);
        menuEndpoint = Endpoint.publish(BASE_URL + "/menu", menuWs);
        orderEndpoint = Endpoint.publish(BASE_URL + "/orders", orderWs);
        couponEndpoint = Endpoint.publish(BASE_URL + "/coupons", couponWs);

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  SOAP Server running on " + BASE_URL + "       ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  WSDL endpoints:                                 ║");
        System.out.println("║  " + BASE_URL + "/restaurants?wsdl               ║");
        System.out.println("║  " + BASE_URL + "/menu?wsdl                      ║");
        System.out.println("║  " + BASE_URL + "/orders?wsdl                    ║");
        System.out.println("║  " + BASE_URL + "/coupons?wsdl                   ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    public void stop() {
        if (restaurantEndpoint != null) restaurantEndpoint.stop();
        if (menuEndpoint != null) menuEndpoint.stop();
        if (orderEndpoint != null) orderEndpoint.stop();
        if (couponEndpoint != null) couponEndpoint.stop();
    }
}