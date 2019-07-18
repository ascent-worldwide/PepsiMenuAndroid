package com.clairvoyant.naijamenu.utils;

public class Constants {
    // Google Console APIs developer key
    public static final String DEVELOPER_KEY = "AIzaSyAkfE9t7lq1DArsNvE-wGbFsBkjuURcO0g";
    // YouTube video id
    public static final String YOUTUBE_VIDEO_CODE = "_oEA18Y8gM0";
    // Website URL
    public static final String API_ENDPOINT = "http://ec2-18-220-213-138.us-east-2.compute.amazonaws.com/pepsi-menu/api/";

    // Local API EndPoint
    //public static final String API_ENDPOINT = "http://182.18.160.160/pepsi-menu/api/";

    // Temp API EndPoint
    //public static final String API_ENDPOINT = "http://ec2-52-66-104-61.ap-south-1.compute.amazonaws.com/uat/pepsiUAT/api/";

    // live API EndPoint  ---> Initial URl
    //public static final String API_ENDPOINT = "http://naijamenuapp.com/api/";   /// http://ec2-18-220-213-138.us-east-2.compute.amazonaws.com
    public static final String LOGIN_API = API_ENDPOINT + "User/login";
    public static final String PROMOTIONAL_BANNER_API = API_ENDPOINT + "Restaurant/GetPromotionalBanner";
    public static final String CATEGORY_API = API_ENDPOINT + "Menu/getCategories";
    public static final String PRODUCT_BY_CATEGORY_ID_API = API_ENDPOINT + "Menu/getProductsByCategoryId";
    public static final String RATE_RECIPE_API = API_ENDPOINT + "rating/RateRecipe";
    public static final String RATE_RESTAURANT_API = API_ENDPOINT + "rating/RateRestaurant";
    public static final String SURVEY_QUESTIONS_API = API_ENDPOINT + "rating/GetSurveyQuestions";
    public static final String QUIZ_QUESTIONS_API = API_ENDPOINT + "contest/getQuizQuestions";
    public static final String WINNER_DETAIL_API = API_ENDPOINT + "contest/sendWinnerDetail";
    public static final String QUIZ_PROMOTION_API = API_ENDPOINT + "contest/GetQuizPromo";
    public static final String GET_BRAND_PROMOTION = API_ENDPOINT + "brand/GetBrandPromotion";
    public static final String PASSWORD_CONFIRMATION_API = API_ENDPOINT + "User/confirmPassword";
    public static final String GET_ALL_MENU = API_ENDPOINT + "Menu/getMenuByRestaurantId";
    public static final String APP_VERSION_API = API_ENDPOINT + "User/getAppVersion";
    public static final String LOGGED = "loggedin";
    public static final String DEVICE_TYPE = "Android";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String BRAND_VIDEOS_URL_VERSION = "brandVideoUrlVersion";
    public static final String VIDEO_URL = "videoURL";
    public static final String VIDEO_FILE_NAME = "fileName";
    public static final String SENDER_ID = "926002498330";
    public static final String GCM_ID = "gcm_id";
    public static final String RESTAURANT_NAME = "restaurant_name";
    public static final String RESTAURANT_LOGO = "restaurant_logo";
    public static final String RESTAURANT_LOGO_WITH_TEXT = "restaurant_logo_with_text";
    public static final String HOME_CATEGORIES = "home_categories";
    public static final String PASSWORD = "password";
    public static final String PROMO_BANNER = "promo_banner";
    public static final String RESTAURANT_BACKGROUND_IMG_LANDSCAPE = "restaurant_background_img_land";
    public static final String RESTAURANT_BACKGROUND_IMG_PORTRAIT = "restaurant_background_img_por";
    public static final String RESTAURANT_HOMESCREEN_IMG = "restaurant_homescreen_img";
    public static final String IS_PROMO_BANNER_AVAILABLE = "is_promo_banner_available";
    public static final String PRODUCT_BY_CATEGORY_ID = "product_by_category_id";
    public static final String MINUTE = " Minutes";
    public static final String RESTAURANT_THEME = "restaurant_theme";
    public static final String ORIENTATION = "orientation";
    public static final String IS_VIDEO_DOWNLOADED_STR = "isVideoDownloaded";
    public static final String MENU_VERSION = "menu_version";
    // intent params
    public static final String UPDATED_MENU_VERSION = "updatedMenuVersion";
    public static boolean IS_VIDEO_DOWNLOADED = false;
    public static int VOLLEY_TIMEOUT = 30000;
    public static int VOLLEY_MAX_RETRIES = 1;
    public static float VOLLEY_BACKUP_MULT = 2;
}