package com.senati.appautos;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Listar extends AppCompatActivity {
    ListView lstVehiculos;
    private final String URL = "http://192.168.101.36:3000/vehiculos"; // constante

    RequestQueue requestQueue; //Cola de pedido


    private void loadIU(){
        lstVehiculos = findViewById(R.id.lstVehiculos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            //Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            //v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadIU();
        getData();  //Acceder a los datos del servicio - GET

    }

    private void getData() {
        // 1.- Habilitar el canal de comuniciacion
        requestQueue = Volley.newRequestQueue(this);// Usamos this y no getAplicationcontext() porque es una orden de alto nivel

        // 2.- Que tipo de dato espero obtener?
        //Opciones => Objeto JSON, Arrgelom text, Binario(.jpg) RPT: ARRAY JSON
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        Log.d("Datos recibidos:",jsonArray.toString());
                        renderData(jsonArray);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Error en WS: ", volleyError.toString());
                    }
                }
        );
        // 3.- Enviar la solicitud
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Este metodo renderiza los datos obtenidos por el webServices en el listView
     */
    private void renderData(JSONArray vehiculos) {
        try{
            ArrayAdapter arrayAdapter;
            ArrayList<String> listaVehiculos = new ArrayList<>();

            for(int i =0; i < vehiculos.length(); i++){
                JSONObject jsonObject = vehiculos.getJSONObject(i);
                listaVehiculos.add(jsonObject.getString("marca")+ " \t- " +jsonObject.getString("placa"));
            }

            //El adaptador recibe la lista y coloca cada elemento uno debajo de otro
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaVehiculos);
            lstVehiculos.setAdapter(arrayAdapter);

        } catch (Exception e) {
            Log.e("Error JSON recibido:", e.toString());
        }
    }

}