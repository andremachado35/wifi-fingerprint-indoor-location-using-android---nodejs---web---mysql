package com.example.andre.aplicacaoposicionamento;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanwifi extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    List scannedResult;
    List mac_address;
    Button button;
    int newlevel, level;
    int contagem ;
    int id = 1;
    int ID_2_Int=0;
    String RSSIvalue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect beacons.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                } });
            builder.show();
        }
        setContentView(R.layout.activity_scanwifi);
        button = (Button) findViewById(R.id.button);
        scannedResult = new ArrayList();
        mac_address = new ArrayList();
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        contagem=0;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doProcess();
            }
        });

    }
    public void onResume() {
        super.onResume();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            salto2();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void salto2(){
        Intent intent = new Intent(Scanwifi.this,Login_Activity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permision", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public interface ServerCallback{
        void onSuccess(JSONObject result);
    }
    public void doProcess() {
        //POST da FINGERPRINT E AP
        final Fingerprint.MapID_FPImpl FP= Fingerprint.MapID_FPImpl.getInstance();
        PostReferencePoint(id);
        id++;
        FP.put_count(id);
        scanfunction();
    }

                                /*
                                *Post do Reference point
                                *
                                *
                                 */
public void PostReferencePoint(final int id){
    //ver token do utilizador
    SharedPreferences preferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
    String Token = preferences.getString("Token","");
    String bearer = "Bearer ";
    final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();

    final String Id_String = String.valueOf(id);
    String RF_Name ="1";
    String RF_X ="0";
    String RF_Y ="0";
    String RF_Isoffline ="0";
    String RF_Idspace ="1";
    final ReferencePoint.MapID_RFImpl RP= ReferencePoint.MapID_RFImpl.getInstance();
    RP.put_RF_Nome(Id_String, RF_Name);
    RP.put_RF_X(Id_String, RF_X);
    RP.put_RF_Y(Id_String, RF_Y);
    RP.put_RF_ISOffline(Id_String, RF_Isoffline);
    RP.put_RF_Isspace(Id_String, RF_Idspace);
    final String Name = RP.get_RF_Name(Id_String);
    final String CoorDx = RP.get_RF_X(Id_String);
    final String CoorDy = RP.get_RF_Y(Id_String);
    final String isoffline = RP.get_RF_Isoffline(Id_String);
    final String id_space = RP.get_RF_Idspace(Id_String);

    //url do post
    final UniversalVariable urlprev = UniversalVariable.getInstance();
    String posturl = new StringBuilder().append(urlprev.getValue()).append("/reference_points").toString();

    StringRequest RFstringRequest = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            try {
                //Ver id do reference point
                JSONObject js2 = new JSONObject(response);
                String data = js2.getString("data");
                JSONObject insert = new JSONObject(data);
                String id_reference_point = insert.getString("insertId");
                Toast.makeText(getApplicationContext(),"Adicionou com sucesso Reference Point:"+ id_reference_point, Toast.LENGTH_SHORT).show();
                //ADICIONAR NO HASHMAP
                RP.put(Id_String,id_reference_point);
                ReferencePoint.MapID_RFImpl rf = ReferencePoint.MapID_RFImpl.getInstance();
                String id_new_string = String.valueOf(id);
                rf.put(id_new_string, id_reference_point);
                if(id_reference_point!=null){
                    Postfingerprint(id_new_string);
                }
                //Toast.makeText(getApplicationContext(), insertId, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                String jsonError = new String(networkResponse.data);
                Log.e("LOG_VOLLEY", error.toString());
                ReferencePoint.MapID_RFImpl rf = ReferencePoint.MapID_RFImpl.getInstance();
                String id_reference_point = rf.getID(Id_String);
                Toast.makeText(getApplicationContext(), "Erro ao Adicionar Reference-Point:"+id_reference_point, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), jsonError, Toast.LENGTH_SHORT).show();

                // Print Error!
            }

        }
    }) {

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", tokenpost);
            return params;
        }
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> jsonBody = new HashMap<String, String>();
            jsonBody.put("name", Name);
            jsonBody.put("coordx", CoorDx);
            jsonBody.put("coordy", CoorDy);
            jsonBody.put("isOffline", isoffline);
            jsonBody.put("id_space", id_space);
            return jsonBody;
        }
    };
    RFstringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(RFstringRequest);
}


