package com.example.andre.aplicaocalibracao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
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

public class ListSpaces extends AppCompatActivity {
    Spinner SpinnerSpaceUsers;
    List<String> names = new ArrayList<String>();
    ArrayAdapter<String> spinnerArrayAdapter;
    Context mContext;
    Button buttonatualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        names.clear();
        setContentView(R.layout.activity_list_spaces);
        SpinnerSpaceUsers = (Spinner) findViewById(R.id.SpinnerSpaceUser);
        buttonatualizar = (Button)findViewById(R.id.buttonAtualizar);
        this.mContext = this;
        GetListOfSpaces(SpinnerSpaceUsers);
        buttonatualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salto4(SpinnerSpaceUsers);
            }
        });

    }

    public void onResume() {
        super.onResume();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            salto3();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void salto3(){
        Intent intent = new Intent(ListSpaces.this,Login_Activity.class);
        startActivity(intent);
    }
    public void salto4(Spinner spinnerSpaceUsers){
        names.clear();
        GetListOfSpaces(SpinnerSpaceUsers);
    }
    //O botão do cantinho i
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                // Code you want run when activity is clicked
                salto2();
                Toast.makeText(this, "Informação sobre o ponto de referência", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void salto2(){
        Intent intent = new Intent(ListSpaces.this,ListSpaces_RV_main.class);
        startActivity(intent);
    }

    public void GetListOfSpaces(final Spinner SpinnerSpaceUser) {
        //buscar id_user
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //ver token do utilizador
        String Token = preferences.getString("Token","");
        String bearer = "Bearer ";
        final String tokenpost = new StringBuilder().append(bearer).append(Token).toString();
        //buscar id do espaço
        final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
        //user id
        final String user_id = preferences.getString("User", "");
        //URL para o get
        final UniversalVariable urlprev = UniversalVariable.getInstance();
        String url = new StringBuilder().append(urlprev.getValue()).append("/users/").toString();
        String url2 = new StringBuilder().append(url).append(user_id).toString();
        String url3 = new StringBuilder().append(url2).append("/Spaces").toString();
        names.add("Lista de espacos");
        //Pedido get
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url3,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Aqui preciso fazer parse de toda data, names e id dos espacos que recebe de resposta
                        JsonParser parser = new JsonParser();
                        JsonArray nomes = parser.parse(response).getAsJsonObject().getAsJsonArray("data");
                        //textViewspace.setText(response);

                        for (int i = 0; i < nomes.size(); i++) {
                            JsonObject o = nomes.get(i).getAsJsonObject();
                            String id_space = o.get("id_space").getAsString();
                            String name = o.get("name").getAsString();
                            String Space_Description = o.get("description").getAsString();
                            String map_path = o.get("map_path").getAsString();
                            String map_width = o.get("map_width").getAsString();
                            String map_length = o.get("map_length").getAsString();
                            String id_user = o.get("id_user").getAsString();
                            spaces.putSpinner_ID_Name(id_space, name);
                            spaces.put_ID_Space_Name(id_space,name);
                            spaces.put_ID_Space_Description(id_space,Space_Description);
                            spaces.put_ID_Space_Comprimento(id_space,map_length);
                            spaces.put_ID_Space_Largura(id_space,map_width);
                            spaces.Put_ID_Space_Path(id_space,map_path);
                            spaces.Put_ID_User(id_space,id_user);
                            System.getProperty("line.separator");
                            names.add(name);

                        }
                        ConstructSpinner(SpinnerSpaceUser);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getApplicationContext(), "Não é possível obter os espaços deste utilizador", Toast.LENGTH_SHORT).show();

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
    public void ConstructSpinner (final Spinner SpinnerSpaceUser){
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerSpaceUser.setAdapter(spinnerArrayAdapter);
        //EM caso de selecao
        SpinnerSpaceUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String papaia = parent.getItemAtPosition(position).toString();
                if (papaia !="Lista de espacos") {
                    SpinnerSpaceUser.setOnItemSelectedListener(this);
                    String selectedspace = parent.getItemAtPosition(position).toString();
                    //ve qual é a selecao e manda fazer o put do novo ID no reference point
                    final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
                    spaces.Put_SelectedSpace(selectedspace);
                    salto();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Não foi possivel selecionar nada no Spinner" , Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void salto(){
        Intent intent = new Intent(ListSpaces.this,MainActivity.class);
        startActivity(intent);
    }
}
