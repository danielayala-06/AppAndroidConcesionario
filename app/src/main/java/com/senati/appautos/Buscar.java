package com.senati.appautos;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Buscar extends AppCompatActivity {
EditText edtId, edtMarca,edtModelo, edtColor, edtPlaca;
Button btnBuscarVehiculo, btnActualizarVehiculo, btnEliminarVehiculo;

private final String URL = "http://192.168.101.36:3000/vehiculos/"; // constante
RequestQueue requestQueue;
private void loadIU(){
    edtId = findViewById(R.id.edtIdEdit);
    edtMarca = findViewById(R.id.edtMarcaEdit);
    edtModelo = findViewById(R.id.edtModeloEdit);
    edtColor = findViewById(R.id.edtColorEdit);
    edtPlaca = findViewById(R.id.edtPlacaEdit);

    btnBuscarVehiculo = findViewById(R.id.btnBuscarVehiculo);
    btnActualizarVehiculo = findViewById(R.id.btnActualzar);
    btnEliminarVehiculo = findViewById(R.id.btnEliminar);
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadIU();
        btnBuscarVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serachById();
            }
        });
        btnActualizarVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUpdate();
            }
        });
        btnEliminarVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });
    }

    private void confirmDelete() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Mantenimiento de Vehiculos");
        dialog.setMessage("¿Desea eliminar el vehiculo "+edtMarca.getText().toString()+"?");
        dialog.setCancelable(false);
        dialog.setNegativeButton("Cancelar", null);
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteVehiculo();
            }
        });
        dialog.show();
    }
    private void confirmUpdate(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Mantenimiento de Vehiculos");
        dialog.setMessage("¿Procedemos con la actualización?");
        dialog.setCancelable(false);
        dialog.setNegativeButton("Cancelar", null);
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateVehicle();
                formClear();
            }
        });
        dialog.show();
    }
    private void updateVehicle() {
        //1.- Canal de comunicación
        requestQueue = Volley.newRequestQueue(this);
        //2.- JSON a enviar (BODY)
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("marca", edtMarca.getText().toString().trim());
            jsonObject.put("modelo", edtModelo.getText().toString().trim());
            jsonObject.put("color", edtColor.getText().toString().trim());
            jsonObject.put("placa", edtPlaca.getText().toString().trim());
        }catch (JSONException e){
            Log.e("Error en el JSON",e.toString());
        }
        //3.- Solicitud (Utlilizara el JSON del paso 2)
        String endPoint = URL + edtId.getText().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                endPoint,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Motivos:
                        // 1.- Problemas en el WS
                        // 2.- El ID enviado no existe
                        // 3.- La placa está repetida

                        Toast.makeText(getApplicationContext(), "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        //4.- Enviamos la solicitud
        requestQueue.add(jsonObjectRequest);
    }
    private void serachById() {
        String idVehiculo = edtId.getText().toString().trim();

        if(idVehiculo.isEmpty()){
            edtId.setError("Escriba el ID");
            edtId.requestFocus();
        }else{
            //1 .- Canal de comunicación
            requestQueue = Volley.newRequestQueue(this);
            String endPoint = URL+idVehiculo;

            //2 .- Solicitud
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    endPoint,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            //Log.d("Respuesta WS", jsonObject.toString());
                            try{
                                edtMarca.setText(jsonObject.getString("marca"));
                                edtModelo.setText(jsonObject.getString("modelo"));
                                edtColor.setText(jsonObject.getString("color"));
                                edtPlaca.setText(jsonObject.getString("placa"));
                            } catch (Exception e) {
                                Log.e("Error JSON", e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Log.e("Error WS", volleyError.toString());
                            formClear();
                            edtId.requestFocus();
                            Toast.makeText(getApplicationContext(), "No existe el vehiculo", Toast.LENGTH_LONG).show();
                        }
                    }
            );

            //3 .- Envio de la solicitud
            requestQueue.add(jsonObjectRequest);
        }
    }
    private void formClear(){
        edtMarca.setText(null);
        edtModelo.setText(null);
        edtColor.setText(null);
        edtPlaca.setText(null);
    }
    private void deleteVehiculo(){
        requestQueue = Volley.newRequestQueue(this);

        //Canal de comunicación
        String endPoint = URL + edtId.getText().toString();
        //JSONObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                endPoint,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        formClear();
                        Toast.makeText(getApplicationContext(), "Vehiculo eliminado correctamente", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"No se pudo eliminar el vehiculo", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}