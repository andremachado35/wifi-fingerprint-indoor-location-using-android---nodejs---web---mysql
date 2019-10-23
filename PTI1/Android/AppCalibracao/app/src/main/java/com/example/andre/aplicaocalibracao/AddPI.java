package com.example.andre.aplicaocalibracao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Map;

public class AddPI extends AppCompatActivity {
    Button confirmar;
    Button addselected;
    EditText Nome, Coordx, Coordy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pi);
        confirmar = (Button) findViewById(R.id.buttonconfirmar);
        addselected = (Button) findViewById(R.id.buttonselectmap);
        Nome=(EditText)findViewById(R.id.EditTextNome);
        Coordx=(EditText)findViewById(R.id.EditTextCoordx);
        Coordy=(EditText)findViewById(R.id.EditTextCoordy);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPI();
            }
        });

        addselected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salto2();
            }
        });

    }
    public void salto2() {
        Intent intent = new Intent(AddPI.this,AddPI_SelectOnMap.class);
        startActivity(intent);
    }


    public void postPI() {
        //Token
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String Token = preferences.getString("Token", "");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //Campos
        float x = (Float.valueOf(Coordx.getText().toString())) / 2;
        float y = (Float.valueOf(Coordy.getText().toString())) / 2;
        final String Nome_Pi = Nome.getText().toString();
        final String Coordxs = String.valueOf(x);
        final String Coordys = String.valueOf(y);


        //id_space
        final Spaces.MapID_SpacesImpl spaces = Spaces.MapID_SpacesImpl.getInstance();
        String SelectedItem = spaces.GetSelectedSpace();
        final String id_space = spaces.get_by_the_Name(SelectedItem);
        String Largura = spaces.get_ID_Space_Largura(id_space);
        String Comprimento = spaces.get_ID_Space_Comprimento(id_space);
        if ((int) x < 0|| (int)x ==0||(int)y <0 || (int)y ==0 || (int) x > (Integer.valueOf(Largura)) || (int) y > (Integer.valueOf(Comprimento))) {
            salto3();
        } else {
            final UniversalVariable urlprev = UniversalVariable.getInstance();
            String posturl = new StringBuilder().append(urlprev.getValue()).append("/interest_points").toString();
            StringRequest RFstringRequest = new StringRequest(Request.Method.POST, posturl, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        //Ver id do reference point
                        JSONObject js2 = new JSONObject(response);
                        String data = js2.getString("data");
                        JSONObject insert = new JSONObject(data);
                        String id_reference_point = insert.getString("insertId");
                        Toast.makeText(getApplicationContext(), "Adicionou com sucesso o Ponto de Interesse" + id_reference_point, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), insertId, Toast.LENGTH_SHORT).show();
                        salto();
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
                        Toast.makeText(getApplicationContext(), "Campos introduzidos são inválidos", Toast.LENGTH_SHORT).show();
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
                    jsonBody.put("name", Nome_Pi);
                    jsonBody.put("coordx", Coordxs);
                    jsonBody.put("coordy", Coordys);
                    jsonBody.put("id_space", id_space);
                    return jsonBody;
                }
            };
            RFstringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(RFstringRequest);
        }
    }

    public void salto() {
        Intent intent = new Intent(AddPI.this,InfoPI.class);
        startActivity(intent);
    }

    public void salto3(){
        Intent intent = new Intent(AddPI.this,AddPI.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Valor excede dimensão da imagem", Toast.LENGTH_SHORT).show();
    }
}
