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
import com.fooddelivery.domain.repository.CartRepository;
import com.fooddelivery.domain.repository.CouponRepository;
import com.fooddelivery.domain.repository.MenuItemRepository;
import com.fooddelivery.domain.repository.OrderRepository;
import com.fooddelivery.domain.repository.PaymentRepository;
import com.fooddelivery.domain.repository.RestaurantRepository;
import com.fooddelivery.domain.repository.RiderRepository;
import com.fooddelivery.domain.repository.UserRepository;
import com.fooddelivery.infrastructure.repository.memory.InMemoryCartRepository;
import com.fooddelivery.infrastructure.security.RegexEmailValidator;
import com.fooddelivery.infrastructure.security.Sha256PasswordHasher;
import com.fooddelivery.infrastructure.session.CurrentSession;
import com.fooddelivery.infrastructure.session.InMemoryCurrentSession;
import com.fooddelivery.infrastructure.util.UuidIdGenerator;

/**
 * Central dependency container for the application.
 */
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

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    private final GetCartUseCase getCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final ClearCartUseCase clearCartUseCase;

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
                       RegisterUserUseCase registerUserUseCase,
                       LoginUseCase loginUseCase,
                       LogoutUseCase logoutUseCase,
                       GetCurrentUserUseCase getCurrentUserUseCase,
                       GetCartUseCase getCartUseCase,
                       AddCartItemUseCase addCartItemUseCase,
                       RemoveCartItemUseCase removeCartItemUseCase,
                       UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase,
                       ClearCartUseCase clearCartUseCase) {
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
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.getCartUseCase = getCartUseCase;
        this.addCartItemUseCase = addCartItemUseCase;
        this.removeCartItemUseCase = removeCartItemUseCase;
        this.updateCartItemQuantityUseCase = updateCartItemQuantityUseCase;
        this.clearCartUseCase = clearCartUseCase;
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
                registerUserUseCase,
                loginUseCase,
                logoutUseCase,
                getCurrentUserUseCase,
                getCartUseCase,
                addCartItemUseCase,
                removeCartItemUseCase,
                updateCartItemQuantityUseCase,
                clearCartUseCase
        );
    }

    public static AppContext create() {
        return INSTANCE;
    }

    public UserRepository userRepository() {
        return userRepository;
    }

    public RestaurantRepository restaurantRepository() {
        return restaurantRepository;
    }

    public MenuItemRepository menuItemRepository() {
        return menuItemRepository;
    }

    public OrderRepository orderRepository() {
        return orderRepository;
    }

    public RiderRepository riderRepository() {
        return riderRepository;
    }

    public CouponRepository couponRepository() {
        return couponRepository;
    }

    public PaymentRepository paymentRepository() {
        return paymentRepository;
    }

    public CartRepository cartRepository() {
        return cartRepository;
    }

    public EmailValidator emailValidator() {
        return emailValidator;
    }

    public PasswordHasher passwordHasher() {
        return passwordHasher;
    }

    public IdGenerator idGenerator() {
        return idGenerator;
    }

    public CurrentSession currentSession() {
        return currentSession;
    }

    public RegisterUserUseCase registerUserUseCase() {
        return registerUserUseCase;
    }

    public LoginUseCase loginUseCase() {
        return loginUseCase;
    }

    public LogoutUseCase logoutUseCase() {
        return logoutUseCase;
    }

    public GetCurrentUserUseCase getCurrentUserUseCase() {
        return getCurrentUserUseCase;
    }

    public GetCartUseCase getCartUseCase() {
        return getCartUseCase;
    }

    public AddCartItemUseCase addCartItemUseCase() {
        return addCartItemUseCase;
    }

    public RemoveCartItemUseCase removeCartItemUseCase() {
        return removeCartItemUseCase;
    }

    public UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase() {
        return updateCartItemQuantityUseCase;
    }

    public ClearCartUseCase clearCartUseCase() {
        return clearCartUseCase;
    }
}