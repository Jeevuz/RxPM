package me.dmdev.rxpm.permissions

/**
 * Represents the runtime permissions (dangerous).
 */
enum class Permission(internal val value: String) {

    // Calendar
    READ_CALENDAR("android.permission.READ_CALENDAR"),
    WRITE_CALENDAR("android.permission.WRITE_CALENDAR"),

    // Camera
    CAMERA("android.permission.CAMERA"),

    // Contacts
    READ_CONTACTS("android.permission.READ_CONTACTS"),
    WRITE_CONTACTS("android.permission.WRITE_CONTACTS"),
    GET_ACCOUNTS("android.permission.GET_ACCOUNTS"),

    // Location
    ACCESS_FINE_LOCATION("android.permission.ACCESS_FINE_LOCATION"),
    ACCESS_COARSE_LOCATION("android.permission.ACCESS_COARSE_LOCATION"),

    // Microphone
    RECORD_AUDIO("android.permission.RECORD_AUDIO"),

    // Phone
    READ_PHONE_STATE("android.permission.READ_PHONE_STATE"),
    READ_PHONE_NUMBERS("android.permission.READ_PHONE_NUMBERS"),
    CALL_PHONE("android.permission.CALL_PHONE"),
    ANSWER_PHONE_CALLS("android.permission.ANSWER_PHONE_CALLS"),
    READ_CALL_LOG("android.permission.READ_CALL_LOG"),
    WRITE_CALL_LOG("android.permission.WRITE_CALL_LOG"),
    ADD_VOICEMAIL("com.android.voicemail.permission.ADD_VOICEMAIL"),
    USE_SIP("android.permission.USE_SIP"),
    PROCESS_OUTGOING_CALLS("android.permission.PROCESS_OUTGOING_CALLS"),

    // Sensors
    BODY_SENSORS("android.permission.BODY_SENSORS"),

    // Sms
    SEND_SMS("android.permission.SEND_SMS"),
    RECEIVE_SMS("android.permission.RECEIVE_SMS"),
    READ_SMS("android.permission.READ_SMS"),
    RECEIVE_WAP_PUSH("android.permission.RECEIVE_WAP_PUSH"),
    RECEIVE_MMS("android.permission.RECEIVE_MMS"),

    // Storage
    READ_EXTERNAL_STORAGE("android.permission.READ_EXTERNAL_STORAGE"),
    WRITE_EXTERNAL_STORAGE("android.permission.WRITE_EXTERNAL_STORAGE")
}