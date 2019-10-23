package com.example.andre.aplicaocalibracao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class InfoPI extends AppCompatActivity {
    private PinView2 imageViewPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pi);
        imageViewPI = (PinView2)findViewById(R.id.imageViewPI);

        /*************************************
         * Função responsável por inserir o nome do ponto de interesse
         *1-Buscar Pontos de Interesse neste espaço selecionado pelo utilizador
         *2-Representar imagem do espaço selecionado
         *3-Percorre esses pontos de interesse e coloca pino
         */

        Getpi();
    }
    public void onResume() {
        super.onResume();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            salto2();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void salto2(){
        Intent intent = new Intent(InfoPI.this,MainActivity.class);
        startActivity(intent);
    }
    //O botão do cantinho +
    @Override
    public boolean onCreateOptionsMenu(Menu menu3) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu3, menu3);
        return super.onCreateOptionsMenu(menu3);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item3) {
        // Handle presses on the action bar items
        switch (item3.getItemId()) {
            case R.id.plus:
                // Code you want run when activity is clicked
                salto4();
                Toast.makeText(this, "Adicionar Ponto de Interesse", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item3);
        }
    }
    public void salto4(){
        Intent intent = new Intent(InfoPI.this,AddPI.class);
        startActivity(intent);
    }

    //1-Buscar Pontos de Interesse neste espaço selecionado pelo utilizador
    public void Getpi(){
        //buscar id_user
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //ver token do utilizador
        String Token = preferences.getString("Token","");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //
        final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
        String SelectedItem = spaces.GetSelectedSpace();
        String SelectedID= spaces.get_by_the_Name(SelectedItem);
        //Url do get
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String GetUrl = new StringBuilder().append(urlprev.getValue()).append("/spaces/").toString();
        String GetUrl2 = new StringBuilder().append(GetUrl).append(SelectedID).toString();
        String GetUrlTotal = new StringBuilder().append(GetUrl2).append("/interest_points").toString();



        StringRequest stringRequest = new StringRequest(Request.Method.GET, GetUrlTotal,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Aqui preciso fazer parse de toda data, names e id dos espacos que recebe de resposta
                        JsonParser parser = new JsonParser();
                        JsonArray nomes = parser.parse(response).getAsJsonObject().getAsJsonArray("data");
                        Toast.makeText(getApplicationContext(), "A receber resposta dos Pontos de Interesse", Toast.LENGTH_SHORT).show();
                        //textViewspace.setText(response);
                        //Guardar numero de PI
                        PontosDeInteresse.Map_ID_PIImpl pi= PontosDeInteresse.Map_ID_PIImpl.getInstance();
                        int number_of_spaces = nomes.size();
                        pi.Put_NumberOfSpaces(number_of_spaces);

                        for (int i = 0; i < nomes.size(); i++) {
                            JsonObject o = nomes.get(i).getAsJsonObject();
                            //Guardar informação dos pontos de interesse
                            String id_interest_point = o.get("id_interest_point").getAsString();
                            String name = o.get("name").getAsString();
                            String coordx = o.get("coordx").getAsString();
                            String coordy = o.get("coordy").getAsString();
                            pi.put_PI_Coordx(String.valueOf(i),coordx);
                            pi.put_PI_Coordy(String.valueOf(i),coordy);
                            pi.put_PI_Name(String.valueOf(i),name);
                        }

                        RepresentarImagem();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getApplicationContext(), "Não contém nenhum Ponto de Interesse para este espaço", Toast.LENGTH_SHORT).show();

                    }
                }){
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


//2-Representar imagem
    public void RepresentarImagem(){
        //aqui tenciono buscar largura e comprimento do espaço
        //Depois tenciono carregar a imagem
        //definir marca por cada ponto
        //espacos
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
        //Url da Imagem
        //final String url="https://dummyimage.com/"+largura+"x"+comprimento+"/000/fff.png&text="+SpaceName;
        final String url = "http://fpoimg.com/" + largura+"x"+comprimento+"?text="+SpaceName;
        /*************************************
         * Função responsável por inserir o nome do ponto de interesse
         *
         */
        AsyncTask download = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    return BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                imageViewPI.setImage(ImageSource.bitmap((Bitmap) o));   //Buscar Numero de espacos PI :
                PontosDeInteresse.Map_ID_PIImpl pi= PontosDeInteresse.Map_ID_PIImpl.getInstance();
                int number_of_pi = pi.GetSelectedSpace();
                for (int j = 0 ; j<number_of_pi;j++) {
                    String x = pi.get_PI_Coordx_by_ID(String.valueOf(j));
                    String y = pi.get_PI_Coordy_by_ID(String.valueOf(j));
                    final String name = pi.get_PI_Name_by_ID(String.valueOf(j));
                    Float xconveted = Float.parseFloat(x);
                    Float yconverted = Float.parseFloat(y);
                    final PointF sCoord = new PointF(xconveted*2,yconverted*2);
                    imageViewPI.setPin(sCoord, true,name);

                }

            }
        };
        download.execute();
    }
}

