package com.example.andre.aplicaocalibracao;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import com.davemorrissey.labs.subscaleview.ImageSource;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    WifiManager wifiManager;
    private PinView imageView;
    int id = 1;
    int ID_2_Int=0;
    int contagem = 0 ;
    int level, newlevel;
    String RSSIval;
    String RSSIvalue;


    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView =(PinView) findViewById(R.id.imageView);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        executarforadocreate();
    }

    public void executarforadocreate(){
        //Carregar imagem do espaço
        final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
        String SelectedItem = spaces.GetSelectedSpace();
        String id_space = spaces.get_by_the_Name(SelectedItem);
        String Largura = spaces.get_ID_Space_Largura(id_space);
        String Comprimento = spaces.get_ID_Space_Comprimento(id_space);
        String SpaceName = spaces.get_id_Space_Name(id_space);
        SpaceName = SpaceName.replace(" ","+");
        final int largura = Integer.parseInt(Largura)*2;
        final int comprimento = Integer.parseInt(Comprimento)*2;
        //final String urlfinal = "https://dummyimage.com/" + largura + "x" + comprimento+ "/000/fff.png&text=" + SpaceName;
        // url alternativo
        final String urlfinal = "http://fpoimg.com/" + largura+"x"+comprimento+"?text="+SpaceName;

        //apresentar imagem
        AsyncTask download = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    return BitmapFactory.decodeStream((InputStream)new URL(urlfinal).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                imageView.setImage(ImageSource.bitmap((Bitmap) o));
            }
        };
        download.execute();
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imageView.isReady()) {
                    final PointF sCoord = imageView.viewToSourceCoord(e.getX(), e.getY());
                    Toast.makeText(getApplicationContext(),"scord:"+sCoord.toString()+"\n larg: "+largura+" comp: "+comprimento,Toast.LENGTH_LONG).show();
                    if(sCoord.y>0 && sCoord.y<=comprimento && sCoord.x>0 && sCoord.x<=largura){
                        //adicionar o pino
                        imageView.setPin(sCoord, false);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Confirmar!");
                        builder.setMessage("Tem a certeza que pretende calibrar em x:"+Math.round(sCoord.x/2)+" y: "+Math.round(sCoord.y/2)+"?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final ReferencePoint.MapID_RFImpl RP = ReferencePoint.MapID_RFImpl.getInstance();
                                RP.put_RF_X("largura",Integer.toString(Math.round(sCoord.x/2)));
                                RP.put_RF_Y("comprimento", Integer.toString(Math.round(sCoord.y/2)));
                                postmethod();
                                imageView.setPin(sCoord, true);
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ReferencePoint.MapID_RFImpl RP = ReferencePoint.MapID_RFImpl.getInstance();
                                imageView.setPin(sCoord, false);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                return true;
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
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
        Intent intent = new Intent(MainActivity.this,ListSpaces.class);
        startActivity(intent);
    }

    //O botão do cantinho i
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item5) {
        // Handle presses on the action bar items
        switch (item5.getItemId()) {
            case R.id.action_search:
                // Code you want run when activity is clicked
                salto();
                Toast.makeText(this, "Informação sobre o ponto de referência", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item5);
        }
    }
    public void salto(){
        Intent intent = new Intent(MainActivity.this,InfoPI.class);
        startActivity(intent);
    }

    public void postmethod() {
//POST da FINGERPRINT E AP
        final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
        PostReferencePoint(id);
        id++;
        FP.put_count(id);
    }

    public void PostReferencePoint(final int id) {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String Token = preferences.getString("Token", "");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //parametros RF
        final ReferencePoint.MapID_RFImpl RP = ReferencePoint.MapID_RFImpl.getInstance();
        final String Id_String = String.valueOf(id);
        //Buscar espaco selecionado
        final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
        String Selected_Space = spaces.GetSelectedSpace();
        String RF_Idspace = spaces.get_by_the_Name(Selected_Space);
        String RF_Name = "1";
        String RF_X = RP.get_RF_X("largura");
        String RF_Y = RP.get_RF_Y("comprimento");
        String RF_Isoffline = "1";
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

        //Url do post
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
                    RP.put(Id_String, id_reference_point);
                    ReferencePoint.MapID_RFImpl rf = ReferencePoint.MapID_RFImpl.getInstance();
                    String id_new_string = String.valueOf(id);
                    rf.put(id_new_string, id_reference_point);
                    if (id_reference_point != null) {
                        Postfingerprint(id_new_string);
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
                    ReferencePoint.MapID_RFImpl rf = ReferencePoint.MapID_RFImpl.getInstance();
                    String id_reference_point = rf.getID(Id_String);
                    Toast.makeText(getApplicationContext(), "Erro ao Adicionar Reference-Point:"+id_reference_point, Toast.LENGTH_SHORT).show();
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
                jsonBody.put("name", Name);
                jsonBody.put("coordx", CoorDx);
                jsonBody.put("coordy", CoorDy);
                jsonBody.put("isOffline", isoffline);
                jsonBody.put("id_space", id_space);
                return jsonBody;
            }
        };
        RFstringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(RFstringRequest);
    }

    public void Postfingerprint(final String String_new_id) {
        //Token do user
        //ver token do utilizador e user id
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String Token = preferences.getString("Token", "");
        final String user_id = preferences.getString("User", "");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //REFERENCE POINT
        final ReferencePoint.MapID_RFImpl RP = ReferencePoint.MapID_RFImpl.getInstance();
        final String Id_rf = RP.getID(String_new_id);
        //DATA ATUAL NO FORMATO PEDIDO
        final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
        SimpleDateFormat dateint = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final String date = dateint.format(new Date());
        FP.put_ID_FP_Date(String_new_id, date);
        //Url do post
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String posturl = new StringBuilder().append(urlprev.getValue()).append("/fingerprints").toString();

        StringRequest FPstringRequest = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject js2 = new JSONObject(response);
                    String error = js2.getString("error");

                    if (error == "false") {
                        //A receber: ID FP
                        JSONObject fingerprintresponse = new JSONObject(response);
                        String fingerprintresponsedata = fingerprintresponse.getString("data");
                        JSONObject insert = new JSONObject(fingerprintresponsedata);
                        String id_fingerprint = insert.getString("insertId");
                        //HASHMAP DE FP IDS, quando recebe mete laa
                        Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
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
                jsonBody.put("date", date);
                jsonBody.put("id_reference_point", Id_rf);
                jsonBody.put("id_user", user_id);
                return jsonBody;
            }
        };
        FPstringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(FPstringRequest);
    }


    public void scanfunction() {
    //é neste que acaba por publicar
    if (wifiManager.isWifiEnabled() && wifiManager.setWifiEnabled(true) && wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED) {
        final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
        int id_int = FP.getCount();
        String Id_Anterior_String = String.valueOf(id_int - 1);
        wifiManager.startScan();
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        //PERCORRER TODAS LEITURAS DE WIFI
        for (int i = 0; i < scanResultList.size(); i++) {
            List<ScanResult> results = wifiManager.getScanResults();

            for (ScanResult result : results) {
                if (result.BSSID.equals(scanResultList.get(i).BSSID)) {
                    //IMPRIMIR NO Listview
                    int size = scanResultList.size();
                    ID_2_Int++;
                    String id_2 = String.valueOf(ID_2_Int);
                    final AccessPoint.MapID_APImpl AP = AccessPoint.MapID_APImpl.getInstance();
                    final RSSI.MapID_RSSIImpl Rssi = RSSI.MapID_RSSIImpl.getInstance();
                    RSSIvalue= "";
                    newlevel = 0;
                    //Definir nivel do RSSI
                    for (int j = 0; j < 3; j++) {
                        level = WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), result.level);
                        newlevel += level;
                    }
                    RSSIval = "";
                    RSSIval = String.valueOf((newlevel / 3) + 100);
                    Rssi.put_ID_RSSI_Value(id_2, RSSIval);
                    //VER ID DO ARRAYLIST
                    //DEFINIR SSID BSSID do AP
                    String SSID = scanResultList.get(i).SSID;
                    if (SSID.isEmpty()){
                        AP.putID_SSID(id_2, "SSID VAZIO");
                    }
                    else {
                        AP.putID_SSID(id_2, SSID);
                    }
                    AP.putID_SSID(id_2, SSID);
                    String BSSID = scanResultList.get(i).BSSID;
                    AP.putID_MAC(id_2, BSSID);
                    PostAps(id_2, Id_Anterior_String, size);
                }
            }
        }
    }else {
        Toast.makeText(this,"Não tinha o WIFI ativo",Toast.LENGTH_LONG).show();
        wifiManager.setWifiEnabled(true);

    }

        }


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
        final String SSID;
        if(AP.get_id_Ap_SSID(id_2).isEmpty()){
            SSID = "SSID_VAZIO";
        }else {
            SSID = AP.get_id_Ap_SSID(id_2);
        }
        final String BSSID =AP.get_Id_AP_MAC(id_2);
        final String Id_Fp= FP.getIDFP(Id_Anterior_String);
        //Url do post
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
//                    createButtons();
                    // Print Error!
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
        //Url do post
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
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
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
                    final RSSI.MapID_RSSIImpl Rssi = RSSI.MapID_RSSIImpl.getInstance();
                    String ID_RSSI = Rssi.getID(id_2);
                    Toast.makeText(getApplicationContext(), "Erro ao adicionar o RSSI:"+ID_RSSI, Toast.LENGTH_SHORT).show();
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
        //Url do post
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String posturl = new StringBuilder().append(urlprev.getValue()).append("/fingerprint_aps").toString();
        StringRequest FPAPstringRequest = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                contagem++;
                if (contagem == size) {
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
                jsonBody.put("id_fingerprint", id_fingerprint);
                jsonBody.put("id_ap",id_ap);
                return jsonBody;
            }
        };
        FPAPstringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(FPAPstringRequest);
    }
}

