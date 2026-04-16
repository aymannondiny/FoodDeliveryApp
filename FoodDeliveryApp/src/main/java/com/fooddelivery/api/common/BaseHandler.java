package com.fooddelivery.api.common;

import com.fooddelivery.api.ApiResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public abstract class BaseHandler implements HttpHandler {

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        if (!getSupportedMethod().equalsIgnoreCase(exchange.getRequestMethod())) {
            ApiResponse.sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            Map<String, String> params =
                    ApiResponse.parseQuery(exchange.getRequestURI().getRawQuery());

            Object responseBody = handleRequest(exchange, params);
            ApiResponse.sendSuccess(exchange, responseBody);

        } catch (ApiException e) {
            ApiResponse.sendError(exchange, e.getStatusCode(), e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse.sendError(exchange, 500, "Internal Server Error");
        }
    }

    protected String getSupportedMethod() {
        return "GET";
    }

    protected abstract Object handleRequest(HttpExchange exchange,
                                            Map<String, String> params) throws Exception;
}