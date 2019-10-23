package com.example.andre.aplicaocalibracao;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
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

public class ListSpaces_RV_main extends AppCompatActivity {
    private TextView filho;
    private RecyclerView recyclerView;
    private ArrayList<ListSpaces_RV_Pai_class> listSpaces_RV_Pai_class;
    private ListSpaces_RV_myadapter adapter;
    List<String> names = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv_main);

        GetListOfSpaces();
   }

    //O botão do cantinho i
    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu2);
        return super.onCreateOptionsMenu(menu2);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.backarrow:
                // Code you want run when activity is clicked
                salto2();
                Toast.makeText(this, "Informação sobre o ponto de referência", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void salto2(){
        Intent intent = new Intent(ListSpaces_RV_main.this,ListSpaces.class);
        startActivity(intent);
    }
    public void GetListOfSpaces(  ) {
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
                        Toast.makeText(getApplicationContext(), "A buscar resposta do pedido", Toast.LENGTH_SHORT).show();
                        //textViewspace.setText(response);
                        spaces.Put_NumberOfSpaces(nomes.size());
                        for (int i = 0; i < nomes.size(); i++) {
                            JsonObject o = nomes.get(i).getAsJsonObject();
                            String id_space = o.get("id_space").getAsString();
                            String name = o.get("name").getAsString();
                            String Space_Description = o.get("description").getAsString();
                            String map_path = o.get("map_path").getAsString();
                            String map_width = o.get("map_width").getAsString();
                            String map_length = o.get("map_length").getAsString();
                            String id_user = o.get("id_user").getAsString();
                            spaces.putSpinner_ID_Name(String.valueOf(i), name);
                            spaces.put_ID_Space_Name(String.valueOf(i),name);
                            spaces.put_ID_Space_Description(String.valueOf(i),Space_Description);
                            spaces.put_ID_Space_Comprimento(String.valueOf(i),map_length);
                            spaces.put_ID_Space_Largura(String.valueOf(i),map_width);
                            spaces.Put_ID_Space_Path(String.valueOf(i),map_path);
                            spaces.Put_ID_User(String.valueOf(i),id_user);
                            System.getProperty("line.separator");
                            names.add(name);

                        }
                        initVars();
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }
public void initVars(){
    filho = (TextView) findViewById(R.id.filho);

    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    listSpaces_RV_Pai_class = new ArrayList<>();

    setData();
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new ListSpaces_RV_myadapter(this, listSpaces_RV_Pai_class);
    recyclerView.setAdapter(adapter);

}
    private void setData() {
        final Spaces.MapID_SpacesImpl spaces= Spaces.MapID_SpacesImpl.getInstance();
        int Size= spaces.Get_NumberOfSpaces();
        for(int j=0;j<Size;j++){
            ArrayList<ListSpaces_RV_Filho_class> filho = new ArrayList<>();
            String name = spaces.get_id_Space_Name(String.valueOf(j));
            String Description="Descrição:"+spaces.Get_Space_Description_By_Name(String.valueOf(j));
            String largura= "Largura:"+spaces.get_ID_Space_Largura(String.valueOf(j));
            String comprimento="Comprimento:"+spaces.get_ID_Space_Comprimento(String.valueOf(j));
            filho.add(new ListSpaces_RV_Filho_class(Description +"\n"+largura+"\n"+ comprimento));
            listSpaces_RV_Pai_class.add(new ListSpaces_RV_Pai_class(name,filho));
        }

    }
}


