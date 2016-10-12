package com.tfm.sporting.sporting;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private Button btnLogin;

    private EditText etEmail;
    private EditText etPass;
    String prueba = "HOLA";


    private TextView tvNoRegistrado;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);

        tvNoRegistrado = (TextView) findViewById(R.id.tvNoRegistrado);

        //Tarea boton Login
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginTask tarea = new LoginTask();
                tarea.execute(
                        etEmail.getText().toString(),
                        etPass.getText().toString());
            }
        });

        //Tarea registro
        tvNoRegistrado.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent ActividadRegistro = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(ActividadRegistro);
            }
        });
    }

    private class LoginTask extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://192.168.0.103/Reg/RegistroUser");
            post.setHeader("content-type", "application/json");

            try
            {
                //Construimos el objeto cliente en formato JSON
                JSONObject dato = new JSONObject();

                //dato.put("Id", Integer.parseInt(txtId.getText().toString()));
                //dato.put("Nuser", (params[0]));
                dato.put("Nemail", params[0]);
                dato.put("Npass", (params[1]));

                StringEntity entity = new StringEntity(dato.toString());
                post.setEntity(entity);

                HttpResponse resp = httpClient.execute(post);
                String respStr = EntityUtils.toString(resp.getEntity());

                if(!respStr.equals("true"))
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
                        "Bienvenido de nuevo", Toast.LENGTH_SHORT);

                toastLogin.show();

                // -> Actividad logueado !
                Intent ActividadSporting = new Intent(getApplicationContext(), Sporting.class);
                startActivity(ActividadSporting);
            }
            else{
                toastLogin = Toast.makeText(getApplicationContext(),
                        "Error: Datos Incorrectos", Toast.LENGTH_SHORT);

                toastLogin.show();
            }
        }
    }
}