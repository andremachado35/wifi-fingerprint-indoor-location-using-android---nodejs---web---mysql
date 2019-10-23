package com.example.andre.aplicacaoposicionamento;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PressNewLocalization extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_press_new_localization);
        fora();
    }

    public void fora (){
        final PinView imageViewPressLocalization = findViewById(R.id.imageViewPressLocalization);

        //Carregar imagem do espaço
        final Spaces.MapID_SpacesImpl spaces = Spaces.MapID_SpacesImpl.getInstance();
        String SelectedItem = spaces.GetSelectedSpace();
        String id_space = spaces.get_by_the_Name(SelectedItem);
        String Largura = spaces.get_ID_Space_Largura(id_space);
        String Comprimento = spaces.get_ID_Space_Comprimento(id_space);
        String SpaceName = spaces.get_id_Space_Name(id_space);
        SpaceName = SpaceName.replace(" ", "+");
        final int largura = Integer.parseInt(Largura) * 2;
        final int comprimento = Integer.parseInt(Comprimento) * 2;

        //final String urlfinal = "https://dummyimage.com/" + largura + "x" + comprimento + "/000/fff.png&text=" + SpaceName;
        final String urlfinal = "http://fpoimg.com/" + largura+"x"+comprimento+"?text="+SpaceName;

        //apresentar imagem
        AsyncTask download = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    return BitmapFactory.decodeStream((InputStream) new URL(urlfinal).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                imageViewPressLocalization.setImage(ImageSource.bitmap((Bitmap) o));
                //TODO: mostrar pontos de referencia já existentes
            }
        };
        download.execute();
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imageViewPressLocalization.isReady()) {
                    final PointF sCoord = imageViewPressLocalization.viewToSourceCoord(e.getX(), e.getY());
                    if (sCoord.y > 0 && sCoord.y <= comprimento && sCoord.x > 0 && sCoord.x <= largura) {
                        imageViewPressLocalization.setPin(sCoord);


                        //Mensagem de confirmação
                        final AlertDialog.Builder builder = new AlertDialog.Builder(PressNewLocalization.this);
                        builder.setTitle("Confirm");
                        builder.setMessage("Tem a certeza que pretende calibrar em x:" + Math.round(sCoord.x / 2) + " y: " + Math.round(sCoord.y / 2) + "?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Saber a fingerprint que adicionou
                                final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
                                int id_int = FP.getCount();
                                String id_anterior = String.valueOf(id_int - 1);
                                final ReferencePoint.MapID_RFImpl RP = ReferencePoint.MapID_RFImpl.getInstance();
                                RP.put_RF_X(id_anterior, Integer.toString(Math.round(sCoord.x / 2)));
                                RP.put_RF_Y(id_anterior, Integer.toString(Math.round(sCoord.y / 2)));
                                String scoordx = Float.toString(sCoord.x);
                                String scoordy = Float.toString(sCoord.y);
                                Post_contribution(scoordx,scoordy);
                            }

                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
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


        imageViewPressLocalization.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    public void Post_contribution(String comprimentonew, String larguranew) {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //ver token do utilizador
        final String Token = preferences.getString("Token", "");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //URL do put para atualizar novos valores
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String url = urlprev.getValue() + "/contributions_history";
        //Novo comprimento e largura em inteiro
        float comp_new = Float.parseFloat(comprimentonew);
        float larg_new = Float.parseFloat(larguranew);

        //Valores atuais de comprimento e largura
        String xs = urlprev.Get_Localizacao_x();
        String ys = urlprev.Get_Localizacao_y();
        float x_f_old = Float.parseFloat(xs);
        float y_f_old = Float.parseFloat(ys);
        //Calcular offset
        float off_x = comp_new - x_f_old;
        float off_y = larg_new - y_f_old;
        final String offset_x = String.valueOf(off_x);
        final String offset_y = String.valueOf(off_y);
        //DATA ATUAL NO FORMATO PEDIDO
        final Fingerprint.MapID_FPImpl FP = Fingerprint.MapID_FPImpl.getInstance();
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
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(getApplicationContext(), "Atualizou o histórico de contribuições", Toast.LENGTH_SHORT).show();
                        Log.d("Response", response);
                        salto();
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("date", date);
                params.put("coordx_offset", offset_x);
                params.put("coordy_offset", offset_y);
                params.put("id_reference_point", rf_id);
                params.put("id_user", user_id);

                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(putRequest);
    }
    public void salto(){
        Intent intent = new Intent(PressNewLocalization.this,SetRequestforPressnew.class);
        startActivity(intent);
    }
}