public void scanfunction() {
    //é neste que acaba por publicar
    if (wifiManager.isWifiEnabled() && wifiManager.setWifiEnabled(true) && wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED) {
        final Fingerprint.MapID_FPImpl FP= Fingerprint.MapID_FPImpl.getInstance();
        int id_int = FP.getCount();
        String Id_Anterior_String = String.valueOf(id_int-1);
        wifiManager.startScan();
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        //PERCORRER TODAS LEITURAS DE WIFI
        for (int i = 0; i < scanResultList.size(); i++) {
            List<ScanResult> results = wifiManager.getScanResults();
            for (ScanResult result : results) {
                if (result.BSSID.equals(scanResultList.get(i).BSSID)) {
                    //IMPRIMIR NO Listview
                    int size = scanResultList.size();
                    ID_2_Int ++;
                    String id_2 = String.valueOf(ID_2_Int);
                    final AccessPoint.MapID_APImpl AP = AccessPoint.MapID_APImpl.getInstance();
                    final RSSI.MapID_RSSIImpl Rssi = RSSI.MapID_RSSIImpl.getInstance();
                    RSSIvalue= "";
                    newlevel = 0;
                    //Definir nivel do RSSI
                    for(int j = 0 ; j<3; j++) {
                        level = WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), result.level);
                        newlevel += level;
                    }
                    RSSIvalue = String.valueOf((newlevel/3) + 100);
                    Rssi.put_ID_RSSI_Value(id_2, RSSIvalue);
                    String SSID = scanResultList.get(i).SSID;
                    if (SSID.isEmpty()){
                        AP.putID_SSID(id_2, "SSID VAZIO");
                    }
                    else {
                        AP.putID_SSID(id_2, SSID);
                    }
                    String BSSID = scanResultList.get(i).BSSID;
                    AP.putID_MAC(id_2, BSSID);
                    scannedResult.add("\n"+"SSID:"+ SSID + "\n" + "BSSID:"+ BSSID + "\n" + "RSSI:"+RSSIvalue + "\n" + "\n");
                    String s = scannedResult.toString();
                    PostAps(id_2,Id_Anterior_String,size);
                }
            }
        }
    }else {

        Toast.makeText(this,"Não tinha o WIFI ativo",Toast.LENGTH_LONG).show();
        wifiManager.setWifiEnabled(true);
    }
}

public void salto(){
    Intent intent = new Intent(Scanwifi.this,Space_list.class);
    startActivity(intent);
}

    //POST DO FINGERPRINT_AP
public void  PostFingerPrint_AP( String id_2,final int size,String Id_anterior_String){
    //ver token do utilizador e user id

    SharedPreferences preferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
    String Token = preferences.getString("Token","");
    final String user_id = preferences.getString("User","");
    String bearer = "Bearer ";
    final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
    //acess vars
    final AccessPoint.MapID_APImpl AP= AccessPoint.MapID_APImpl.getInstance();
    final Fingerprint.MapID_FPImpl FP= Fingerprint.MapID_FPImpl.getInstance();
    final String id_fingerprint = FP.getIDFP(Id_anterior_String);
    final String id_ap = AP.getID(id_2);
    final UniversalVariable urlprev = UniversalVariable.getInstance();
    String posturl = new StringBuilder().append(urlprev.getValue()).append("/fingerprint_aps").toString();
    StringRequest FPAPstringRequest = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            contagem++;
            if (contagem == size) {
                salto();
                Toast.makeText(getApplicationContext(), "Adicionou com sucesso o AP / FP", Toast.LENGTH_SHORT).show();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                String jsonError = new String(networkResponse.data);
                Log.e("LOG_VOLLEY", error.toString());
                Toast.makeText(getApplicationContext(), "Erro ao adicionar AP/FP", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), jsonError, Toast.LENGTH_SHORT).show();
                }

        }
    }) {

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", tokenpost);
            return params;
        }
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> jsonBody = new HashMap<String, String>();
            jsonBody.put("id_fingerprint", id_fingerprint);
            jsonBody.put("id_ap",id_ap);
            return jsonBody;
        }
    };
    FPAPstringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(FPAPstringRequest);
}
    // POST DO RSSI
