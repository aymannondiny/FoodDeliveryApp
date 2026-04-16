package com.fooddelivery.api.order;

import com.fooddelivery.api.common.BaseHandler;
import com.fooddelivery.api.common.Mapper;
import com.fooddelivery.api.common.NotFoundException;
import com.fooddelivery.api.common.RequestParams;
import com.fooddelivery.model.Order;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class OrderHandler extends BaseHandler {

    private final OrderReader orderReader;
    private final Mapper<Order, Map<String, Object>> orderMapper;

    public OrderHandler(OrderReader orderReader,
                        Mapper<Order, Map<String, Object>> orderMapper) {
        this.orderReader = orderReader;
        this.orderMapper = orderMapper;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange, Map<String, String> params) {
        String orderId = RequestParams.required(params, "orderId");

        Order order = orderReader.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        return orderMapper.map(order);
    }
}