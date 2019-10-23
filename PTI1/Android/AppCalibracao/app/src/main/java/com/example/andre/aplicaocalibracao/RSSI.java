package com.example.andre.aplicaocalibracao;

import java.util.HashMap;
import java.util.Map;

public class RSSI {
    private String ID_RSSI;
    private String Value;

    public RSSI(){

    }
    public RSSI(RSSI rssi) {
        this.ID_RSSI = ID_RSSI;
        this.Value = Value;
    }

    public String getID_RSSI() {
        return ID_RSSI;
    }

    public void setID_RSSI(String ID_RSSI) {
        this.ID_RSSI = ID_RSSI;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }



    public static class MapID_RSSIImpl{
        private Map<String,String> ID_RSSI_id = new HashMap<String, String>();
        private Map<String,String> ID_RSSI_value = new HashMap<String, String>();

        private static MapID_RSSIImpl instance;
        private MapID_RSSIImpl(){

        }
        public static MapID_RSSIImpl getInstance(){
            if (instance==null){
                instance = new MapID_RSSIImpl();
            }
            return instance;

        }
        public void put_Id_Rssi_id (String Id, String Rssi_Id) {
            ID_RSSI_id.put(Id, Rssi_Id);
        }

        public void put_ID_RSSI_Value (String Id, String Rssi_Value){
            ID_RSSI_value.put(Id, Rssi_Value);
        }


        public String getID(String Id){
            String ID_new = ID_RSSI_id.get(Id);
            return ID_new;
        }
        public String get_ID_RSSI_Value(String Id){
            String ID_new = ID_RSSI_value.get(Id);
            return ID_new;
        }
    }
}
