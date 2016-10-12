package com.tfm.sporting.sporting;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class RegistroActivity extends Activity {

    private Button btnRegistro;

    private EditText etNombre;
    private EditText etEmail;
    private EditText etPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        btnRegistro = (Button) findViewById(R.id.btnRegistro);

        etNombre = (EditText) findViewById(R.id.etNombre);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);


        //Tarea boton Login
        btnRegistro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RegistroTask tarea = new RegistroTask();
                tarea.execute(
                        etNombre.getText().toString(),
                        etEmail.getText().toString(),
                        etPass.getText().toString());
            }
        });

    }

    //Tarea Asíncrona para crear usuario
    private class RegistroTask extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://192.168.0.103/Reg/Create");
            post.setHeader("content-type", "application/json");

            try
            {
                //Construimos el objeto cliente en formato JSON
                JSONObject dato = new JSONObject();

                //dato.put("Id", Integer.parseInt(txtId.getText().toString()));
                dato.put("Nuser", (params[0]));
                dato.put("Nemail", params[1]);
                dato.put("Npass", (params[2]));

                StringEntity entity = new StringEntity(dato.toString());
                post.setEntity(entity);

                HttpResponse resp = httpClient.execute(post);
                String respStr = EntityUtils.toString(resp.getEntity());
                if(!respStr.equals("\"HAY\""))
                    resul = false;
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {
            Toast toastLogin;

            if (result)
            {
                toastLogin = Toast.makeText(getApplicationContext(),
                        "Error: Datos Incorrectos", Toast.LENGTH_SHORT);

                toastLogin.show();

                Intent ActividadMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(ActividadMain);
            }
            else{
                toastLogin = Toast.makeText(getApplicationContext(),
                        "Registrado", Toast.LENGTH_SHORT);

                toastLogin.show();
                Intent ActividadMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(ActividadMain);
            }
        }
    }
}