package com.example.andre.aplicacaoposicionamento;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LocalizationRequest extends AppCompatActivity {
    Button edit_position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localization_request);
        edit_position = (Button) findViewById(R.id.editposition);
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String id_type_user = preferences.getString("UserType", "");
        if (id_type_user.equals("1") || id_type_user.equals("2")) {
            edit_position.setVisibility(View.VISIBLE);

        } else {
            edit_position.setVisibility(View.GONE);
        }
        edit_position.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocalizationRequest.this, SetLocalizacao.class);
                startActivity(intent);
            }
        });
        //comeca o get para obter a localizacao
        getlocalizacao();

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
        Intent intent = new Intent(LocalizationRequest.this,Space_list.class);
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
                        //Aqui preciso fazer parse de toda data, names e id dos espacos que recebe de resposta
                        Toast.makeText(getApplicationContext(), "A receber resposta do pedido da Localização", Toast.LENGTH_SHORT).show();
                        //textViewspace.setText(response);

                        try {
                            JSONObject js2 = new JSONObject(response);
                            String data = js2.getString("data");
                            JSONObject insertx = new JSONObject(data);
                            String x = insertx.getString("x");
                            JSONObject inserty = new JSONObject(data);
                            final UniversalVariable urlprev = UniversalVariable.getInstance();
                            urlprev.Put_Localizacao_x(x);
                            String y = inserty.getString("y");
                            urlprev.Put_Localizacao_y(y);

                            //aqui vai retirar a largura e comprimento do espaço selecionado
                            if(!x.equals("null"))
                                GetinfoImagem(x, y);
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

    public void GetinfoImagem(final String x, final String y) {
        //buscar id_user
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //ver token do utilizador
        String Token = preferences.getString("Token", "");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //buscar id do espaço
        final Spaces.MapID_SpacesImpl spaces = Spaces.MapID_SpacesImpl.getInstance();
        //user id
        final String user_id = preferences.getString("User", "");
        //URL para o get
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String url = new StringBuilder().append(urlprev.getValue()).append("/spaces/").toString();
        String SelectedItem = spaces.GetSelectedSpace();
        String id_space = spaces.get_by_the_Name(SelectedItem);

        String url2 = new StringBuilder().append(url).append(id_space).toString();

        //Pedido get

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Aqui preciso fazer parse de toda data, names e id dos espacos que recebe de resposta
                        JsonParser parser = new JsonParser();
                        JsonArray nomes = parser.parse(response).getAsJsonObject().getAsJsonArray("data");
                        Toast.makeText(getApplicationContext(), "A buscar resposta do pedido", Toast.LENGTH_SHORT).show();
                        //textViewspace.setText(response);

                        for (int i = 0; i < nomes.size(); i++) {
                            JsonObject o = nomes.get(i).getAsJsonObject();
                            String id_space = o.get("id_space").getAsString();
                            String name = o.get("name").getAsString();
                            String Space_Description = o.get("description").getAsString();
                            String map_path = o.get("map_path").getAsString();
                            String map_width = o.get("map_width").getAsString();
                            String map_length = o.get("map_length").getAsString();
                            spaces.putSpinner_ID_Name(id_space, name);
                            spaces.put_ID_Space_Description(id_space, Space_Description);
                            spaces.put_ID_Space_Comprimento(id_space, map_length);
                            spaces.put_ID_Space_Largura(id_space, map_width);
                            spaces.Put_ID_Space_Path(id_space, map_path);
                            System.getProperty("line.separator");

                        }
                        imageofspacerequest(x, y);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getApplicationContext(), "Não é possível obter os espaços deste utilizador", Toast.LENGTH_SHORT).show();

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

    public void imageofspacerequest(final String x, final String y) {
        //espacos
        final Spaces.MapID_SpacesImpl spaces = Spaces.MapID_SpacesImpl.getInstance();
        String SelectedItem = spaces.GetSelectedSpace();
        String id_space = spaces.get_by_the_Name(SelectedItem);
        String Largura = spaces.get_ID_Space_Largura(id_space);
        String Comprimento = spaces.get_ID_Space_Comprimento(id_space);
        int largura = Integer.parseInt(Largura);
        int comprimento = Integer.parseInt(Comprimento);
        String SpaceName = spaces.get_id_Space_Name(id_space);
        SpaceName = SpaceName.replace(" ", "+");
        //final String urlfinal = "https://dummyimage.com/" + largura*2 + "x" + comprimento*2+ "/000/fff.png&text=" + SpaceName;
        final String urlfinal = "http://fpoimg.com/" + largura*2+"x"+comprimento*2+"?text="+SpaceName;

        final PinView imageViewLocal = findViewById(R.id.imageViewLocal);
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
                imageViewLocal.setImage(ImageSource.bitmap((Bitmap) o));
                imageViewLocal.setPin(new PointF(Float.parseFloat(x)*2, Float.parseFloat(y)*2));
            }
        };
        download.execute();
    }
}


