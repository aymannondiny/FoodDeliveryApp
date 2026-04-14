package com.fooddelivery.api.menu;

import com.fooddelivery.api.common.BaseHandler;
import com.fooddelivery.api.common.Mapper;
import com.fooddelivery.api.common.RequestParams;
import com.fooddelivery.model.MenuItem;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;

public class MenuHandler extends BaseHandler {

    private final MenuReader menuReader;
    private final Mapper<Map<String, List<MenuItem>>, Map<String, Object>> menuMapper;

    public MenuHandler(MenuReader menuReader,
                       Mapper<Map<String, List<MenuItem>>, Map<String, Object>> menuMapper) {
        this.menuReader = menuReader;
        this.menuMapper = menuMapper;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange, Map<String, String> params) {
        String restaurantId = RequestParams.required(params, "restaurantId");
        return menuMapper.map(menuReader.getMenuByCategory(restaurantId));
    }
}