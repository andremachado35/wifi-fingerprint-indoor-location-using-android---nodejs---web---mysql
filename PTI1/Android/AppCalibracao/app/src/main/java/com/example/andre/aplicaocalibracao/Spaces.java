package com.example.andre.aplicaocalibracao;

import java.util.HashMap;
import java.util.Map;

public class Spaces {
    public static class MapID_SpacesImpl{
        private Map<String,String> ID_SPACE = new HashMap<String, String>();
        private Map<String,String> ID_Space_name = new HashMap<String, String>();
        private Map<String,String> Spinner_Name_Id = new HashMap<String, String>();
        private Map<String,String> ID_Comprimento = new HashMap<>();
        private Map<String,String> ID_Largura = new HashMap<>();
        private Map<String,String> ID_Description = new HashMap<>();
        private Map<String,String> ID_Path = new HashMap<>();
        private Map<String,String> ID_User = new HashMap<>();
        private static Spaces.MapID_SpacesImpl instance;
        String SelectedSpace;
        int NumberOfSpaces;
        private MapID_SpacesImpl(){

        }
        public static Spaces.MapID_SpacesImpl getInstance(){
            if (instance==null){
                instance = new Spaces.MapID_SpacesImpl();
            }
            return instance;

        }

        public void putID_Space (String id, String ID_Space) {
            ID_SPACE.put(id, ID_Space);
        }

        public void put_ID_Space_Description (String id, String Description){
            ID_Description.put(id, Description);
        }

        public void put_ID_Space_Name(String id, String name){
            ID_Space_name.put(id,name);
        }

        public void putSpinner_ID_Name(String Id_Space, String Name){
            Spinner_Name_Id.put(Name,Id_Space);
        }
        public void put_ID_Space_Comprimento(String id, String Comprimento){
            ID_Comprimento.put(id,Comprimento);
        }

        public void put_ID_Space_Largura (String id, String largura){
            ID_Largura.put(id,largura);
        }

        public void Put_ID_Space_Path ( String id, String path){
            ID_Path.put(id,path);
        }
        public void Put_ID_User (String id, String user){
            ID_User.put(id,user);
        }
        public void Put_SelectedSpace(String SelectedSpace){
            this.SelectedSpace=SelectedSpace;
        }
        public String get_ID_Space_Comprimento(String id){
            String Comprimento =ID_Comprimento.get(id);
            return Comprimento;
        }
        public String get_ID_Space_Largura(String id){
            String Largura = ID_Largura.get(id);
            return Largura;
        }
        public String getID_Space(String id){
            String ID_new = ID_SPACE.get(id);
            return ID_new;
        }
        public String get_id_Space_Name(String id){
            String ID_new = ID_Space_name.get(id);
            return ID_new;
        }
        public String Get_MapPath_Space_ID(String id){
            String Map_Path = ID_Path.get(id);
            return Map_Path;
        }
        public String Get_Space_Description_By_Name(String id){
            String New_Description = ID_Description.get(id);
            return New_Description;
        }
        public String getID_User(String id){
            String User = ID_User.get(id);
            return User;
        }
        public String get_by_the_Name(String Name){
            String ID_New = Spinner_Name_Id.get(Name);
            return ID_New;
        }

        public String GetSelectedSpace() {
            return SelectedSpace;
        }
        public void Put_NumberOfSpaces(int newnumber){this.NumberOfSpaces=newnumber;}
        public int Get_NumberOfSpaces() {
            return NumberOfSpaces;
        }

    }
}
