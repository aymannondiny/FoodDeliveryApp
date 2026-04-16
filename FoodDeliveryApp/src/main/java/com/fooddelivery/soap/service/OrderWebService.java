package com.fooddelivery.soap.service;

import com.fooddelivery.application.order.GetOrderByIdUseCase;
import com.fooddelivery.application.rider.FindRiderByIdUseCase;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Rider;
import com.fooddelivery.soap.dto.SoapOrder;
import com.fooddelivery.soap.dto.SoapOrderItem;
import com.fooddelivery.soap.dto.SoapRiderInfo;
import com.fooddelivery.soap.dto.SoapTrackingInfo;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.stream.Collectors;

@WebService(serviceName = "OrderService", targetNamespace = "http://fooddelivery.com/soap")
public class OrderWebService {

    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final FindRiderByIdUseCase findRiderByIdUseCase;

    public OrderWebService(GetOrderByIdUseCase getOrderByIdUseCase,
                           FindRiderByIdUseCase findRiderByIdUseCase) {
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.findRiderByIdUseCase = findRiderByIdUseCase;
    }

    @WebMethod
    public SoapOrder getOrder(@WebParam(name = "orderId") String orderId) {
        Order order = getOrderByIdUseCase.execute(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        return toSoapOrder(order);
    }

    @WebMethod
    public SoapTrackingInfo trackOrder(@WebParam(name = "orderId") String orderId) {
        Order order = getOrderByIdUseCase.execute(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        SoapTrackingInfo tracking = new SoapTrackingInfo();
        tracking.setOrderId(order.getId());
        tracking.setStatus(order.getStatus().name());
        tracking.setStatusMessage(order.getStatus().getDescription());
        tracking.setUpdatedAt(order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null);

        if (order.getRiderId() != null) {
            findRiderByIdUseCase.execute(order.getRiderId()).ifPresent(rider -> {
                SoapRiderInfo riderInfo = new SoapRiderInfo();
                riderInfo.setName(rider.getName());
                riderInfo.setPhone(rider.getPhone());
                riderInfo.setVehicleType(rider.getVehicleType());
                tracking.setRider(riderInfo);
            });
        }

        return tracking;
    }

    private SoapOrder toSoapOrder(Order order) {
        SoapOrder soap = new SoapOrder();
        soap.setId(order.getId());
        soap.setRestaurantName(order.getRestaurantName());
        soap.setStatus(order.getStatus().name());
        soap.setStatusMessage(order.getStatus().getDescription());
        soap.setSubtotal(order.getSubtotal());
        soap.setDeliveryFee(order.getDeliveryFee());
        soap.setDiscount(order.getDiscount());
        soap.setTotalAmount(order.getTotalAmount());
        soap.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
        soap.setCreatedAt(order.getCreatedAt() != null ? order.getCreatedAt().toString() : null);
        soap.setCouponCode(order.getCouponCode());

        if (order.getItems() != null) {
            soap.setItems(order.getItems().stream().map(item -> {
                SoapOrderItem soapItem = new SoapOrderItem();
                soapItem.setName(item.getMenuItemName());
                soapItem.setQuantity(item.getQuantity());
                soapItem.setUnitPrice(item.getUnitPrice());
                soapItem.setLineTotal(item.getLineTotal());
                return soapItem;
            }).collect(Collectors.toList()));
        }

        return soap;
    }
}