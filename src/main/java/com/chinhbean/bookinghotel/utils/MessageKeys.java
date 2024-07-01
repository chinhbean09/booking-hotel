package com.chinhbean.bookinghotel.utils;

public class MessageKeys {


    public static final String USERNAME_ALREADY_EXISTS = "user.register.user_already_exists";

    public static final String PHONE_NUMBER_ALREADY_EXISTS = "user.login.register.phone-number_already_exists";

    public static final String LOGIN_SUCCESSFULLY = "user.login.login_successfully";
    public static final String REGISTER_SUCCESSFULLY = "user.login.register_successfully";
    public static final String REGISTER_FAILED = "user.login.register_failed";
    public static final String LOGIN_FAILED = "user.login.login_failed";
    public static final String PASSWORD_NOT_MATCH = "user.register.password_not_match";
    public static final String USER_IS_LOCKED = "user.login.user_is_locked";
    public static final String USER_NOT_FOUND = "user.get_user.user_not_found";
    public static final String OLD_PASSWORD_WRONG = "user.change_password.old_password_wrong";
    public static final String CONFIRM_PASSWORD_NOT_MATCH = "user.confirm_password_not_match";
    public static final String CHANGE_PASSWORD_SUCCESSFULLY = "user.change_password.change_password_successfully";
    public static final String CHANGE_PASSWORD_FAILED = "user.change_password.change_password_failed";
    public static final String OTP_SENT_SUCCESSFULLY = "forgot_password.otp_sent_successfully";
    public static final String OTP_IS_EXPIRED = "forgot_password.otp_is_expired";
    public static final String OTP_NOT_FOUND = "forgot_password.otp_not_found";
    public static final String OTP_VERIFIED_SUCCESSFULLY = "forgot_password.otp_verified_successfully";
    public static final String OTP_INCORRECT = "forgot_password.opt_incorrect";

    public static final String DELETE_HOTEL_SUCCESSFULLY = "hotel.delete_hotel.delete_successfully";
    public static final String INSERT_HOTEL_FAILED = "hotel.create_hotel.create_failed";
    public static final String DELETE_HOTEL_FAILED = "hotel.delete_hotel.delete_failed";
    public static final String UPDATE_HOTEL_FAILED = "hotel.update_hotel.update_failed";
    public static final String DELETE_ORDER_DETAIL_SUCCESSFULLY = "order.delete_order_detail.delete_successfully";
    public static final String UPLOAD_IMAGES_MAX_5 = "product.upload_images.error_max_5_images";
    public static final String UPLOAD_IMAGES_FILE_LARGE = "product.upload_images.file_large";

    public static final String UPDATE_AVATAR_SUCCESSFULLY = "user.update_avatar.update_avatar_successfully";
    public static final String UPLOAD_IMAGES_FILE_MUST_BE_IMAGE = "product.upload_images.file_must_be_image";
    public static final String INSERT_CATEGORY_FAILED = "category.create_category.create_failed";
    public static final String WRONG_PHONE_PASSWORD = "user.login.wrong_phone_password";
    public static final String ROLE_DOES_NOT_EXISTS = "user.login.role_not_exist";
    public static final String USER_DOES_NOT_EXISTS = "user.get_user.user_not_exist";
    public static final String ENABLE_USER_SUCCESSFULLY = "user.enable_user.enable_successfully";
    public static final String BLOCK_USER_SUCCESSFULLY = "user.enable_user.disable_successfully";

    public static final String TOKEN_IS_EXPIRED = "user.jwt.token_is_expired";
    public static final String TOKEN_GENERATION_FAILED = "user.login.jwt.token_can_not_create";

    public static final String UPDATE_HOTEL_STATUS_SUCCESSFULLY = "hotel.update_status.update_hotel_status_successfully";
    public static final String USER_CANNOT_CHANGE_STATUS_TO = "update_status.user_cannot_change_status_to";
    public static final String INSERT_HOTEL_SUCCESSFULLY = "hotel.create_hotel.create_successfully";
    public static final String UPDATE_HOTEL_SUCCESSFULLY = "hotel.update_hotel.update_successfully";
    public static final String HOTEL_DOES_NOT_EXISTS = "hotel.list_hotel.hotel_not_exist";
    public static final String RETRIEVED_ALL_HOTELS_SUCCESSFULLY = "hotel.list_hotel.retrieved_all_hotels_successfully";
    public static final String RETRIEVED_HOTEL_DETAILS_SUCCESSFULLY = "hotel.hotel_detail.retrieved_hotel_details_successfully";
    public static final String USER_DOES_NOT_HAVE_PERMISSION_TO_CHANGE_STATUS = "hotel.update_status.user_does_not_have_permission_to_change_status";
    public static final String ROOM_DOES_NOT_EXISTS = "room.list_room.room_not_exist";
    public static final String UPDATED_ROOM_STATUS_SUCCESSFULLY = "room.update_status.update_room_status_successfully";

