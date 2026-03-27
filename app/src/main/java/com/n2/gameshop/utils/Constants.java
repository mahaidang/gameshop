package com.n2.gameshop.utils;

public final class Constants {
    private Constants() {
    }

    // Order statuses
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_SHIPPING = "shipping";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";

    public static final String[] ORDER_STATUSES = {
            STATUS_PENDING, STATUS_CONFIRMED, STATUS_SHIPPING, STATUS_DELIVERED, STATUS_CANCELLED
    };

    // Roles
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";

    // Platforms
    public static final String[] PLATFORMS = {"PC", "Console", "Mobile"};

    // Order filter indices
    public static final int FILTER_ALL = 0;
    public static final int FILTER_TODAY = 1;
    public static final int FILTER_THIS_WEEK = 2;
    public static final int FILTER_THIS_MONTH = 3;
    public static final int FILTER_THIS_YEAR = 4;
}

