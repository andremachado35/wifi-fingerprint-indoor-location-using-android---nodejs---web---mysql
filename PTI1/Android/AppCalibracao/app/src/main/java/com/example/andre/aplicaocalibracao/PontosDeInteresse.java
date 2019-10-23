package com.example.andre.aplicaocalibracao;


import java.util.HashMap;
import java.util.Map;


public class PontosDeInteresse  {

    public static class Map_ID_PIImpl {
        private Map<String,String> ID_PI_Name = new HashMap<String, String>();
        private Map<String,String> ID_PI_coordx = new HashMap<String, String>();
        private Map<String,String> ID_RF_coordy = new HashMap<String, String>();
        int NumerodeEspacos;

        private static PontosDeInteresse.Map_ID_PIImpl instance;
        private Map_ID_PIImpl(){

        }
        public static PontosDeInteresse.Map_ID_PIImpl getInstance(){
            if (instance==null){
                instance = new PontosDeInteresse.Map_ID_PIImpl();
            }
            return instance;
        }

        public void  put_PI_Name (String PI_ID, String PI_Name){
            ID_PI_Name.put(PI_ID,PI_Name);
        }
        public void  put_PI_Coordx (String PI_ID, String PI_Coordx){
            ID_PI_coordx.put(PI_ID,PI_Coordx);
        }
        public void  put_PI_Coordy (String PI_ID, String PI_Coordy){
            ID_RF_coordy.put(PI_ID,PI_Coordy);
        }
        public void Put_NumberOfSpaces(int Number_Of_Pi){
            this.NumerodeEspacos=Number_Of_Pi;
        }

        public String get_PI_Name_by_ID(String id){
            String ID_PI_New = ID_PI_Name.get(id);
            return ID_PI_New;
        }

        public String get_PI_Coordx_by_ID(String id){
            String Coordx_PI_New = ID_PI_coordx.get(id);
            return Coordx_PI_New;
        }
        public String get_PI_Coordy_by_ID(String id){
            String Coordy_PI_New = ID_RF_coordy.get(id);
            return Coordy_PI_New;
        }

        public int GetSelectedSpace() {
            return NumerodeEspacos;
        }

    }
}