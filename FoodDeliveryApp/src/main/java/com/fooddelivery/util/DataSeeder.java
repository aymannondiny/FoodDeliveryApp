package com.fooddelivery.util;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.RepositoryFactory;
import com.fooddelivery.service.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;

/**
 * Seeds the JSON data files with sample restaurants, menus, users,
 * riders, and coupons for demonstration purposes.
 * Only runs once – skipped if data already exists.
 */
public class DataSeeder {

    public static void seed() {
        if (RepositoryFactory.restaurants().count() > 0) return; // Already seeded

        System.out.println("Seeding sample data...");

        // ── Users ──────────────────────────────────────────────────────────
        AuthService auth = AuthService.getInstance();

        User customer = auth.register("Rahim Uddin", "rahim@example.com", "password123",
                                      "01711000001", User.Role.CUSTOMER);
        customer.setDefaultAddress(new Address("12 Dhanmondi Lake Road", "Dhanmondi",
                                               "Dhaka", "1205", 23.7461, 90.3742));
        RepositoryFactory.users().save(customer.getId(), customer);

        User owner1 = auth.register("Kamal Hossain", "kamal@example.com", "password123",
                                    "01711000002", User.Role.RESTAURANT_OWNER);
        User owner2 = auth.register("Nasrin Akter", "nasrin@example.com", "password123",
                                    "01711000003", User.Role.RESTAURANT_OWNER);
        auth.logout();

        // ── Riders ─────────────────────────────────────────────────────────
        RiderService riderSvc = RiderService.getInstance();
        User riderUser = auth.register("Farhan Islam", "farhan@example.com", "password123",
                                       "01711000004", User.Role.RIDER);
        auth.logout();
        Rider rider = riderSvc.register(riderUser.getId(), "Farhan Islam", "01711000004", "Bike");
        rider.setCurrentArea("Dhanmondi");
        RepositoryFactory.riders().save(rider.getId(), rider);

        // ── Restaurant 1 – Dhaka Bites ─────────────────────────────────────
        RestaurantService restSvc = RestaurantService.getInstance();
        MenuService menuSvc = MenuService.getInstance();

        Address addr1 = new Address("45 Road 27", "Dhanmondi", "Dhaka", "1209", 23.7512, 90.3752);
        Restaurant r1 = restSvc.register(owner1.getId(), "Dhaka Bites",
                "Bangladeshi", addr1, "01800000001", 15.0, 100.0, 30);
        r1.setDescription("Authentic Bangladeshi home-style cooking");
        r1.setSchedule(Schedule.allDay("08:00", "23:00"));
        r1.setRating(4.5); r1.setTotalRatings(120);
        RepositoryFactory.restaurants().save(r1.getId(), r1);

        MenuItem biriyani = menuSvc.addMenuItem(r1.getId(), "Kacchi Biriyani",
                "Slow-cooked mutton biriyani with saffron rice", "Mains", 280.0);
        menuSvc.addAddon(biriyani.getId(), "Extra Raita", 20.0);
        menuSvc.addAddon(biriyani.getId(), "Extra Meat", 60.0);

        MenuItem tehari = menuSvc.addMenuItem(r1.getId(), "Beef Tehari",
                "Spiced beef and rice", "Mains", 200.0);
        menuSvc.addAddon(tehari.getId(), "Extra Achar", 10.0);

        menuSvc.addMenuItem(r1.getId(), "Lentil Soup (Dal)",
                "Classic red lentil soup", "Starters", 60.0);
        menuSvc.addMenuItem(r1.getId(), "Chicken Rezala",
                "Creamy white curry", "Mains", 220.0);
        menuSvc.addMenuItem(r1.getId(), "Mishti Doi",
                "Sweet yoghurt dessert", "Desserts", 60.0);
        menuSvc.addMenuItem(r1.getId(), "Borhani",
                "Spiced yoghurt drink", "Drinks", 50.0);

        // ── Restaurant 2 – Dragon Palace ───────────────────────────────────
        Address addr2 = new Address("8 Gulshan Avenue", "Gulshan", "Dhaka", "1212", 23.7808, 90.4148);
        Restaurant r2 = restSvc.register(owner2.getId(), "Dragon Palace",
                "Chinese", addr2, "01800000002", 20.0, 150.0, 40);
        r2.setDescription("Authentic Chinese cuisine in the heart of Gulshan");
        r2.setSchedule(new Schedule("11:00", "22:30",
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                           DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)));
        r2.setRating(4.2); r2.setTotalRatings(85);
        RepositoryFactory.restaurants().save(r2.getId(), r2);

