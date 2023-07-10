package com.bbits.park;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class GlobalContext {
//    public static interface OnPlateChangeListener
//    {
//        public void onPlateChanged(String newValue);
//    }
//
//    private OnPlateChangeListener listener;
//
//    public void setOnPlateChangeListener(OnPlateChangeListener listener)
//    {
//        this.listener = listener;
//    }


    public static String getEmail() {
        return email;
    }

    public static void setEmail(String a) {
        email = a;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String a) {
        userId = a;
    }

    public static JSONArray getCities() {
        return cities;
    }

    public static void setCities(JSONArray a) {
        cities = a;
    }

    public static JSONArray getZones() {
        return zones;
    }

    public static void setZones(JSONArray a) {
        zones = a;
    }

    public static Object getSelectedZoneName() {
        return selectedZoneName;
    }

    public static void setSelectedZoneName(String a) {
        selectedZoneName = a;
    }

    public static Object getSelectedZoneId() {
        return selectedZoneId;
    }

    public static void setSelectedZoneId(String a) {
        selectedZoneId = a;
    }

    public static Object getAgentZoneName() {
        return agentZoneName;
    }

    public static void setAgentZoneName(String a) {
        agentZoneName = a;
    }

    public static Object getAgentZoneId() {
        return agentZoneId;
    }

    public static void setAgentZoneId(String a) {
        agentZoneId = a;
    }

    public static Object getSelectedCityName() {
        return selectedCityName;
    }

    public static void setSelectedCityName(String a) {
        selectedCityName = a;
    }

    public static Object getSelectedCityId() {
        return selectedCityId;
    }

    public static void setSelectedCityId(String a) {
        selectedCityId = a;
    }

    public static JSONObject getSelectedTicket() {
        return selectedTicket;
    }

    public static void setSelectedTicket(JSONObject a) {
        selectedTicket = a;
    }

    public static JSONObject getSelectedHistoryTicket() {
        return selectedHistoryTicket;
    }

    public static void setSelectedHistoryTicket(JSONObject a) {
        selectedHistoryTicket = a;
    }

    public static String getParkingStatus() {
        return parkingStatus;
    }

    public static void setParkingStatus(String a) {
        parkingStatus = a;
    }

    public static String getFrom() {
        return from;
    }

    public static void setFrom(String a) {
        from= a;
    }

    public static String getTo() {
        return to;
    }

    public static void setTo(String a) {
         to= a;
    }

    public static String getParkingId() {
        return parkingId;
    }

    public static void setParkingId(String a) {
        parkingId = a;
    }

    public static JSONArray getTicketsIssued() {
        return ticketsIssued;
    }

    public static void setTicketsIssued(JSONArray a) {
        ticketsIssued = a;
    }

    public static ArrayList<Bitmap> getSelectedImages() {
        return selectedImages;
    }

    public static void setSelectedImages(ArrayList<Bitmap> a) {
        selectedImages = a;
    }

    public static String getPlateNum() {
        return plateNum;
    }

    public static void setPlateNum(String a) {

//        this.plateNum = a;
//
//        if(listener != null)
//        {
//            listener.onPlateChanged(a);
//        }
        plateNum = a;
    }
    private static String userId;
    private static String email;
    private static JSONArray cities;

    private static JSONArray zones;

    private static String selectedCityName;

    private static String selectedCityId;

    private static String selectedZoneName;

    private static String selectedZoneId;

    private static String agentZoneName = null;
    private static String agentZoneId = null;
    private static JSONObject selectedTicket;

    private static JSONObject selectedHistoryTicket;

    private static String plateNum;

    private static String parkingStatus = "Status";

    private static String from;

    private static String to;

    private static String parkingId;

    private static ArrayList<Bitmap> selectedImages = null;

    private static JSONArray ticketsIssued = null;



}