public void PostRssi(final String id_2,final String id_anterior, final int size){
    //ver token do utilizador e user id
    SharedPreferences preferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
    String Token = preferences.getString("Token","");
    final String user_id = preferences.getString("User","");
    String bearer = "Bearer ";
    final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
    //acess vars
    final AccessPoint.MapID_APImpl AP= AccessPoint.MapID_APImpl.getInstance();
    final Fingerprint.MapID_FPImpl FP= Fingerprint.MapID_FPImpl.getInstance();
    final RSSI.MapID_RSSIImpl Rssi = RSSI.MapID_RSSIImpl.getInstance();
    final String ID_FP = FP.getIDFP(id_anterior);
    final String Value = Rssi.get_ID_RSSI_Value(id_2);
    final String ID_AP = AP.getID(id_2);
    final UniversalVariable urlprev = UniversalVariable.getInstance();
    String posturl = new StringBuilder().append(urlprev.getValue()).append("/rssis").toString();
    StringRequest RSSIstringRequest = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            try {
                JSONObject js2 = new JSONObject(response);
                String data = js2.getString("data");
                JSONObject insert = new JSONObject(data);
                String Id_rssi_id = insert.getString("insertId");
                Rssi.put_Id_Rssi_id(id_2,Id_rssi_id);
                PostFingerPrint_AP(id_2,size,id_anterior);
                Toast.makeText(getApplicationContext(), "Adicionou com sucesso o RSSI:"+Id_rssi_id, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                final RSSI.MapID_RSSIImpl Rssi = RSSI.MapID_RSSIImpl.getInstance();
                String ID_RSSI = Rssi.getID(id_2);
                Toast.makeText(getApplicationContext(), "Erro ao adicionar o RSSI:"+ID_RSSI, Toast.LENGTH_SHORT).show();
                String jsonError = new String(networkResponse.data);
                Log.e("LOG_VOLLEY", error.toString());
                Toast.makeText(getApplicationContext(), jsonError, Toast.LENGTH_SHORT).show();
            }
        }
    }) {

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", tokenpost);
            return params;
        }
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> jsonBody = new HashMap<String, String>();
            jsonBody.put("id_ap", ID_AP);
            jsonBody.put("value", Value);
            jsonBody.put("id_fingerprint",ID_FP);
            return jsonBody;
        }
    };
    RSSIstringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(RSSIstringRequest);
}
    // Post de Apps
