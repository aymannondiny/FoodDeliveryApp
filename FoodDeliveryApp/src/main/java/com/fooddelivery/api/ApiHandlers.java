package com.fooddelivery.api;

import com.fooddelivery.model.*;
import com.fooddelivery.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// ──────────────────────────────────────────────────────────────────────────────
//  GET /api/restaurants?area=Dhanmondi
//  Returns all approved restaurants; optionally filtered by area or cuisine.
// ──────────────────────────────────────────────────────────────────────────────
class RestaurantsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            ApiResponse.sendError(ex, 405, "Method Not Allowed"); return;
        }
        Map<String, String> params = ApiResponse.parseQuery(ex.getRequestURI().getRawQuery());
        String area    = params.get("area");
        String cuisine = params.get("cuisine");
        String search  = params.get("search");

        List<Restaurant> results;
        RestaurantService svc = RestaurantService.getInstance();

        if (area != null && !area.isBlank()) {
            Address dummy = new Address("", area, "Dhaka", "");
            results = svc.findNearby(dummy, 50);
        } else if (cuisine != null && !cuisine.isBlank()) {
            results = svc.filterByCuisine(cuisine);
        } else if (search != null && !search.isBlank()) {
            results = svc.search(search);
        } else {
            results = svc.getAll();
        }

        List<Map<String, Object>> payload = results.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",                  r.getId());
            m.put("name",                r.getName());
            m.put("cuisineType",         r.getCuisineType());
            m.put("description",         r.getDescription());
            m.put("rating",              r.getRating());
            m.put("totalRatings",        r.getTotalRatings());
            m.put("isOpen",              r.isCurrentlyOpen());
            m.put("minOrderAmount",      r.getMinOrderAmount());
            m.put("deliveryFeePerKm",    r.getDeliveryFeePerKm());
            m.put("estimatedDeliveryMin",r.getEstimatedDeliveryMinutes());
            m.put("phone",               r.getPhoneNumber());
            if (r.getAddress() != null) {
                Map<String, Object> addr = new LinkedHashMap<>();
                addr.put("street",  r.getAddress().getStreet());
                addr.put("area",    r.getAddress().getArea());
                addr.put("city",    r.getAddress().getCity());
                addr.put("lat",     r.getAddress().getLatitude());
                addr.put("lng",     r.getAddress().getLongitude());
                m.put("address", addr);
            }
            return m;
        }).collect(Collectors.toList());

        ApiResponse.sendSuccess(ex, payload);
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  GET /api/menu?restaurantId=RST-XXXXXXXX
//  Returns the full menu (grouped by category) for a restaurant.
// ──────────────────────────────────────────────────────────────────────────────
class MenuHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            ApiResponse.sendError(ex, 405, "Method Not Allowed"); return;
        }
        Map<String, String> params = ApiResponse.parseQuery(ex.getRequestURI().getRawQuery());
        String restaurantId = params.get("restaurantId");
        if (restaurantId == null || restaurantId.isBlank()) {
            ApiResponse.sendError(ex, 400, "restaurantId query parameter is required."); return;
        }

        Map<String, List<MenuItem>> grouped =
            MenuService.getInstance().getMenuByCategory(restaurantId);

        Map<String, Object> payload = new LinkedHashMap<>();
        grouped.forEach((category, items) -> {
            List<Map<String, Object>> itemList = items.stream().map(item -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id",          item.getId());
                m.put("name",        item.getName());
                m.put("description", item.getDescription());
                m.put("price",       item.getPrice());
                m.put("available",   item.isOrderable());
                m.put("quantity",    item.getQuantity());
                m.put("addons",      item.getAddons().stream().map(a -> {
                    Map<String, Object> am = new LinkedHashMap<>();
                    am.put("id",         a.getId());
                    am.put("name",       a.getName());
                    am.put("extraPrice", a.getExtraPrice());
                    return am;
                }).collect(Collectors.toList()));
                return m;
            }).collect(Collectors.toList());
            payload.put(category, itemList);
        });

        ApiResponse.sendSuccess(ex, payload);
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  GET /api/order?orderId=ORD-XXXXXXXX
//  Returns full order details including status history.
// ──────────────────────────────────────────────────────────────────────────────
class OrderHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            ApiResponse.sendError(ex, 405, "Method Not Allowed"); return;
        }
        Map<String, String> params = ApiResponse.parseQuery(ex.getRequestURI().getRawQuery());
        String orderId = params.get("orderId");
        if (orderId == null || orderId.isBlank()) {
            ApiResponse.sendError(ex, 400, "orderId query parameter is required."); return;
        }

        Order order = OrderService.getInstance().findById(orderId).orElse(null);
        if (order == null) {
            ApiResponse.sendError(ex, 404, "Order not found: " + orderId); return;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id",             order.getId());
        payload.put("restaurantName", order.getRestaurantName());
        payload.put("status",         order.getStatus().name());
        payload.put("statusMessage",  order.getStatus().getDescription());
        payload.put("subtotal",       order.getSubtotal());
        payload.put("deliveryFee",    order.getDeliveryFee());
        payload.put("discount",       order.getDiscount());
        payload.put("totalAmount",    order.getTotalAmount());
        payload.put("paymentMethod",  order.getPaymentMethod().name());
        payload.put("createdAt",      order.getCreatedAt().toString());
        payload.put("couponCode",     order.getCouponCode());

        List<Map<String, Object>> itemsList = order.getItems().stream().map(item -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name",      item.getMenuItemName());
            m.put("quantity",  item.getQuantity());
            m.put("unitPrice", item.getUnitPrice());
            m.put("lineTotal", item.getLineTotal());
            return m;
        }).collect(Collectors.toList());
        payload.put("items", itemsList);

        Map<String, String> history = new LinkedHashMap<>();
        order.getStatusHistory().forEach((s, t) -> history.put(s.name(), t.toString()));
        payload.put("statusHistory", history);

        ApiResponse.sendSuccess(ex, payload);
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  GET /api/track?orderId=ORD-XXXXXXXX
//  Lightweight tracking endpoint: just the status and rider info.
// ──────────────────────────────────────────────────────────────────────────────
class TrackHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            ApiResponse.sendError(ex, 405, "Method Not Allowed"); return;
        }
        Map<String, String> params = ApiResponse.parseQuery(ex.getRequestURI().getRawQuery());
        String orderId = params.get("orderId");
        if (orderId == null || orderId.isBlank()) {
            ApiResponse.sendError(ex, 400, "orderId required."); return;
        }

        Order order = OrderService.getInstance().findById(orderId).orElse(null);
        if (order == null) { ApiResponse.sendError(ex, 404, "Order not found."); return; }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderId",       order.getId());
        payload.put("status",        order.getStatus().name());
        payload.put("statusMessage", order.getStatus().getDescription());
        payload.put("updatedAt",     order.getUpdatedAt().toString());

        if (order.getRiderId() != null) {
            RiderService.getInstance().findById(order.getRiderId()).ifPresent(r -> {
                Map<String, Object> rm = new LinkedHashMap<>();
                rm.put("name",        r.getName());
                rm.put("phone",       r.getPhone());
                rm.put("vehicleType", r.getVehicleType());
                payload.put("rider", rm);
            });
        }
        ApiResponse.sendSuccess(ex, payload);
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  GET /api/cuisines
//  Returns all distinct cuisine types with restaurant counts.
// ──────────────────────────────────────────────────────────────────────────────
class CuisinesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            ApiResponse.sendError(ex, 405, "Method Not Allowed"); return;
        }
        List<String> cuisines = RestaurantService.getInstance().getAllCuisineTypes();
        ApiResponse.sendSuccess(ex, cuisines);
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  GET /api/coupon?code=WELCOME20&subtotal=500
//  Validates a coupon code and returns the discount amount.
// ──────────────────────────────────────────────────────────────────────────────
class CouponHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            ApiResponse.sendError(ex, 405, "Method Not Allowed"); return;
        }
        Map<String, String> params = ApiResponse.parseQuery(ex.getRequestURI().getRawQuery());
        String code     = params.get("code");
        String subtotalS = params.get("subtotal");

        if (code == null || subtotalS == null) {
            ApiResponse.sendError(ex, 400, "code and subtotal parameters are required."); return;
        }
        double subtotal;
        try { subtotal = Double.parseDouble(subtotalS); }
        catch (NumberFormatException e) { ApiResponse.sendError(ex, 400, "Invalid subtotal."); return; }

        try {
            Coupon c = CouponService.getInstance().validateCoupon(code, subtotal);
            double discount = c.calculateDiscount(subtotal);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("code",            c.getCode());
            m.put("discountPercent", c.getDiscountPercent());
            m.put("discountAmount",  discount);
            m.put("finalAmount",     subtotal - discount);
            ApiResponse.sendSuccess(ex, m);
        } catch (Exception e) {
            ApiResponse.sendError(ex, 400, e.getMessage());
        }
    }
}
