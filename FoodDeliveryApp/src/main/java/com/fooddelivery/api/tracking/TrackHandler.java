package com.fooddelivery.api.tracking;

import com.fooddelivery.api.common.BaseHandler;
import com.fooddelivery.api.common.Mapper;
import com.fooddelivery.api.common.NotFoundException;
import com.fooddelivery.api.common.RequestParams;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class TrackHandler extends BaseHandler {

    private final TrackingReader trackingReader;
    private final Mapper<TrackingInfo, Map<String, Object>> trackingMapper;

    public TrackHandler(TrackingReader trackingReader,
                        Mapper<TrackingInfo, Map<String, Object>> trackingMapper) {
        this.trackingReader = trackingReader;
        this.trackingMapper = trackingMapper;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange, Map<String, String> params) {
        String orderId = RequestParams.required(params, "orderId");

        TrackingInfo trackingInfo = trackingReader.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        return trackingMapper.map(trackingInfo);
    }
}