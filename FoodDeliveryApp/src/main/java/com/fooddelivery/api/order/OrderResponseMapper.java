package com.fooddelivery.api.order;

import com.fooddelivery.api.common.Mapper;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderResponseMapper implements Mapper<Order, Map<String, Object>> {

    @Override
    public Map<String, Object> map(Order order) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", order.getId());
        payload.put("restaurantName", order.getRestaurantName());
        payload.put("status", order.getStatus() != null ? order.getStatus().name() : null);
        payload.put("statusMessage", order.getStatus() != null ? order.getStatus().getDescription() : null);
        payload.put("subtotal", order.getSubtotal());
        payload.put("deliveryFee", order.getDeliveryFee());
        payload.put("discount", order.getDiscount());
        payload.put("totalAmount", order.getTotalAmount());
        payload.put("paymentMethod", order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
        payload.put("createdAt", order.getCreatedAt() != null ? order.getCreatedAt().toString() : null);
        payload.put("couponCode", order.getCouponCode());

        List<OrderItem> rawItems = order.getItems();
        List<Map<String, Object>> items = rawItems == null
                ? Collections.emptyList()
                : rawItems.stream().map(this::mapItem).collect(Collectors.toList());

        payload.put("items", items);

        Map<String, String> history = new LinkedHashMap<>();
        if (order.getStatusHistory() != null) {
            order.getStatusHistory().forEach((status, time) ->
                    history.put(status.name(), time != null ? time.toString() : null));
        }
        payload.put("statusHistory", history);

        return payload;
    }

    private Map<String, Object> mapItem(OrderItem item) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", item.getMenuItemName());
        m.put("quantity", item.getQuantity());
        m.put("unitPrice", item.getUnitPrice());
        m.put("lineTotal", item.getLineTotal());
        return m;
    }
}