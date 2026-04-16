package com.fooddelivery.soap.service;

import com.fooddelivery.application.restaurant.RestaurantQueryService;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.soap.dto.SoapAddress;
import com.fooddelivery.soap.dto.SoapRestaurant;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;
import java.util.stream.Collectors;

@WebService(serviceName = "RestaurantService", targetNamespace = "http://fooddelivery.com/soap")
public class RestaurantWebService {

    private final RestaurantQueryService restaurantQueryService;

    public RestaurantWebService(RestaurantQueryService restaurantQueryService) {
        this.restaurantQueryService = restaurantQueryService;
    }

    @WebMethod
    public List<SoapRestaurant> getAllRestaurants() {
        return restaurantQueryService.getAll().stream()
                .filter(Restaurant::isApproved)
                .map(this::toSoap)
                .collect(Collectors.toList());
    }

    @WebMethod
    public List<SoapRestaurant> searchRestaurants(@WebParam(name = "query") String query) {
        return restaurantQueryService.search(query).stream()
                .map(this::toSoap)
                .collect(Collectors.toList());
    }

    @WebMethod
    public List<SoapRestaurant> filterByCuisine(@WebParam(name = "cuisine") String cuisine) {
        return restaurantQueryService.filterByCuisine(cuisine).stream()
                .map(this::toSoap)
                .collect(Collectors.toList());
    }

    @WebMethod
    public List<String> getAllCuisineTypes() {
        return restaurantQueryService.getAllCuisineTypes();
    }

    private SoapRestaurant toSoap(Restaurant r) {
        SoapRestaurant soap = new SoapRestaurant();
        soap.setId(r.getId());
        soap.setName(r.getName());
        soap.setCuisineType(r.getCuisineType());
        soap.setDescription(r.getDescription());
        soap.setRating(r.getRating());
        soap.setTotalRatings(r.getTotalRatings());
        soap.setOpen(r.isCurrentlyOpen());
        soap.setMinOrderAmount(r.getMinOrderAmount());
        soap.setDeliveryFeePerKm(r.getDeliveryFeePerKm());
        soap.setEstimatedDeliveryMinutes(r.getEstimatedDeliveryMinutes());
        soap.setPhone(r.getPhoneNumber());

        if (r.getAddress() != null) {
            soap.setAddress(new SoapAddress(
                    r.getAddress().getStreet(),
                    r.getAddress().getArea(),
                    r.getAddress().getCity(),
                    r.getAddress().getLatitude(),
                    r.getAddress().getLongitude()
            ));
        }

        return soap;
    }
}