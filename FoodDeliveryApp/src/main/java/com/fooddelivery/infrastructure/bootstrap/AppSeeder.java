package com.fooddelivery.infrastructure.bootstrap;

import com.fooddelivery.application.auth.request.RegisterUserCommand;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Coupon;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Rider;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.Schedule;
import com.fooddelivery.model.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;

/**
 * Seeds demo/sample data using AppContext and application services.
 * This avoids legacy service singletons and RepositoryFactory usage.
 */
public class AppSeeder {

    private final AppContext context;

    public AppSeeder(AppContext context) {
        this.context = context;
    }

    public void seedIfNeeded() {
        if (context.restaurantRepository().count() > 0) {
            return;
        }

        System.out.println("Seeding sample data...");

        // ── Users ──────────────────────────────────────────────────────────
        User customer = registerUser(
                "Rahim Uddin",
                "rahim@example.com",
                "password123",
                "01711000001",
                User.Role.CUSTOMER
        );

        customer.setDefaultAddress(new Address(
                "12 Dhanmondi Lake Road",
                "Dhanmondi",
                "Dhaka",
                "1205",
                23.7461,
                90.3742
        ));
        context.userRepository().save(customer.getId(), customer);

        User owner1 = registerUser(
                "Kamal Hossain",
                "kamal@example.com",
                "password123",
                "01711000002",
                User.Role.RESTAURANT_OWNER
        );

        User owner2 = registerUser(
                "Nasrin Akter",
                "nasrin@example.com",
                "password123",
                "01711000003",
                User.Role.RESTAURANT_OWNER
        );

        User riderUser = registerUser(
                "Farhan Islam",
                "farhan@example.com",
                "password123",
                "01711000004",
                User.Role.RIDER
        );

        // ── Rider ──────────────────────────────────────────────────────────
        Rider rider = context.registerRiderUseCase().execute(
                riderUser.getId(),
                "Farhan Islam",
                "01711000004",
                "Bike"
        );
        rider.setCurrentArea("Dhanmondi");
        context.riderRepository().save(rider.getId(), rider);

        // ── Restaurants + Menus ────────────────────────────────────────────
        seedDhakaBites(owner1);
        seedDragonPalace(owner2);
        seedBurgerRepublic(owner1);

        // ── Coupons ────────────────────────────────────────────────────────
        createCoupon("WELCOME20", 20, 100, 150, LocalDate.now().plusMonths(3), -1);
        createCoupon("FLAT50", 50, 50, 300, LocalDate.now().plusMonths(1), 200);
        createCoupon("NEWUSER", 15, 80, 100, LocalDate.now().plusMonths(6), -1);

        System.out.println("Sample data seeded successfully.");
    }

    private User registerUser(String name,
                              String email,
                              String password,
                              String phone,
                              User.Role role) {
        return context.registerUserUseCase()
                .execute(new RegisterUserCommand(name, email, password, phone, role))
                .getUser();
    }

    private void createCoupon(String code,
                              double discountPercent,
                              double maxDiscountAmount,
                              double minOrderAmount,
                              LocalDate expiryDate,
                              int usageLimit) {
        Coupon coupon = context.couponCommandService().createCoupon(
                code,
                discountPercent,
                maxDiscountAmount,
                minOrderAmount,
                expiryDate,
                usageLimit
        );
        context.couponRepository().save(coupon.getId(), coupon);
    }

    private void seedDhakaBites(User owner) {
        Address address = new Address(
                "45 Road 27",
                "Dhanmondi",
                "Dhaka",
                "1209",
                23.7512,
                90.3752
        );

        Restaurant restaurant = context.restaurantRegistrationUseCase().execute(
                owner.getId(),
                "Dhaka Bites",
                "Bangladeshi",
                address,
                "01800000001",
                15.0,
                100.0,
                30
        );

        restaurant.setDescription("Authentic Bangladeshi home-style cooking");
        restaurant.setSchedule(Schedule.allDay("08:00", "23:00"));
        restaurant.setRating(0.0);
        restaurant.setTotalRatings(0);
        context.restaurantRepository().save(restaurant.getId(), restaurant);

        MenuItem biriyani = context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Kacchi Biriyani",
                "Slow-cooked mutton biriyani with saffron rice",
                "Mains",
                280.0
        );
        context.menuManagementService().addAddon(biriyani.getId(), "Extra Raita", 20.0);
        context.menuManagementService().addAddon(biriyani.getId(), "Extra Meat", 60.0);

