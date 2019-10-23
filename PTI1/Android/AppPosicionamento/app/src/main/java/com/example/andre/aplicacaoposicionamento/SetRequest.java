package com.example.andre.aplicacaoposicionamento;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.davemorrissey.labs.subscaleview.ImageSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SetRequest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_request);
        PostNewLocalization();
        RepresentarEspaco();
    }

    public void PostNewLocalization(){
        //buscar id_user
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //ver token do utilizador
        final String Token = preferences.getString("Token","");
        String bearer = "Bearer "+Token;
        //Buscar id do hashmap para aceder ao hashmap do RF
        final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
        int id_int = FP.getCount();
        String id_anterior = String.valueOf(id_int - 1);
        //buscar informacao do reference point com esse id em questao
        final ReferencePoint.MapID_RFImpl RP = ReferencePoint.MapID_RFImpl.getInstance();
        final String id_rf = RP.getID(id_anterior);
        final String name_rf = RP.get_RF_Name(id_anterior);
        final String comprimento_new = RP.get_RF_X(id_anterior);
        final String largura_new = RP.get_RF_Y(id_anterior);
        final String isOffline_new = "1";
        RP.put_RF_ISOffline(id_anterior, isOffline_new);
        //buscar id do espaco
        final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
        String SelectedItem = spaces.GetSelectedSpace();
        final String id_space = spaces.get_by_the_Name(SelectedItem);
        //URL do put para atualizar novos valores
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String RFputurl = urlprev+"/reference_points/"+id_rf;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, RFputurl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(getApplicationContext(), "Atualizou as novas coordenadas", Toast.LENGTH_SHORT).show();
                        Log.d("Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.e("LOG_VOLLEY", error.toString());
                    Toast.makeText(getApplicationContext(), jsonError, Toast.LENGTH_SHORT).show();
                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", Token);
                return params;
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name",name_rf );
                params.put("coordx",comprimento_new);
                params.put("coordy",largura_new);
                params.put("isOffline",isOffline_new);
                params.put("id_space",id_space);

                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(putRequest);
    }

    public void RepresentarEspaco(){

//espacos da imagem
        final Spaces.MapID_SpacesImpl spaces = Spaces.MapID_SpacesImpl.getInstance();
        String SelectedItem = spaces.GetSelectedSpace();
        String id_space = spaces.get_by_the_Name(SelectedItem);
        String Largura = spaces.get_ID_Space_Largura(id_space);
        String Comprimento = spaces.get_ID_Space_Comprimento(id_space);
        final ReferencePoint.MapID_RFImpl RP = ReferencePoint.MapID_RFImpl.getInstance();
        //Buscar id do hashmap para aceder ao hashmap do RF
        final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
        int id_int = FP.getCount();
        String id_anterior = String.valueOf(id_int - 1);
        //buscas x e y da localização
        String x_string = RP.get_RF_X(id_anterior);
        String y_string = RP.get_RF_Y(id_anterior);
        final Float x = Float.parseFloat(x_string);
        final Float y = Float.parseFloat(y_string);
        final int largura = Integer.parseInt(Largura)*2;
        final int comprimento = Integer.parseInt(Comprimento)*2;
        //Nome do Espaço para o Link
        String SpaceName = spaces.get_id_Space_Name(id_space);
        SpaceName = SpaceName.replace(" ", "+");
        //Url da Imagem
        //final String urlfinal = "https://dummyimage.com/"+largura+"x"+comprimento+ "/000/fff.png&text=" + SpaceName;
        final String urlfinal = "http://fpoimg.com/" + largura+"x"+comprimento+"?text="+SpaceName;
        final PinView imageViewSetRequest = findViewById(R.id.imageViewSetRequest);
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
                imageViewSetRequest.setImage(ImageSource.bitmap((Bitmap) o));
                imageViewSetRequest.setPin(new PointF(x, y));
            }
        };
        download.execute();
    }
}