        MenuItem friedRice = menuSvc.addMenuItem(r2.getId(), "Chicken Fried Rice",
                "Classic wok-tossed fried rice", "Mains", 180.0);
        menuSvc.addAddon(friedRice.getId(), "Extra Egg", 20.0);
        menuSvc.addAddon(friedRice.getId(), "Chilli Sauce", 15.0);

        menuSvc.addMenuItem(r2.getId(), "Dim Sum Basket (6 pcs)",
                "Steamed assorted dim sum", "Starters", 160.0);
        menuSvc.addMenuItem(r2.getId(), "Kung Pao Chicken",
                "Spicy stir-fried chicken with peanuts", "Mains", 240.0);
        menuSvc.addMenuItem(r2.getId(), "Beef Chow Mein",
                "Stir-fried noodles with beef", "Mains", 220.0);
        menuSvc.addMenuItem(r2.getId(), "Spring Rolls (4 pcs)",
                "Crispy vegetable spring rolls", "Starters", 100.0);
        menuSvc.addMenuItem(r2.getId(), "Mango Pudding",
                "Silky smooth mango pudding", "Desserts", 90.0);

        // ── Restaurant 3 – Burger Republic ────────────────────────────────
        Address addr3 = new Address("22 Banani Road 11", "Banani", "Dhaka", "1213", 23.7939, 90.4053);
        Restaurant r3 = restSvc.register(owner1.getId(), "Burger Republic",
                "Fast Food", addr3, "01800000003", 12.0, 80.0, 20);
        r3.setDescription("Gourmet burgers & sides, fast!");
        r3.setSchedule(Schedule.allDay("10:00", "00:00"));
        r3.setRating(4.7); r3.setTotalRatings(340);
        RepositoryFactory.restaurants().save(r3.getId(), r3);

        MenuItem classic = menuSvc.addMenuItem(r3.getId(), "Classic Beef Burger",
                "Angus beef patty, lettuce, tomato, cheese", "Burgers", 220.0);
        menuSvc.addAddon(classic.getId(), "Extra Cheese", 30.0);
        menuSvc.addAddon(classic.getId(), "Bacon", 50.0);
        menuSvc.addAddon(classic.getId(), "Jalapeños", 20.0);

        menuSvc.addMenuItem(r3.getId(), "Crispy Chicken Burger",
                "Crunchy fried chicken fillet with coleslaw", "Burgers", 190.0);
        menuSvc.addMenuItem(r3.getId(), "French Fries (Large)",
                "Golden crispy fries", "Sides", 80.0);
        menuSvc.addMenuItem(r3.getId(), "Onion Rings",
                "Beer-battered onion rings", "Sides", 90.0);
        menuSvc.addMenuItem(r3.getId(), "Chocolate Milkshake",
                "Thick and creamy", "Drinks", 120.0);
        menuSvc.addMenuItem(r3.getId(), "Soft Drink (330ml)",
                "Coke / Pepsi / 7UP", "Drinks", 50.0);

        // ── Coupons ────────────────────────────────────────────────────────
        CouponService couponSvc = CouponService.getInstance();
        couponSvc.createCoupon("WELCOME20", 20, 100, 150,
                LocalDate.now().plusMonths(3), -1);
        couponSvc.createCoupon("FLAT50", 50, 50, 300,
                LocalDate.now().plusMonths(1), 200);
        couponSvc.createCoupon("NEWUSER", 15, 80, 100,
                LocalDate.now().plusMonths(6), -1);

        System.out.println("Sample data seeded successfully.");
    }
}
