package com.example.andre.aplicaocalibracao;

public class UniversalVariable {
    public static  UniversalVariable instance = null;
    public static UniversalVariable getInstance(){
        if (instance==null){
            instance = new UniversalVariable();
        }
        return instance;

    }
    private String url = "http://192.168.223.65:8080";

    public String getValue() {
        return url;
    }


}
