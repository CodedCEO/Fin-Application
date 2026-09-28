package com.finapp.account.constants;

public final class CardsConstants {
    private CardsConstants() {
        // restrict instantiation
    }

    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";
    public static final String STATUS_BLOCKED = "Blocked";
    public static final String STATUS_EXPIRED = "Expired";
    public static final String STATUS_PENDING_ACTIVATION = "Pending Activation";
    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201 = "Card created successfully";
    public static final String MESSAGE_202 = "Card record retrieved successfully";
    public static final String MESSAGE_203 = "Card records retrieved successfully";

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final int MAX_PAGE_SIZE = 100;


    public static final String STATUS_200 = "200";
    public static final String MESSAGE_301 = "Card activated successfully";
    public static final String MESSAGE_302 = "Card deactivated successfully";
    public static final String MESSAGE_303 = "Card reactivated successfully";
    public static final String MESSAGE_304 = "Card deleted successfully";



    public static final String STATUS_417 = "417";
    public static final String MESSAGE_417_ACTIVATION = "Deactivation operation failed. Please try again or contact Customer Service";
    // public static final String  STATUS_500 = "500";
    // public static final String  MESSAGE_500 = "An error occurred. Please try again or contact Dev team";

}
