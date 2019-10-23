package com.example.andre.aplicacaoposicionamento;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SetLocalizacao extends AppCompatActivity {
    EditText editText_comprimento;
    EditText editText_largura;
    TextView textViewLarguraShow;
    TextView textViewComprimentoShow;
    Button set , press;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_localizacao);
        editText_comprimento = (EditText)findViewById(R.id.editTextcomprimentonew);
        editText_largura = (EditText)findViewById(R.id.editTextnewlargura);
        textViewLarguraShow = (TextView)findViewById(R.id.textViewlargurashow);
        textViewComprimentoShow = (TextView)findViewById(R.id.textViewcomprimentoshow);
        set = (Button) findViewById(R.id.buttonset);
        getlocalizacao();
        press = (Button)findViewById(R.id.buttonPressLocation);
        //caso pretenda selecionar no mapa a nova opção
        press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salto2();
            }
        });

        //quando clicar no botão "set"
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retirar valores x e y do edit text
                String larguranew = editText_largura.getText().toString();
                String comprimentonew = editText_comprimento.getText().toString();
                //espacos da imagem
                final Spaces.MapID_SpacesImpl spaces = Spaces.MapID_SpacesImpl.getInstance();
                String SelectedItem = spaces.GetSelectedSpace();
                String id_space = spaces.get_by_the_Name(SelectedItem);
                String Largura = spaces.get_ID_Space_Largura(id_space);
                String Comprimento = spaces.get_ID_Space_Comprimento(id_space);
                if (Integer.valueOf(larguranew)>(Integer.valueOf(Comprimento)*2)||Integer.valueOf(comprimentonew)>(Integer.valueOf(Largura)*2)){
                    salto3();
                }else {
                    Post_contribution(comprimentonew, larguranew);
                    //TENHO QUE GUARDAR EM SPACES E FAZER NEW INTENT
                    //Saber a fingerprint que adicionou
                    final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
                    int id_int = FP.getCount();
                    String id_anterior = String.valueOf(id_int - 1);
                    //Guardar o novo x e y
                    final ReferencePoint.MapID_RFImpl RP = ReferencePoint.MapID_RFImpl.getInstance();
                    RP.put_RF_X(id_anterior, comprimentonew);
                    RP.put_RF_Y(id_anterior, larguranew);
                    salto();
                }
            }
            });

    }
    public void salto3(){
        Intent intent = new Intent(SetLocalizacao.this,SetLocalizacao.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Valor excede dimensão da imagem", Toast.LENGTH_SHORT).show();

    }
    public void Post_contribution(String comprimentonew, String larguranew){
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //ver token do utilizador
        final String Token = preferences.getString("Token", "");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //URL do put para atualizar novos valores
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String url = urlprev.getValue()+"/contributions_history";
        //Novo comprimento e largura em inteiro
        int comp_new = Integer.valueOf(comprimentonew);
        int larg_new = Integer.valueOf(larguranew);
        //Valores atuais de comprimento e largura
        String xs = urlprev.Get_Localizacao_x();
        String ys = urlprev.Get_Localizacao_y();
        float x_f_old = Float.parseFloat(xs);
        float y_f_old = Float.parseFloat(ys);
        int x_old = (int)x_f_old;
        int y_old = (int)y_f_old;
        //Calcular offset
        int off_x = comp_new-x_old;
        int off_y = larg_new-y_old;
        final String offset_x = String.valueOf(off_x);
        final String offset_y = String.valueOf(off_y);
        //DATA ATUAL NO FORMATO PEDIDO
        final Fingerprint.MapID_FPImpl FP= Fingerprint.MapID_FPImpl.getInstance();
        SimpleDateFormat dateint = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final String date = dateint.format(new Date());
        //user id
        final String user_id = preferences.getString("User", "");
        //Preciso disto para saber o ID da fingerprint
        int id_int = FP.getCount();
        String id_anterior = String.valueOf(id_int - 1);
        //id_reference_point
        ReferencePoint.MapID_RFImpl rf = ReferencePoint.MapID_RFImpl.getInstance();
        final String rf_id = rf.getID(id_anterior);
        //inicio do post
        StringRequest putRequest = new StringRequest(Request.Method.POST, url,
        new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Toast.makeText(getApplicationContext(), "Atualizou o histórico de contribuições", Toast.LENGTH_SHORT).show();
                Log.d("Response", response);
            }
        }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                String jsonError = new String(networkResponse.data);
                Log.e("LOG_VOLLEY", error.toString());
                Toast.makeText(getApplicationContext(), "Não foi possivel adicionar a Contribuição", Toast.LENGTH_SHORT).show();
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
                params.put("date",date );
                params.put("coordx_offset",offset_x);
                params.put("coordy_offset",offset_y);
                params.put("id_reference_point",rf_id);
                params.put("id_user",user_id);

                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(putRequest);

    }

    public void salto(){
        Intent intent = new Intent(SetLocalizacao.this,SetRequest.class);
        startActivity(intent);
    }

    public void salto2()
    {
        Intent intent = new Intent(SetLocalizacao.this,PressNewLocalization.class);
        startActivity(intent);

    }


    public void getlocalizacao() {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //ver token do utilizador
        String Token = preferences.getString("Token", "");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //Preciso disto para saber o ID da fingerprint
        final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
        int id_int = FP.getCount();
        String id_anterior = String.valueOf(id_int - 1);
        String id_fingerprint = FP.getIDFP(id_anterior);
        //url do get
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String geturl = urlprev.getValue()+"/spaces/fingerprints/"+id_fingerprint+"/location";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, geturl,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject js2 = new JSONObject(response);
                        String data = js2.getString("data");
                        JSONObject insertx = new JSONObject(data);
                        String x = insertx.getString("x");
                        JSONObject inserty = new JSONObject(data);
                        String y = inserty.getString("y");
                        textViewComprimentoShow.setText(x);
                        textViewLarguraShow.setText(y);
                        //aqui vai retirar a largura e comprimento do espaço selecionado
                    } catch (JSONException e) {
                    }
                }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getApplicationContext(), "Erro ao obter a sua localização", Toast.LENGTH_SHORT).show();

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
    }

