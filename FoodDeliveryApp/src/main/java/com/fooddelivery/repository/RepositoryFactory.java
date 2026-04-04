package com.fooddelivery.repository;

/**
 * Central access point for all repository singletons.
 * Services obtain repositories exclusively through this factory.
 */
public class RepositoryFactory {

    private RepositoryFactory() {}

    public static UserRepository        users()       { return UserRepository.getInstance(); }
    public static RestaurantRepository  restaurants() { return RestaurantRepository.getInstance(); }
    public static MenuItemRepository    menuItems()   { return MenuItemRepository.getInstance(); }
    public static OrderRepository       orders()      { return OrderRepository.getInstance(); }
    public static RiderRepository       riders()      { return RiderRepository.getInstance(); }
    public static CouponRepository      coupons()     { return CouponRepository.getInstance(); }
    public static PaymentRepository     payments()    { return PaymentRepository.getInstance(); }
}
