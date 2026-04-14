package com.fooddelivery.infrastructure.bootstrap;

import com.fooddelivery.application.auth.GetCurrentUserUseCase;
import com.fooddelivery.application.auth.LoginUseCase;
import com.fooddelivery.application.auth.LogoutUseCase;
import com.fooddelivery.application.auth.RegisterUserUseCase;
import com.fooddelivery.application.cart.AddCartItemUseCase;
import com.fooddelivery.application.cart.ClearCartUseCase;
import com.fooddelivery.application.cart.GetCartUseCase;
import com.fooddelivery.application.cart.RemoveCartItemUseCase;
import com.fooddelivery.application.cart.UpdateCartItemQuantityUseCase;
import com.fooddelivery.application.common.EmailValidator;
import com.fooddelivery.application.common.IdGenerator;
import com.fooddelivery.application.common.PasswordHasher;
import com.fooddelivery.application.coupon.CouponCommandService;
import com.fooddelivery.application.coupon.CouponQueryService;
import com.fooddelivery.application.coupon.CouponValidationUseCase;
import com.fooddelivery.application.menu.MenuManagementService;
import com.fooddelivery.application.menu.MenuQueryService;
import com.fooddelivery.application.order.AdvanceOrderStatusUseCase;
import com.fooddelivery.application.order.CancelOrderUseCase;
import com.fooddelivery.application.order.CompleteDeliveryUseCase;
import com.fooddelivery.application.order.GetActiveRestaurantOrdersUseCase;
import com.fooddelivery.application.order.GetOrderByIdUseCase;
import com.fooddelivery.application.order.GetOrderHistoryUseCase;
import com.fooddelivery.application.order.GetRestaurantOrdersUseCase;
import com.fooddelivery.application.order.PlaceOrderUseCase;
import com.fooddelivery.application.payment.GetPaymentForOrderUseCase;
import com.fooddelivery.application.restaurant.RestaurantManagementService;
import com.fooddelivery.application.restaurant.RestaurantQueryService;
import com.fooddelivery.application.restaurant.RestaurantRegistrationUseCase;
import com.fooddelivery.application.rider.FindRiderByIdUseCase;
import com.fooddelivery.domain.policy.OrderStatusPolicy;
import com.fooddelivery.domain.repository.CartRepository;
import com.fooddelivery.domain.repository.CouponRepository;
import com.fooddelivery.domain.repository.MenuItemRepository;
import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.repository.PaymentRepository;
import com.fooddelivery.domain.repository.RestaurantRepository;
import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.domain.repository.UserRepository;
import com.fooddelivery.domain.service.DeliveryFeeCalculator;
import com.fooddelivery.domain.service.OrderPaymentProcessor;
import com.fooddelivery.domain.service.RiderAssigner;
import com.fooddelivery.infrastructure.order.DefaultDeliveryFeeCalculator;
import com.fooddelivery.infrastructure.order.DefaultOrderStatusPolicy;
import com.fooddelivery.infrastructure.order.RepositoryRiderAssigner;
import com.fooddelivery.infrastructure.payment.LegacyOrderPaymentProcessor;
import com.fooddelivery.infrastructure.repository.memory.InMemoryCartRepository;
import com.fooddelivery.infrastructure.security.RegexEmailValidator;
import com.fooddelivery.infrastructure.security.Sha256PasswordHasher;
import com.fooddelivery.infrastructure.session.CurrentSession;
import com.fooddelivery.infrastructure.session.InMemoryCurrentSession;
import com.fooddelivery.infrastructure.util.UuidIdGenerator;

public final class AppContext {

    private static final AppContext INSTANCE = build();

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;
    private final CouponRepository couponRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;

    private final EmailValidator emailValidator;
    private final PasswordHasher passwordHasher;
    private final IdGenerator idGenerator;
    private final CurrentSession currentSession;

    private final DeliveryFeeCalculator deliveryFeeCalculator;
    private final RiderAssigner riderAssigner;
    private final OrderPaymentProcessor orderPaymentProcessor;
    private final OrderStatusPolicy orderStatusPolicy;

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    private final GetCartUseCase getCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final ClearCartUseCase clearCartUseCase;

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final GetOrderHistoryUseCase getOrderHistoryUseCase;
    private final GetRestaurantOrdersUseCase getRestaurantOrdersUseCase;
    private final GetActiveRestaurantOrdersUseCase getActiveRestaurantOrdersUseCase;
    private final AdvanceOrderStatusUseCase advanceOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompleteDeliveryUseCase completeDeliveryUseCase;

    private final RestaurantRegistrationUseCase restaurantRegistrationUseCase;
    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantManagementService restaurantManagementService;

    private final MenuQueryService menuQueryService;
    private final MenuManagementService menuManagementService;

