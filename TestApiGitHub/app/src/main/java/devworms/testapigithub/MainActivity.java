package devworms.testapigithub;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ProgressDialog pDialog;
    EditText edtBusca;
    Button btnBusca;
    ListView lista;
    String[] name,imagen,descripcion;
    ArrayAdapter <String> adaptador;
    String respuesta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtBusca = (EditText)findViewById(R.id.edtBusca);
        btnBusca =(Button)findViewById(R.id.btnBusca);
        lista= (ListView)findViewById(R.id.lista);
        adaptador= new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        btnBusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaptador= new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_expandable_list_item_1);
                new getConsultaApi().execute();
            }
        });
    }

    class getConsultaApi extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Buscando información");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Albums JSON
         */
        protected String doInBackground(String... args) {

           String body = "https://api.github.com/search/repositories?q=language%3A"+ edtBusca.getText().toString() +"&sort=stars";


            JSONParser jsp = new JSONParser();
            respuesta = jsp.makeHttpRequest(body, "GET", body, "");
            if (respuesta != "error") {
                try {

                    JSONObject json = new JSONObject(respuesta);
                    String items = "";

                    items = json.getString("items");

                    JSONArray jsonitem = new JSONArray(items);

                    name = new String[jsonitem.length()];
                    descripcion = new String[jsonitem.length()];
                    imagen = new String[jsonitem.length()];

                    // looping through All albums
                    for (int i = 0; i < jsonitem.length(); i++) {
                        JSONObject dato = jsonitem.getJSONObject(i);
                        name[i]= dato.getString("full_name");
                        descripcion[i] = dato.getString("description");
                        String owner = dato.getString("owner");
                        JSONObject jsonOwner = new JSONObject(owner);

                        imagen[i] = jsonOwner.getString("avatar_url");
                        adaptador.add(name[i]);



                    }



                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }


            return null;

        }


        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all albums
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (respuesta != "error") {
                        lista.setAdapter(adaptador);

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                Intent next = new Intent(getBaseContext(), DetalleRepo.class);
                                next.putExtra("nombre", name[position]);
                                next.putExtra("imagen", imagen[position]);
                                next.putExtra("descrip", descripcion[position]);
                                startActivity(next);


                            }
                        });

                    }else{
                        Toast.makeText(getBaseContext(),"No se encontraron datos", Toast.LENGTH_LONG).show();
                    }
                }
            });

            pDialog.dismiss();


        }
    }
}