public void PostAps( final String id_2, final String Id_Anterior_String, final int size_oflistAps){
    //ver token do utilizador e user id
    SharedPreferences preferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
    String Token = preferences.getString("Token","");
    final String user_id = preferences.getString("User","");
    String bearer = "Bearer ";
    final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
    //Access point vars
    final AccessPoint.MapID_APImpl AP= AccessPoint.MapID_APImpl.getInstance();
    Fingerprint.MapID_FPImpl FP= Fingerprint.MapID_FPImpl.getInstance();
    final String SSID = AP.get_id_Ap_SSID(id_2);
    final String BSSID =AP.get_Id_AP_MAC(id_2);
    final String Id_Fp= FP.getIDFP(Id_Anterior_String);
    //url do post
    final UniversalVariable urlprev = UniversalVariable.getInstance();
    String posturl = new StringBuilder().append(urlprev.getValue()).append("/aps").toString();

    StringRequest APstringRequest = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject js2 = new JSONObject(response);
                String data = js2.getString("data");
                JSONObject insert = new JSONObject(data);
                String Id_AP = insert.getString("insertId");
                AP.putID_AP(id_2,Id_AP);

                if(Id_AP!=null ||Id_Fp !=null){
                    PostRssi(id_2,Id_Anterior_String,size_oflistAps);
                }
                Toast.makeText(getApplicationContext(), "Adicionou com sucesso o Acess Point:"+Id_AP, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                String jsonError = new String(networkResponse.data);
                Log.e("LOG_VOLLEY", error.toString());
                final AccessPoint.MapID_APImpl AP= AccessPoint.MapID_APImpl.getInstance();
                String Id_AP= AP.getID(id_2);
                Toast.makeText(getApplicationContext(), "Erro ao adicionar o Acess Point:"+Id_AP, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), jsonError, Toast.LENGTH_SHORT).show();
            }
        }
    }){

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", tokenpost);
            return params;
        }
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> jsonBody = new HashMap<String, String>();
            jsonBody.put("ssid", SSID);
            jsonBody.put("mac", BSSID);
            return jsonBody;
        }
    };
    APstringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(APstringRequest);
}
    //POST fingerprint
public void  Postfingerprint(final String String_new_id ){
    //Token do user
    //ver token do utilizador e user id
    SharedPreferences preferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
    String Token = preferences.getString("Token","");
    final String user_id = preferences.getString("User","");
    String bearer = "Bearer ";
    final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
    //REFERENCE POINT
    final ReferencePoint.MapID_RFImpl RP= ReferencePoint.MapID_RFImpl.getInstance();
    final String Id_rf = RP.getID(String_new_id);
    //DATA ATUAL NO FORMATO PEDIDO
    final Fingerprint.MapID_FPImpl FP= Fingerprint.MapID_FPImpl.getInstance();
    SimpleDateFormat dateint = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    final String date = dateint.format(new Date());
    FP.put_ID_FP_Date(String_new_id,date);
    //Url do post
    final UniversalVariable urlprev = UniversalVariable.getInstance();
    String posturl = new StringBuilder().append(urlprev.getValue()).append("/fingerprints").toString();

    StringRequest FPstringRequest = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject js2 = new JSONObject(response);
                String error = js2.getString("error");
                if (error == "false"){
                    //A receber: ID FP
                    JSONObject fingerprintresponse = new JSONObject(response);
                    String fingerprintresponsedata = fingerprintresponse.getString("data");
                    JSONObject insert = new JSONObject(fingerprintresponsedata);
                    String id_fingerprint = insert.getString("insertId");
                    //HASHMAP DE FP IDS, quando recebe mete laa
                    Fingerprint.MapID_FPImpl FP= Fingerprint.MapID_FPImpl.getInstance();
                    FP.put(String_new_id, id_fingerprint);
                    Toast.makeText(getApplicationContext(),"Adicionou com sucesso a Fingerprint:"+id_fingerprint, Toast.LENGTH_SHORT).show();
                    scanfunction();


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                String jsonError = new String(networkResponse.data);
                Log.e("LOG_VOLLEY", error.toString());
                Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
                String id_fingerprint = FP.getIDFP(String_new_id);
                Toast.makeText(getApplicationContext(), "Erro ao adicionar o Reference-Point:"+id_fingerprint, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), jsonError, Toast.LENGTH_SHORT).show();
            }

        }
    })
    {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", tokenpost);
            return params;
        }
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> jsonBody = new HashMap<String, String>();
            jsonBody.put("date", date);
            jsonBody.put("id_reference_point", Id_rf);
            jsonBody.put("id_user",user_id);
            return jsonBody;
        }
    };
    FPstringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(FPstringRequest);
}
}