        MenuItem tehari = context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Beef Tehari",
                "Spiced beef and rice",
                "Mains",
                200.0
        );
        context.menuManagementService().addAddon(tehari.getId(), "Extra Achar", 10.0);

        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Lentil Soup (Dal)",
                "Classic red lentil soup",
                "Starters",
                60.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Chicken Rezala",
                "Creamy white curry",
                "Mains",
                220.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Mishti Doi",
                "Sweet yoghurt dessert",
                "Desserts",
                60.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Borhani",
                "Spiced yoghurt drink",
                "Drinks",
                50.0
        );
    }

    private void seedDragonPalace(User owner) {
        Address address = new Address(
                "8 Gulshan Avenue",
                "Gulshan",
                "Dhaka",
                "1212",
                23.7808,
                90.4148
        );

        Restaurant restaurant = context.restaurantRegistrationUseCase().execute(
                owner.getId(),
                "Dragon Palace",
                "Chinese",
                address,
                "01800000002",
                20.0,
                150.0,
                40
        );

        restaurant.setDescription("Authentic Chinese cuisine in the heart of Gulshan");
        restaurant.setSchedule(new Schedule(
                "11:00",
                "22:30",
                EnumSet.of(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY,
                        DayOfWeek.SATURDAY
                )
        ));
        restaurant.setRating(0.0);
        restaurant.setTotalRatings(0);
        context.restaurantRepository().save(restaurant.getId(), restaurant);

        MenuItem friedRice = context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Chicken Fried Rice",
                "Classic wok-tossed fried rice",
                "Mains",
                180.0
        );
        context.menuManagementService().addAddon(friedRice.getId(), "Extra Egg", 20.0);
        context.menuManagementService().addAddon(friedRice.getId(), "Chilli Sauce", 15.0);

        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Dim Sum Basket (6 pcs)",
                "Steamed assorted dim sum",
                "Starters",
                160.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Kung Pao Chicken",
                "Spicy stir-fried chicken with peanuts",
                "Mains",
                240.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Beef Chow Mein",
                "Stir-fried noodles with beef",
                "Mains",
                220.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Spring Rolls (4 pcs)",
                "Crispy vegetable spring rolls",
                "Starters",
                100.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Mango Pudding",
                "Silky smooth mango pudding",
                "Desserts",
                90.0
        );
    }

    private void seedBurgerRepublic(User owner) {
        Address address = new Address(
                "22 Banani Road 11",
                "Banani",
                "Dhaka",
                "1213",
                23.7939,
                90.4053
        );

        Restaurant restaurant = context.restaurantRegistrationUseCase().execute(
                owner.getId(),
                "Burger Republic",
                "Fast Food",
                address,
                "01800000003",
                12.0,
                80.0,
                20
        );

        restaurant.setDescription("Gourmet burgers & sides, fast!");
        restaurant.setSchedule(Schedule.allDay("10:00", "00:00"));
        restaurant.setRating(0.0);
        restaurant.setTotalRatings(0);
        context.restaurantRepository().save(restaurant.getId(), restaurant);

        MenuItem classic = context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Classic Beef Burger",
                "Angus beef patty, lettuce, tomato, cheese",
                "Burgers",
                220.0
        );
        context.menuManagementService().addAddon(classic.getId(), "Extra Cheese", 30.0);
        context.menuManagementService().addAddon(classic.getId(), "Bacon", 50.0);
        context.menuManagementService().addAddon(classic.getId(), "Jalapeños", 20.0);

        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Crispy Chicken Burger",
                "Crunchy fried chicken fillet with coleslaw",
                "Burgers",
                190.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "French Fries (Large)",
                "Golden crispy fries",
                "Sides",
                80.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Onion Rings",
                "Beer-battered onion rings",
                "Sides",
                90.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Chocolate Milkshake",
                "Thick and creamy",
                "Drinks",
                120.0
        );
        context.menuManagementService().addMenuItem(
                restaurant.getId(),
                "Soft Drink (330ml)",
                "Coke / Pepsi / 7UP",
                "Drinks",
                50.0
        );
    }
}