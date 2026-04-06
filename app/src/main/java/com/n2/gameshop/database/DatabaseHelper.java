package com.n2.gameshop.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "game_shop.db";
	public static final int DATABASE_VERSION = 4;

	private static final String IMG_GTA_V = "https://picsum.photos/seed/gta-v/800/500";
	private static final String IMG_DMC_5 = "https://picsum.photos/seed/dmc5/800/500";
	private static final String IMG_ELDEN_RING = "https://picsum.photos/seed/elden-ring/800/500";
	private static final String IMG_FF_XVI = "https://picsum.photos/seed/final-fantasy-xvi/800/500";
	private static final String IMG_FIFA_24 = "https://picsum.photos/seed/fifa-24/800/500";
	private static final String IMG_NBA_2K24 = "https://picsum.photos/seed/nba-2k24/800/500";
	private static final String IMG_CIV_6 = "https://picsum.photos/seed/civilization-vi/800/500";
	private static final String IMG_AOE_4 = "https://picsum.photos/seed/aoe-iv/800/500";
	private static final String IMG_ZELDA = "https://picsum.photos/seed/zelda/800/500";
	private static final String IMG_COD_MOBILE = "https://picsum.photos/seed/cod-mobile/800/500";

	// ===== USERS =====
	public static final String TABLE_USERS = "users";
	public static final String COLUMN_USER_ID = "id";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_FULL_NAME = "full_name";
	public static final String COLUMN_PHONE = "phone";
	public static final String COLUMN_ROLE = "role";
	public static final String COLUMN_IS_ACTIVE = "is_active";

	// ===== CATEGORIES =====
	public static final String TABLE_CATEGORIES = "categories";
	public static final String COLUMN_CATEGORY_ID = "id";
	public static final String COLUMN_CATEGORY_NAME = "name";
	public static final String COLUMN_CATEGORY_DESC = "description";
	public static final String COLUMN_CATEGORY_ICON = "icon_url";

	// ===== PRODUCTS =====
	public static final String TABLE_PRODUCTS = "products";
	public static final String COLUMN_PRODUCT_ID = "id";
	public static final String COLUMN_PRODUCT_CATEGORY_ID = "category_id";
	public static final String COLUMN_PRODUCT_NAME = "name";
	public static final String COLUMN_PRODUCT_DESC = "description";
	public static final String COLUMN_PRODUCT_PRICE = "price";
	public static final String COLUMN_PRODUCT_IMAGE = "image_url";
	public static final String COLUMN_PRODUCT_STOCK = "stock";
	public static final String COLUMN_PRODUCT_PLATFORM = "platform";
	public static final String COLUMN_PRODUCT_IS_ACTIVE = "is_active";

	// ===== REVIEWS =====
	public static final String TABLE_REVIEWS = "reviews";
	public static final String COLUMN_REVIEW_ID = "id";
	public static final String COLUMN_REVIEW_USER_ID = "user_id";
	public static final String COLUMN_REVIEW_PRODUCT_ID = "product_id";
	public static final String COLUMN_REVIEW_RATING = "rating";
	public static final String COLUMN_REVIEW_COMMENT = "comment";
	public static final String COLUMN_REVIEW_CREATED_AT = "created_at";

	// ===== ORDERS =====
	public static final String TABLE_ORDERS = "orders";
	public static final String COLUMN_ORDER_ID = "id";
	public static final String COLUMN_ORDER_USER_ID = "user_id";
	public static final String COLUMN_ORDER_TOTAL = "total_amount";
	public static final String COLUMN_ORDER_PAYMENT = "payment_method";
	public static final String COLUMN_ORDER_STATUS = "status";
	public static final String COLUMN_ORDER_NOTE = "note";
	public static final String COLUMN_ORDER_CREATED_AT = "created_at";

	// ===== ORDER DETAILS =====
	public static final String TABLE_ORDER_DETAILS = "order_details";
	public static final String COLUMN_OD_ID = "id";
	public static final String COLUMN_OD_ORDER_ID = "order_id";
	public static final String COLUMN_OD_PRODUCT_ID = "product_id";
	public static final String COLUMN_OD_QUANTITY = "quantity";
	public static final String COLUMN_OD_UNIT_PRICE = "unit_price";

	private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
			+ COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, "
			+ COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, "
			+ COLUMN_PASSWORD + " TEXT NOT NULL, "
			+ COLUMN_FULL_NAME + " TEXT, "
			+ COLUMN_PHONE + " TEXT, "
			+ COLUMN_ROLE + " TEXT NOT NULL DEFAULT 'user', "
			+ COLUMN_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1"
			+ ")";

	private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + " ("
			+ COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_CATEGORY_NAME + " TEXT NOT NULL UNIQUE, "
			+ COLUMN_CATEGORY_DESC + " TEXT, "
			+ COLUMN_CATEGORY_ICON + " TEXT"
			+ ")";

	private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " ("
			+ COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_PRODUCT_CATEGORY_ID + " INTEGER NOT NULL, "
			+ COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
			+ COLUMN_PRODUCT_DESC + " TEXT, "
			+ COLUMN_PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0, "
			+ COLUMN_PRODUCT_IMAGE + " TEXT, "
			+ COLUMN_PRODUCT_STOCK + " INTEGER NOT NULL DEFAULT 0, "
			+ COLUMN_PRODUCT_PLATFORM + " TEXT NOT NULL DEFAULT 'PC', "
			+ COLUMN_PRODUCT_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1, "
			+ "FOREIGN KEY(" + COLUMN_PRODUCT_CATEGORY_ID + ") REFERENCES "
			+ TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ")"
			+ ")";

	private static final String CREATE_TABLE_REVIEWS = "CREATE TABLE " + TABLE_REVIEWS + " ("
			+ COLUMN_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_REVIEW_USER_ID + " INTEGER NOT NULL, "
			+ COLUMN_REVIEW_PRODUCT_ID + " INTEGER NOT NULL, "
			+ COLUMN_REVIEW_RATING + " INTEGER NOT NULL CHECK(" + COLUMN_REVIEW_RATING + " BETWEEN 1 AND 5), "
			+ COLUMN_REVIEW_COMMENT + " TEXT, "
			+ COLUMN_REVIEW_CREATED_AT + " TEXT NOT NULL, "
			+ "FOREIGN KEY(" + COLUMN_REVIEW_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), "
			+ "FOREIGN KEY(" + COLUMN_REVIEW_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "), "
			+ "UNIQUE(" + COLUMN_REVIEW_USER_ID + ", " + COLUMN_REVIEW_PRODUCT_ID + ")"
			+ ")";

	private static final String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + " ("
			+ COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_ORDER_USER_ID + " INTEGER NOT NULL, "
			+ COLUMN_ORDER_TOTAL + " REAL NOT NULL, "
			+ COLUMN_ORDER_PAYMENT + " TEXT NOT NULL, "
			+ COLUMN_ORDER_STATUS + " TEXT NOT NULL DEFAULT 'pending', "
			+ COLUMN_ORDER_NOTE + " TEXT, "
			+ COLUMN_ORDER_CREATED_AT + " TEXT NOT NULL, "
			+ "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
			+ ")";

	private static final String CREATE_TABLE_ORDER_DETAILS = "CREATE TABLE " + TABLE_ORDER_DETAILS + " ("
			+ COLUMN_OD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_OD_ORDER_ID + " INTEGER NOT NULL, "
			+ COLUMN_OD_PRODUCT_ID + " INTEGER NOT NULL, "
			+ COLUMN_OD_QUANTITY + " INTEGER NOT NULL, "
			+ COLUMN_OD_UNIT_PRICE + " REAL NOT NULL, "
			+ "FOREIGN KEY(" + COLUMN_OD_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), "
			+ "FOREIGN KEY(" + COLUMN_OD_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")"
			+ ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_USERS);
		db.execSQL(CREATE_TABLE_CATEGORIES);
		db.execSQL(CREATE_TABLE_PRODUCTS);
		db.execSQL(CREATE_TABLE_REVIEWS);
		db.execSQL(CREATE_TABLE_ORDERS);
		db.execSQL(CREATE_TABLE_ORDER_DETAILS);
		seedDefaultAdmin(db);
		seedDefaultCategories(db);
		seedSampleProducts(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAILS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		db.execSQL("PRAGMA foreign_keys=ON");
		seedDefaultAdmin(db);
		seedSampleProductImages(db);
	}

	private void seedDefaultAdmin(SQLiteDatabase db) {
		db.execSQL("INSERT OR IGNORE INTO " + TABLE_USERS + " ("
				+ COLUMN_USERNAME + ", "
				+ COLUMN_EMAIL + ", "
				+ COLUMN_PASSWORD + ", "
				+ COLUMN_FULL_NAME + ", "
				+ COLUMN_PHONE + ", "
				+ COLUMN_ROLE + ", "
				+ COLUMN_IS_ACTIVE
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?)", new Object[]{
				"admin",
				"admin@gameshop.local",
				"admin123",
				"Administrator",
				"",
				"admin",
				1
		});
	}

	private void seedDefaultCategories(SQLiteDatabase db) {
		String sql = "INSERT OR IGNORE INTO " + TABLE_CATEGORIES + " ("
				+ COLUMN_CATEGORY_NAME + ", " + COLUMN_CATEGORY_DESC + ", " + COLUMN_CATEGORY_ICON
				+ ") VALUES (?, ?, ?)";
		db.execSQL(sql, new Object[]{"Action", "Game hành động", ""});
		db.execSQL(sql, new Object[]{"RPG", "Nhập vai", ""});
		db.execSQL(sql, new Object[]{"Sports", "Thể thao", ""});
		db.execSQL(sql, new Object[]{"Strategy", "Chiến thuật", ""});
		db.execSQL(sql, new Object[]{"Adventure", "Phiêu lưu", ""});
	}

	private void seedSampleProducts(SQLiteDatabase db) {
		String sql = "INSERT OR IGNORE INTO " + TABLE_PRODUCTS + " ("
				+ COLUMN_PRODUCT_CATEGORY_ID + ", "
				+ COLUMN_PRODUCT_NAME + ", "
				+ COLUMN_PRODUCT_DESC + ", "
				+ COLUMN_PRODUCT_PRICE + ", "
				+ COLUMN_PRODUCT_IMAGE + ", "
				+ COLUMN_PRODUCT_STOCK + ", "
				+ COLUMN_PRODUCT_PLATFORM + ", "
				+ COLUMN_PRODUCT_IS_ACTIVE
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		db.execSQL(sql, new Object[]{1, "GTA V", "Grand Theft Auto V - Open world action game", 299000, IMG_GTA_V, 100, "PC", 1});
		db.execSQL(sql, new Object[]{1, "Devil May Cry 5", "Hack and slash action game", 450000, IMG_DMC_5, 50, "PC", 1});
		db.execSQL(sql, new Object[]{2, "Elden Ring", "Open world action RPG by FromSoftware", 890000, IMG_ELDEN_RING, 80, "PC", 1});
		db.execSQL(sql, new Object[]{2, "Final Fantasy XVI", "Action RPG by Square Enix", 1200000, IMG_FF_XVI, 40, "Console", 1});
		db.execSQL(sql, new Object[]{3, "FIFA 24", "Football simulation game", 650000, IMG_FIFA_24, 200, "Console", 1});
		db.execSQL(sql, new Object[]{3, "NBA 2K24", "Basketball simulation game", 550000, IMG_NBA_2K24, 120, "PC", 1});
		db.execSQL(sql, new Object[]{4, "Civilization VI", "Turn-based strategy game", 350000, IMG_CIV_6, 90, "PC", 1});
		db.execSQL(sql, new Object[]{4, "Age of Empires IV", "Real-time strategy game", 500000, IMG_AOE_4, 70, "PC", 1});
		db.execSQL(sql, new Object[]{5, "The Legend of Zelda", "Action-adventure game by Nintendo", 1100000, IMG_ZELDA, 60, "Console", 1});
		db.execSQL(sql, new Object[]{1, "Call of Duty Mobile", "FPS game for mobile", 0, IMG_COD_MOBILE, 999, "Mobile", 1});
	}

	private void seedSampleProductImages(SQLiteDatabase db) {
		// Backfill old rows that were seeded with empty image_url.
		updateImageIfMissing(db, "GTA V", IMG_GTA_V);
		updateImageIfMissing(db, "Devil May Cry 5", IMG_DMC_5);
		updateImageIfMissing(db, "Elden Ring", IMG_ELDEN_RING);
		updateImageIfMissing(db, "Final Fantasy XVI", IMG_FF_XVI);
		updateImageIfMissing(db, "FIFA 24", IMG_FIFA_24);
		updateImageIfMissing(db, "NBA 2K24", IMG_NBA_2K24);
		updateImageIfMissing(db, "Civilization VI", IMG_CIV_6);
		updateImageIfMissing(db, "Age of Empires IV", IMG_AOE_4);
		updateImageIfMissing(db, "The Legend of Zelda", IMG_ZELDA);
		updateImageIfMissing(db, "Call of Duty Mobile", IMG_COD_MOBILE);
	}

	private void updateImageIfMissing(SQLiteDatabase db, String productName, String imageUrl) {
		db.execSQL("UPDATE " + TABLE_PRODUCTS
				+ " SET " + COLUMN_PRODUCT_IMAGE + " = ?"
				+ " WHERE " + COLUMN_PRODUCT_NAME + " = ?"
				+ " AND (" + COLUMN_PRODUCT_IMAGE + " IS NULL OR TRIM(" + COLUMN_PRODUCT_IMAGE + ") = '')",
				new Object[]{imageUrl, productName});
	}
}