    public static final String TOKEN_GENERATION_FAILEDSTRING = "user.login.jwt.token_can_not_create";
    public static final String INSERT_ROOM_SUCCESSFULLY = "room.create_room.create_successfully";

    public static final String NO_ROOMS_FOUND = "room.list_room.no_rooms_found";

    public static final String RETRIEVED_ROOMS_SUCCESSFULLY = "room.list_room.retrieved_rooms_successfully";

    public static final String UPDATE_ROOM_SUCCESSFULLY = "room.update_room.update_successfully";

    public static final String DELETE_ROOM_SUCCESSFULLY = "room.delete_room.delete_successfully";

    public static final String ROOM_NUMBER_ALREADY_EXISTS = "room.create_room.room_number_already_exists";
    public static final String UPLOAD_IMAGES_SUCCESSFULLY = "upload_images.upload_images_successfully";
    public static final String UPDATED_IMAGES_SUCCESSFULLY = "upload_images.updated_images_successfully";
    public static final String INSERT_ROOM_TYPE_SUCCESSFULLY = "room_type.create_room_type.create_successfully";
    public static final String ROOM_TYPE_NOT_FOUND = "room_type.list_room_type.room_type_not_found";
    public static final String RETRIEVED_ROOM_TYPES_SUCCESSFULLY = "room_type.list_room_type.retrieved_room_types_successfully";
    public static final String UPDATE_ROOM_TYPE_SUCCESSFULLY = "room_type.update_room_type.update_successfully";
    public static final String DELETE_ROOM_TYPE_SUCCESSFULLY = "room_type.delete_room_type.delete_successfully";
    public static final String RETRIEVED_ALL_BOOKINGS_SUCCESSFULLY = "booking.list_booking.retrieved_all_bookings_successfully";
    public static final String NO_BOOKINGS_FOUND = "booking.no_bookings_found";
    public static final String NO_HOTELS_FOUND = "hotel.list_hotel.no_hotels_found";
    public static final String UPDATE_BOOKING_STATUS_SUCCESSFULLY = "booking.update_status.update_booking_status_successfully";
    public static final String UPDATE_BOOKING_SUCCESSFULLY = "booking.update_booking.update_successfully";
    public static final String RETRIEVED_BOOKING_DETAIL_SUCCESSFULLY = "booking.booking_detail.retrieved_booking_detail_successfully";
    public static final String CREATE_BOOKING_SUCCESSFULLY = "booking.create_booking.create_successfully";
    public static final String USER_DOES_NOT_HAVE_PERMISSION_TO_VIEW_BOOKINGS = "booking.list_booking.user_does_not_have_permission_to_view_bookings";
    public static final String UPDATE_LICENSE_SUCCESSFULLY = "license.update_license.update_successfully";
    public static final String HOTEL_IS_PENDING = "hotel.update_status.hotel_is_pending";

    public static final String HOTEL_IS_CLOSED = "hotel.update_status.hotel_is_closed";
    public static final String USER_DOES_NOT_HAVE_PERMISSION_TO_CREATE_HOTEL = "hotel.create_hotel.USER_DOES_NOT_HAVE_PERMISSION_TO_CREATE_HOTEL";
    public static final String USER_DOES_NOT_HAVE_PERMISSION_TO_UPDATE_HOTEL = "hotel.update_hotel.USER_DOES_NOT_HAVE_PERMISSION_TO_UPDATE_HOTEL";
    public static final String INVALID_DATE_FORMAT = "invalid.date.format";
    public static final String USER_DOES_NOT_HAVE_PERMISSION_TO_VIEW_HOTEL = "hotel.list_hotel.USER_DOES_NOT_HAVE_PERMISSION_TO_VIEW_HOTEL";
}
