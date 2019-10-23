package com.example.andre.aplicacaoposicionamento;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Space_list extends AppCompatActivity  {
    List<String> names = new ArrayList<String>();
    Context mContext;
    ArrayAdapter<String> spinnerArrayAdapter;
    Button buttonatualizar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_list);
        buttonatualizar = (Button)findViewById(R.id.buttonatualizar);

        names.clear();
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerspace);
            this.mContext = this;
        getListadeespaco(spinner);
        buttonatualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salto4(spinner);
            }
        });
    }
    public void salto4(Spinner spinner){
        names.clear();
        getListadeespaco(spinner);

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
        Intent intent = new Intent(Space_list.this,Scanwifi.class);
        startActivity(intent);
    }


    private void getListadeespaco(final Spinner spinner) {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //ver token do utilizador
        String Token = preferences.getString("Token","");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        // Preciso disto para saber a fingerprint que adicionou
        final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
        //Para guardar id dos spinners + nomes para depois apartir do nome saber o id para o put
        final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
        //Saber a fingerprint que adicionou
        int id_int = FP.getCount();
        final String id_anterior = String.valueOf(id_int - 1);
        String id_fingerprint = FP.getIDFP(id_anterior);
        //meter no arraylist o nome do Spinner
        names.add("Lista de espacos");
        //URL para o get
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String geturl = urlprev.getValue()+"/spaces/fingerprints/"+id_fingerprint;
        //inicio do pedido get
        StringRequest  stringRequest = new StringRequest (Request.Method.GET, geturl,
                new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    //Aqui preciso fazer parse de toda data, names e id dos espacos que recebe de resposta
                    JsonParser parser = new JsonParser();
                    JsonArray nomes = parser.parse(response).getAsJsonObject().getAsJsonArray("data");
                    Toast.makeText(getApplicationContext(), "A receber resposta da lista de espaços ", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < nomes.size(); i++) {
                        JsonObject o = nomes.get(i).getAsJsonObject();
                        String id_space=o.get("id_space").getAsString();
                        String name = o.get("name").getAsString();

                        System.getProperty("line.separator");
                        spaces.putSpinner_ID_Name(id_space,name);
                        spaces.put_ID_Space_Name(id_space,name);
                        names.add(name);
                    }
                    funcao(spinner);
                }


                },
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error

                    Toast.makeText(getApplicationContext(), "Erro ao receber a lista de espaços para seleção"+error.toString(), Toast.LENGTH_SHORT).show();

                }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", tokenpost);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void funcao (final Spinner spinner) {
        //coloca opçoes da resposta do get no spinner
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        //EM caso de selecao
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String papaia = parent.getItemAtPosition(position).toString();
                if (papaia !="Lista de espacos") {
                    final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
                    Toast.makeText(getApplicationContext(), "Selecionou Opção do Spinner", Toast.LENGTH_SHORT).show();
                    spinner.setOnItemSelectedListener(this);
                    String selectedspace = parent.getItemAtPosition(position).toString();
                    spaces.Put_SelectedSpace(selectedspace);
                    //ve qual é a selecao e manda fazer o put do novo ID no reference point
                    atualizar (selectedspace);
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "A opção selecionada não é válida", Toast.LENGTH_SHORT).show();

            }
        });
    }
public void atualizar (String selectedspace){
    //ver token do utilizador e user id
    SharedPreferences preferences = getSharedPreferences("myPrefs",MODE_PRIVATE);
    String Token = preferences.getString("Token","");
    final String user_id = preferences.getString("User","");
    String bearer = "Bearer ";
    final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //Preciso disto para saber o ID do referencePoint
    final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
    int id_int = FP.getCount();
    String id_anterior = String.valueOf(id_int - 1);
    //buscar informacao do reference point
    final ReferencePoint.MapID_RFImpl RP = ReferencePoint.MapID_RFImpl.getInstance();
    final String id_rf = RP.getID(id_anterior);
    final String name_rf = RP.get_RF_Name(id_anterior);
    final String coordx = RP.get_RF_X(id_anterior);
    final String coordy = RP.get_RF_Y(id_anterior);
    final String isOffline =RP.get_RF_Isoffline(id_anterior);
    //buscar id do espaco
    final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
    final String id_space = spaces.get_by_the_Name(selectedspace);
    //url do put
    final UniversalVariable urlprev = UniversalVariable.getInstance();
    String url = new StringBuilder().append(urlprev.getValue()).append("/reference_points/").toString();
    String RFputurl = new StringBuilder().append(url).append(id_rf).toString();

    StringRequest putRequest = new StringRequest(Request.Method.PUT, RFputurl,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    // response
                    salto();
                    Log.d("Response", response);
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                String jsonError = new String(networkResponse.data);
                Toast.makeText(getApplicationContext(), "Tente calibrar de novo", Toast.LENGTH_SHORT).show();

                Log.e("LOG_VOLLEY", error.toString());
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
        protected Map<String, String> getParams()
        {
            Map<String, String> params = new HashMap<String, String>();
            params.put("name",name_rf );
            params.put("coordx",coordx);
            params.put("coordy",coordy);
            params.put("isOffline",isOffline);
            params.put("id_space",id_space);

            return params;
        }

    };
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(putRequest);
}
    public void salto(){
        Intent intent = new Intent(Space_list.this,LocalizationRequest.class);
        startActivity(intent);
    }
    }


