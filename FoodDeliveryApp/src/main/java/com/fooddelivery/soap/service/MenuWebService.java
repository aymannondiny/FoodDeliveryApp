package com.fooddelivery.soap.service;

import com.fooddelivery.application.menu.MenuQueryService;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.soap.dto.SoapMenuAddon;
import com.fooddelivery.soap.dto.SoapMenuItem;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;
import java.util.stream.Collectors;

@WebService(serviceName = "MenuService", targetNamespace = "http://fooddelivery.com/soap")
public class MenuWebService {

    private final MenuQueryService menuQueryService;

    public MenuWebService(MenuQueryService menuQueryService) {
        this.menuQueryService = menuQueryService;
    }

    @WebMethod
    public List<SoapMenuItem> getMenu(@WebParam(name = "restaurantId") String restaurantId) {
        return menuQueryService.getAvailableMenu(restaurantId).stream()
                .map(this::toSoap)
                .collect(Collectors.toList());
    }

    private SoapMenuItem toSoap(MenuItem item) {
        SoapMenuItem soap = new SoapMenuItem();
        soap.setId(item.getId());
        soap.setName(item.getName());
        soap.setDescription(item.getDescription());
        soap.setCategory(item.getCategory());
        soap.setPrice(item.getPrice());
        soap.setAvailable(item.isOrderable());
        soap.setQuantity(item.getQuantity());

        if (item.getAddons() != null) {
            soap.setAddons(item.getAddons().stream()
                    .map(a -> new SoapMenuAddon(a.getId(), a.getName(), a.getExtraPrice()))
                    .collect(Collectors.toList()));
        }

        return soap;
    }
}