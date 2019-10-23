package com.example.andre.aplicaocalibracao;

import java.util.HashMap;
import java.util.Map;

class AccessPoint {
    private String SSID;
    private String MAC;
    private String ID_AP;
    private static AccessPoint instance = null;

    public AccessPoint() {
    }

    public AccessPoint(AccessPoint accessPoint) {
        this.SSID = SSID;
        this.MAC = MAC;
        this.ID_AP=ID_AP;
    }
    public static  AccessPoint getInstance(){
        if (instance==null){
            instance = new AccessPoint();

        }
        return instance;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public void setID_AP(String ID_AP) {
        this.ID_AP = ID_AP;
    }

    public String getID_AP() {
        return ID_AP;
    }




    public static class MapID_APImpl{
        private Map<String,String> ID_APMap = new HashMap<String, String>();
        private Map<String,String> ID_AP_SSID = new HashMap<String, String>();
        private Map<String,String> ID_AP_MAC = new HashMap<String, String>();

        private static MapID_APImpl instance;
        private MapID_APImpl(){

        }
        public static MapID_APImpl getInstance(){
            if (instance==null){
                instance = new MapID_APImpl();
            }
            return instance;

        }
        public void putID_AP (String id, String ID_AP) {
            ID_APMap.put(id, ID_AP);
        }

        public void putID_SSID (String id, String SSID){
            ID_AP_SSID.put(id, SSID);
        }
        public void putID_MAC (String id, String MAC){
            ID_AP_MAC.put(id, MAC);
        }

        public String getID(String id){
            String ID_new = ID_APMap.get(id);
            return ID_new;
        }
        public String get_id_Ap_SSID(String id){
            String ID_new = ID_AP_SSID.get(id);
            return ID_new;
        }
        public String get_Id_AP_MAC(String id){
            String ID_new = ID_AP_MAC.get(id);
            return ID_new;
        }
    }
}

