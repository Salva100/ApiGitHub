package devworms.testapigithub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetalleRepo extends AppCompatActivity {

    TextView txtNombre,txtDescripcion;
    ImageView imgLogo;
    URL imageUrl;
    HttpURLConnection conn;
    Bitmap imagenLo;
    ProgressDialog pDialog;
    ListView listaIssues, listaContri;
    ArrayAdapter<String> adaptadorIssues,adaptadorContri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_repo);
        txtNombre = (TextView)findViewById(R.id.txtName);
        txtDescripcion = (TextView)findViewById(R.id.txtDescrip);
        imgLogo = (ImageView)findViewById(R.id.imgLogo);
        listaIssues = (ListView) findViewById(R.id.listIssues);
        listaContri = (ListView) findViewById(R.id.listUser);
        adaptadorIssues= new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        adaptadorContri= new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        new getDescargaImagen().execute();
        txtNombre.setText(getIntent().getStringExtra("nombre"));
        txtDescripcion.setText(getIntent().getStringExtra("descrip"));



    }

    class getDescargaImagen extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(DetalleRepo.this);
            pDialog.setMessage("Buscando info");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Albums JSON
         */
        protected String doInBackground(String... args) {

            try {
                imageUrl = new URL(getIntent().getStringExtra("imagen"));
                conn = (HttpURLConnection) imageUrl.openConnection();
                conn.connect();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2; // el factor de escala a minimizar la imagen, siempre es potencia de 2

                imagenLo = BitmapFactory.decodeStream(conn.getInputStream(), new Rect(0, 0, 0, 0), options);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String body = "https://api.github.com/repos/"+ getIntent().getStringExtra("nombre")+"/issues";


            JSONParser jsp = new JSONParser();
            String respuesta = jsp.makeHttpRequest(body, "GET", body, "");
            if (respuesta != "error") {
                try {
                    JSONArray jsonIssu = new JSONArray(respuesta);

                    for (int i = 0; i < 3; i++) {
                        JSONObject dato = jsonIssu.getJSONObject(i);

                        adaptadorIssues.add(dato.getString("title"));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }


            body = "https://api.github.com/repos/"+ getIntent().getStringExtra("nombre")+"/contributors";


            jsp = new JSONParser();
            respuesta = jsp.makeHttpRequest(body, "GET", body, "");
            if (respuesta != "error") {
                try {
                    JSONArray jsonIssu = new JSONArray(respuesta);

                    for (int i = 0; i < 3; i++) {
                        JSONObject dato = jsonIssu.getJSONObject(i);

                        adaptadorContri.add(dato.getString("login"));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            return null;

        }


        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all albums
            imgLogo.setImageBitmap(imagenLo);
            listaIssues.setAdapter(adaptadorIssues);
            listaContri.setAdapter(adaptadorContri);
            pDialog.dismiss();


        }
    }
}
