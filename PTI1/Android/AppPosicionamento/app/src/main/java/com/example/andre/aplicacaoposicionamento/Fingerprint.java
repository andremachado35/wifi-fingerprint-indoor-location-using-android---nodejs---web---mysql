package com.example.andre.aplicacaoposicionamento;

import java.util.HashMap;
import java.util.Map;

public class Fingerprint {
    private String id_fingerprint;
    private String date;
    private String referencepoint_id;
    private String userid_user;

    public Fingerprint(String id_fingerprint, String date, String referencepoint_id, String userid_user) {
        this.id_fingerprint = id_fingerprint;
        this.date = date;
        this.referencepoint_id = referencepoint_id;
        this.userid_user = userid_user;
    }

    public String getId_fingerprint() {
        return id_fingerprint;
    }

    public void setId_fingerprint(String id_fingerprint) {
        this.id_fingerprint = id_fingerprint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReferencepoint_id() {
        return referencepoint_id;
    }

    public void setReferencepoint_id(String referencepoint_id) {
        this.referencepoint_id = referencepoint_id;
    }

    public String getUserid_user() {
        return userid_user;
    }

    public void setUserid_user(String userid_user) {
        this.userid_user = userid_user;
    }

    public static class MapID_FPImpl{
        private Map<String,String> ID_FPMap = new HashMap<String, String>();
        private Map<String,String> ID_FP_Date=new HashMap<>();
        private Map<String,Integer> Count = new HashMap<>();
        private static MapID_FPImpl instance;
        private MapID_FPImpl(){

        }
        public static MapID_FPImpl getInstance(){
            if (instance==null){
                instance = new MapID_FPImpl();
            }
            return instance;

        }
        public void put (String id, String ID_FP) {
            ID_FPMap.put(id, ID_FP);

        }
        public void put_ID_FP_Date (String id, String FP_Date){
            ID_FP_Date.put(id,FP_Date);
        }
        public int getCount(){
            int count_new = Count.get("0");
            return count_new;
        }
        public  void put_count (int count){
            Count.put("0",count);

        }

        public String getIDFP(String id){
            String ID_new = ID_FPMap.get(id);
            return ID_new;
        }
        public String get_ID_FP_Date(String id){
            String ID_new = ID_FP_Date.get(id);
            return ID_new;
        }

    }
}
