package com.example.andre.aplicacaoposicionamento;

public class UniversalVariable {
    public static  UniversalVariable instance = null;
    public static UniversalVariable getInstance(){
        if (instance==null){
            instance = new UniversalVariable();
        }
        return instance;

    }
    private String url = "http://192.168.223.65:8080";
    private String localizaçãox;
    private String localizaçãoy;

    public void Put_Localizacao_x(String localizaçãox){
        this.localizaçãox=localizaçãox;
    }
    public void Put_Localizacao_y (String localizaçãoy){
        this.localizaçãoy=localizaçãoy;
    }

    public String Get_Localizacao_x (){
        return localizaçãox;
    }

    public String Get_Localizacao_y(){
        return localizaçãoy;
    }
    public String getValue() {
        return url;
    }


}
