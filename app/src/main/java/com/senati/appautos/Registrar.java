package com.senati.appautos;

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
import androidx.core.view.ViewCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Registrar extends AppCompatActivity {
    EditText edtMarca, edtModelo, edtColor, edtPlaca;
    Button btnGuardar;

    private final String URL = "http://192.168.101.36:3000/vehiculos"; // constante
    RequestQueue requestQueue; //Cola de pedido
    private void loadIU(){
        edtMarca = findViewById(R.id.edtMarca);
        edtModelo = findViewById(R.id.edtModelo);
        edtColor = findViewById(R.id.edtColor);
        edtPlaca = findViewById(R.id.edtPlaca);

        btnGuardar = findViewById(R.id.btnGuardar);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            //Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            //v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadIU();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formIsReady()){
                    showConfirmSave();
                }else{
                    Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showConfirmSave() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Registro Vehicular");
        dialog.setMessage("Estas seguro de registrar el vehiculo?");
        dialog.setCancelable(false);
        dialog.setNegativeButton("Cancelar", null);
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                senDataWS();
            }
        });
        dialog.show();
    }

    private boolean formIsReady() {
        //Validación de los campos que son obligatorios.
        if(     !editTextValidate(edtMarca) ||
                !editTextValidate(edtPlaca) ||
                !editTextValidate(edtModelo) ||
                !editTextValidate(edtColor, "Requerido por MTC"))
        {
                return false;
        }
        //Se validio correctamente.
        return true;
    }

    /**
     * Evalua si una caja de texto contiene datos, se requiere mensaje de error personalizado.
     * @param editText
     * @param message
     * @return
     */
    private boolean editTextValidate(EditText editText, String message){
        if(editText.getText().toString().trim().isEmpty()){
            editText.setError(message);
            editText.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Evalua si una caja de texto tiene datos.
     * @param editText
     * @return
     */
    private boolean editTextValidate(EditText editText){
        if(editText.getText().toString().trim().isEmpty()){
            editText.setError("Obligatorio");
            editText.requestFocus();
            return false;
        }
        return true;
    }

    private void senDataWS() {
        //1.- Habilitar el servicio
        requestQueue = Volley.newRequestQueue(this);

        //1.5.- Para que este ENDPOINT /POST funcione debemos preparar un JSON con los datos
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("marca", edtMarca.getText().toString());
            jsonObject.put("modelo", edtModelo.getText().toString());
            jsonObject.put("color", edtColor.getText().toString());
            jsonObject.put("placa", edtPlaca.getText().toString());

        } catch (Exception e) {
            Log.e("Error JSON envio", e.toString());
        }

        //2.- Tipp de dato obtenido y párametros
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, //Verbo
                URL, //ENDPOINT
                jsonObject, //JSON
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("Id Obtenido", jsonObject.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Error al enviar el json", volleyError.toString());
                    }
                }
        );

        //3.- Envio
        requestQueue.add(jsonObjectRequest);

    }
}