    private final CouponCommandService couponCommandService;
    private final CouponQueryService couponQueryService;
    private final CouponValidationUseCase couponValidationUseCase;

    private final GetPaymentForOrderUseCase getPaymentForOrderUseCase;
    private final FindRiderByIdUseCase findRiderByIdUseCase;

    private AppContext(UserRepository userRepository,
                       RestaurantRepository restaurantRepository,
                       MenuItemRepository menuItemRepository,
                       OrderRepository orderRepository,
                       RiderRepository riderRepository,
                       CouponRepository couponRepository,
                       PaymentRepository paymentRepository,
                       CartRepository cartRepository,
                       EmailValidator emailValidator,
                       PasswordHasher passwordHasher,
                       IdGenerator idGenerator,
                       CurrentSession currentSession,
                       DeliveryFeeCalculator deliveryFeeCalculator,
                       RiderAssigner riderAssigner,
                       OrderPaymentProcessor orderPaymentProcessor,
                       OrderStatusPolicy orderStatusPolicy,
                       RegisterUserUseCase registerUserUseCase,
                       LoginUseCase loginUseCase,
                       LogoutUseCase logoutUseCase,
                       GetCurrentUserUseCase getCurrentUserUseCase,
                       GetCartUseCase getCartUseCase,
                       AddCartItemUseCase addCartItemUseCase,
                       RemoveCartItemUseCase removeCartItemUseCase,
                       UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase,
                       ClearCartUseCase clearCartUseCase,
                       PlaceOrderUseCase placeOrderUseCase,
                       GetOrderByIdUseCase getOrderByIdUseCase,
                       GetOrderHistoryUseCase getOrderHistoryUseCase,
                       GetRestaurantOrdersUseCase getRestaurantOrdersUseCase,
                       GetActiveRestaurantOrdersUseCase getActiveRestaurantOrdersUseCase,
                       AdvanceOrderStatusUseCase advanceOrderStatusUseCase,
                       CancelOrderUseCase cancelOrderUseCase,
                       CompleteDeliveryUseCase completeDeliveryUseCase,
                       RestaurantRegistrationUseCase restaurantRegistrationUseCase,
                       RestaurantQueryService restaurantQueryService,
                       RestaurantManagementService restaurantManagementService,
                       MenuQueryService menuQueryService,
                       MenuManagementService menuManagementService,
                       CouponCommandService couponCommandService,
                       CouponQueryService couponQueryService,
                       CouponValidationUseCase couponValidationUseCase,
                       GetPaymentForOrderUseCase getPaymentForOrderUseCase,
                       FindRiderByIdUseCase findRiderByIdUseCase) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.riderRepository = riderRepository;
        this.couponRepository = couponRepository;
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.emailValidator = emailValidator;
        this.passwordHasher = passwordHasher;
        this.idGenerator = idGenerator;
        this.currentSession = currentSession;
        this.deliveryFeeCalculator = deliveryFeeCalculator;
        this.riderAssigner = riderAssigner;
        this.orderPaymentProcessor = orderPaymentProcessor;
        this.orderStatusPolicy = orderStatusPolicy;
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.getCartUseCase = getCartUseCase;
        this.addCartItemUseCase = addCartItemUseCase;
        this.removeCartItemUseCase = removeCartItemUseCase;
        this.updateCartItemQuantityUseCase = updateCartItemQuantityUseCase;
        this.clearCartUseCase = clearCartUseCase;
        this.placeOrderUseCase = placeOrderUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.getOrderHistoryUseCase = getOrderHistoryUseCase;
        this.getRestaurantOrdersUseCase = getRestaurantOrdersUseCase;
        this.getActiveRestaurantOrdersUseCase = getActiveRestaurantOrdersUseCase;
        this.advanceOrderStatusUseCase = advanceOrderStatusUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.completeDeliveryUseCase = completeDeliveryUseCase;
        this.restaurantRegistrationUseCase = restaurantRegistrationUseCase;
        this.restaurantQueryService = restaurantQueryService;
        this.restaurantManagementService = restaurantManagementService;
        this.menuQueryService = menuQueryService;
        this.menuManagementService = menuManagementService;
        this.couponCommandService = couponCommandService;
        this.couponQueryService = couponQueryService;
        this.couponValidationUseCase = couponValidationUseCase;
        this.getPaymentForOrderUseCase = getPaymentForOrderUseCase;
        this.findRiderByIdUseCase = findRiderByIdUseCase;
    }

    private static AppContext build() {
        UserRepository userRepository = com.fooddelivery.repository.UserRepository.getInstance();
        RestaurantRepository restaurantRepository = com.fooddelivery.repository.RestaurantRepository.getInstance();
        MenuItemRepository menuItemRepository = com.fooddelivery.repository.MenuItemRepository.getInstance();
        OrderRepository orderRepository = com.fooddelivery.repository.OrderRepository.getInstance();
        RiderRepository riderRepository = com.fooddelivery.repository.RiderRepository.getInstance();
        CouponRepository couponRepository = com.fooddelivery.repository.CouponRepository.getInstance();
        PaymentRepository paymentRepository = com.fooddelivery.repository.PaymentRepository.getInstance();
        CartRepository cartRepository = new InMemoryCartRepository();

        EmailValidator emailValidator = new RegexEmailValidator();
        PasswordHasher passwordHasher = new Sha256PasswordHasher();
        IdGenerator idGenerator = new UuidIdGenerator();
        CurrentSession currentSession = new InMemoryCurrentSession();

        DeliveryFeeCalculator deliveryFeeCalculator = new DefaultDeliveryFeeCalculator();
        RiderAssigner riderAssigner = new RepositoryRiderAssigner(riderRepository, orderRepository);
        OrderPaymentProcessor orderPaymentProcessor = new LegacyOrderPaymentProcessor();
        OrderStatusPolicy orderStatusPolicy = new DefaultOrderStatusPolicy();

        RegisterUserUseCase registerUserUseCase = new RegisterUserUseCase(
                userRepository, emailValidator, passwordHasher, idGenerator
        );
        LoginUseCase loginUseCase = new LoginUseCase(
                userRepository, passwordHasher, currentSession
        );
        LogoutUseCase logoutUseCase = new LogoutUseCase(currentSession);
        GetCurrentUserUseCase getCurrentUserUseCase = new GetCurrentUserUseCase(currentSession);

        GetCartUseCase getCartUseCase = new GetCartUseCase(cartRepository, currentSession);
        AddCartItemUseCase addCartItemUseCase = new AddCartItemUseCase(cartRepository, currentSession);
        RemoveCartItemUseCase removeCartItemUseCase = new RemoveCartItemUseCase(cartRepository, currentSession);
        UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase =
                new UpdateCartItemQuantityUseCase(cartRepository, currentSession);
        ClearCartUseCase clearCartUseCase = new ClearCartUseCase(cartRepository, currentSession);

        PlaceOrderUseCase placeOrderUseCase = new PlaceOrderUseCase(
                cartRepository,
                orderRepository,
                menuItemRepository,
                couponRepository,
                idGenerator,
                deliveryFeeCalculator,
                orderPaymentProcessor,
                riderAssigner
        );

        GetOrderByIdUseCase getOrderByIdUseCase = new GetOrderByIdUseCase(orderRepository);
        GetOrderHistoryUseCase getOrderHistoryUseCase = new GetOrderHistoryUseCase(orderRepository);
        GetRestaurantOrdersUseCase getRestaurantOrdersUseCase = new GetRestaurantOrdersUseCase(orderRepository);
        GetActiveRestaurantOrdersUseCase getActiveRestaurantOrdersUseCase =
                new GetActiveRestaurantOrdersUseCase(orderRepository);
        AdvanceOrderStatusUseCase advanceOrderStatusUseCase =
                new AdvanceOrderStatusUseCase(orderRepository, orderStatusPolicy);
        CancelOrderUseCase cancelOrderUseCase =
                new CancelOrderUseCase(orderRepository, orderStatusPolicy);
        CompleteDeliveryUseCase completeDeliveryUseCase =
                new CompleteDeliveryUseCase(
                        orderRepository,
                        riderRepository,
                        orderStatusPolicy,
                        orderPaymentProcessor
                );

        RestaurantRegistrationUseCase restaurantRegistrationUseCase =
                new RestaurantRegistrationUseCase(restaurantRepository, idGenerator);
        RestaurantQueryService restaurantQueryService =
                new RestaurantQueryService(restaurantRepository);
        RestaurantManagementService restaurantManagementService =
                new RestaurantManagementService(restaurantRepository);

        MenuQueryService menuQueryService =
                new MenuQueryService(menuItemRepository);
        MenuManagementService menuManagementService =
                new MenuManagementService(menuItemRepository, idGenerator);

        CouponCommandService couponCommandService =
                new CouponCommandService(couponRepository, idGenerator);
        CouponQueryService couponQueryService =
                new CouponQueryService(couponRepository);
        CouponValidationUseCase couponValidationUseCase =
                new CouponValidationUseCase(couponRepository);

        GetPaymentForOrderUseCase getPaymentForOrderUseCase =
                new GetPaymentForOrderUseCase(paymentRepository);
        FindRiderByIdUseCase findRiderByIdUseCase =
                new FindRiderByIdUseCase(riderRepository);

        return new AppContext(
                userRepository,
                restaurantRepository,
                menuItemRepository,
                orderRepository,
                riderRepository,
                couponRepository,
                paymentRepository,
                cartRepository,
                emailValidator,
                passwordHasher,
                idGenerator,
                currentSession,
                deliveryFeeCalculator,
                riderAssigner,
                orderPaymentProcessor,
                orderStatusPolicy,
                registerUserUseCase,
                loginUseCase,
                logoutUseCase,
                getCurrentUserUseCase,
                getCartUseCase,
                addCartItemUseCase,
                removeCartItemUseCase,
                updateCartItemQuantityUseCase,
                clearCartUseCase,
                placeOrderUseCase,
                getOrderByIdUseCase,
                getOrderHistoryUseCase,
                getRestaurantOrdersUseCase,
                getActiveRestaurantOrdersUseCase,
                advanceOrderStatusUseCase,
                cancelOrderUseCase,
                completeDeliveryUseCase,
                restaurantRegistrationUseCase,
                restaurantQueryService,
                restaurantManagementService,
                menuQueryService,
                menuManagementService,
                couponCommandService,
                couponQueryService,
                couponValidationUseCase,
                getPaymentForOrderUseCase,
                findRiderByIdUseCase
        );
    }

    public static AppContext create() {
        return INSTANCE;
    }

    public UserRepository userRepository() { return userRepository; }
    public RestaurantRepository restaurantRepository() { return restaurantRepository; }
    public MenuItemRepository menuItemRepository() { return menuItemRepository; }
    public OrderRepository orderRepository() { return orderRepository; }
    public RiderRepository riderRepository() { return riderRepository; }
    public CouponRepository couponRepository() { return couponRepository; }
    public PaymentRepository paymentRepository() { return paymentRepository; }
    public CartRepository cartRepository() { return cartRepository; }

    public EmailValidator emailValidator() { return emailValidator; }
    public PasswordHasher passwordHasher() { return passwordHasher; }
    public IdGenerator idGenerator() { return idGenerator; }
    public CurrentSession currentSession() { return currentSession; }

    public DeliveryFeeCalculator deliveryFeeCalculator() { return deliveryFeeCalculator; }
    public RiderAssigner riderAssigner() { return riderAssigner; }
    public OrderPaymentProcessor orderPaymentProcessor() { return orderPaymentProcessor; }
    public OrderStatusPolicy orderStatusPolicy() { return orderStatusPolicy; }

    public RegisterUserUseCase registerUserUseCase() { return registerUserUseCase; }
    public LoginUseCase loginUseCase() { return loginUseCase; }
    public LogoutUseCase logoutUseCase() { return logoutUseCase; }
    public GetCurrentUserUseCase getCurrentUserUseCase() { return getCurrentUserUseCase; }

    public GetCartUseCase getCartUseCase() { return getCartUseCase; }
    public AddCartItemUseCase addCartItemUseCase() { return addCartItemUseCase; }
    public RemoveCartItemUseCase removeCartItemUseCase() { return removeCartItemUseCase; }
    public UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase() { return updateCartItemQuantityUseCase; }
    public ClearCartUseCase clearCartUseCase() { return clearCartUseCase; }

    public PlaceOrderUseCase placeOrderUseCase() { return placeOrderUseCase; }
    public GetOrderByIdUseCase getOrderByIdUseCase() { return getOrderByIdUseCase; }
    public GetOrderHistoryUseCase getOrderHistoryUseCase() { return getOrderHistoryUseCase; }
    public GetRestaurantOrdersUseCase getRestaurantOrdersUseCase() { return getRestaurantOrdersUseCase; }
    public GetActiveRestaurantOrdersUseCase getActiveRestaurantOrdersUseCase() { return getActiveRestaurantOrdersUseCase; }
    public AdvanceOrderStatusUseCase advanceOrderStatusUseCase() { return advanceOrderStatusUseCase; }
    public CancelOrderUseCase cancelOrderUseCase() { return cancelOrderUseCase; }
    public CompleteDeliveryUseCase completeDeliveryUseCase() { return completeDeliveryUseCase; }

    public RestaurantRegistrationUseCase restaurantRegistrationUseCase() { return restaurantRegistrationUseCase; }
    public RestaurantQueryService restaurantQueryService() { return restaurantQueryService; }
    public RestaurantManagementService restaurantManagementService() { return restaurantManagementService; }

    public MenuQueryService menuQueryService() { return menuQueryService; }
    public MenuManagementService menuManagementService() { return menuManagementService; }

    public CouponCommandService couponCommandService() { return couponCommandService; }
    public CouponQueryService couponQueryService() { return couponQueryService; }
    public CouponValidationUseCase couponValidationUseCase() { return couponValidationUseCase; }

    public GetPaymentForOrderUseCase getPaymentForOrderUseCase() { return getPaymentForOrderUseCase; }
    public FindRiderByIdUseCase findRiderByIdUseCase() { return findRiderByIdUseCase; }
}