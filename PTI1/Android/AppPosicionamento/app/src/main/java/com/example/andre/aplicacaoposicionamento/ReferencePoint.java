package com.example.andre.aplicacaoposicionamento;

import java.util.HashMap;
import java.util.Map;

public class ReferencePoint extends Scanwifi {
    private String id;
    private String id_rf;
    private String name;
    private String coordx;
    private String coordy;
    private String isoffline;
    private String Spaceid_space;
    private static ReferencePoint instance = null;

    public ReferencePoint() {

    }

    public ReferencePoint(ReferencePoint another) {
        this.id_rf = id_rf;
        this.name = name;
        this.coordx = coordx;
        this.coordy = coordy;
        this.isoffline = isoffline;
        this.Spaceid_space = Spaceid_space;


    }

    public static ReferencePoint getInstance() {
        if (instance == null) {
            instance = new ReferencePoint();
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public String getCoordx() {
        return coordx;
    }

    public String getCoordy() {
        return coordy;
    }

    public String getIsoffline() {
        return isoffline;
    }

    public String getSpaceid_space() {
        return Spaceid_space;
    }

    public String getId_rf() {
        return id_rf;
    }

    public void setId_rf(String id_rf) {
        this.id_rf = id_rf;
    }


    public void setSpaceid_space(String spaceid_space) {
        Spaceid_space = spaceid_space;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordx(String coordx) {
        this.coordx = coordx;
    }

    public void setCoordy(String coordy) {
        this.coordy = coordy;
    }

    public void setIsoffline(String isoffline) {
        this.isoffline = isoffline;
    }



    public static class MapID_RFImpl{

        private Map<String,String> ID_RFMap = new HashMap<String, String>();
        private Map<String,String> ID_RF_Nome = new HashMap<String, String>();
        private Map<String,String> ID_RF_x = new HashMap<String, String>();
        private Map<String,String> ID_RF_y = new HashMap<String, String>();
        private Map<String,String> ID_RF_isoffline = new HashMap<String, String>();
        private Map<String,String> ID_RF_Idspace = new HashMap<String, String>();

        private static MapID_RFImpl instance;
        private MapID_RFImpl(){

        }
        public static MapID_RFImpl getInstance(){
            if (instance==null){
                instance = new MapID_RFImpl();
            }
            return instance;

        }
        public void put (String id, String ID_RF) {
            ID_RFMap.put(id, ID_RF);
        }

        public void  put_RF_Nome (String id, String RF_name){
            ID_RF_Nome.put(id,RF_name);
        }

        public void  put_RF_X (String id, String RF_X){
            ID_RF_x.put(id,RF_X);
        }

        public void  put_RF_Y (String id, String RF_Y){
            ID_RF_y.put(id,RF_Y);
        }

        public void put_RF_ISOffline (String id, String RF_Isoffline){
            ID_RF_isoffline.put(id,RF_Isoffline);
        }
        public void put_RF_Isspace (String id, String RF_Idspace){
            ID_RF_Idspace.put(id,RF_Idspace);
        }

        public String getID(String id){
            String ID_new = ID_RFMap.get(id);
            return ID_new;
        }
        public String get_RF_Name(String id){
            String ID_new = ID_RF_Nome.get(id);
            return ID_new;
        }
        public String get_RF_X(String id){
            String ID_new = ID_RF_x.get(id);
            return ID_new;
        }
        public String get_RF_Y(String id){
            String ID_new = ID_RF_y.get(id);
            return ID_new;
        }
        public String get_RF_Isoffline(String id){
            String ID_new = ID_RF_isoffline.get(id);
            return ID_new;
        }
        public String get_RF_Idspace(String id){
            String ID_new = ID_RF_Idspace.get(id);
            return ID_new;
        }
    }
}

