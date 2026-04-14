package com.fooddelivery.api.menu;

import com.fooddelivery.api.common.Mapper;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.MenuItemAddon;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuResponseMapper implements Mapper<Map<String, List<MenuItem>>, Map<String, Object>> {

    @Override
    public Map<String, Object> map(Map<String, List<MenuItem>> groupedMenu) {
        Map<String, Object> payload = new LinkedHashMap<>();

        groupedMenu.forEach((category, items) -> {
            List<Map<String, Object>> itemList = items.stream()
                    .map(this::mapMenuItem)
                    .collect(Collectors.toList());

            payload.put(category, itemList);
        });

        return payload;
    }

    private Map<String, Object> mapMenuItem(MenuItem item) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", item.getId());
        m.put("name", item.getName());
        m.put("description", item.getDescription());
        m.put("price", item.getPrice());
        m.put("available", item.isOrderable());
        m.put("quantity", item.getQuantity());

        List<MenuItemAddon> addonsRaw = item.getAddons();
        List<Map<String, Object>> addons = addonsRaw == null
                ? Collections.emptyList()
                : addonsRaw.stream().map(this::mapAddon).collect(Collectors.toList());

        m.put("addons", addons);
        return m;
    }

    private Map<String, Object> mapAddon(MenuItemAddon addon) {
        Map<String, Object> am = new LinkedHashMap<>();
        am.put("id", addon.getId());
        am.put("name", addon.getName());
        am.put("extraPrice", addon.getExtraPrice());
        return am;
    }
}