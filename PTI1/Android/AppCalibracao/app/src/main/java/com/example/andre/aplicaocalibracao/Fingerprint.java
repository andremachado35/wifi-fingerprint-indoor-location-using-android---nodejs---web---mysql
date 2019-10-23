package com.example.andre.aplicaocalibracao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

public class Fingerprint extends AppCompatActivity {

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
