package com.example.challenge;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mercadolibre.android.sdk.ApiRequestListener;
import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListResult extends AppCompatActivity implements ApiRequestListener {
    String search;
    ListView listItems;
    ArrayList<Item> items = new ArrayList<Item>();
    int offset = 0;
    int totalitems = 1;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result);

        //Inicializo un progressDialog para que el usuario sepa que esta cargando su busqueda
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Inicializo el listView
        listItems = (ListView)findViewById(R.id.items);

        //Pregunto si el arraylist con los resultados es vacio ya que de esa manera se que viene de la vista Main y no de Details en donde cuando vuelve tiene que ver los resultados de su anterior busqueda
        Log.d("ArrayList Items count", String.valueOf(items.size()));
        if(items.isEmpty())
        {
            Intent intent = getIntent();
            search = intent.getStringExtra("Search");
            //Obtengo la busqueda y llamo a loadResult
            loadResult();
        }

        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Id del Item", items.get(position).getId());
                //Mando a la vista detalles el id del item seleccionado de el listview
                Intent toDetail = new Intent(ListResult.this, DetailsItem.class);
                toDetail.putExtra("Item", items.get(position).getId());
                startActivity(toDetail);
            }
        });
    }
    private void loadResult()
    {
        //Activo el progressdialog y hago uso de la api para cargar los primeros 50 items ya que ese es el tope por llamada
        pDialog.setMessage("Searching...");
        showDialog();
        Meli.asyncGet("/sites/MLA/search?q=" + search + "&offset=" + offset,this);
    }

    public void more(View v)
    {
        //Mediante el boton more llamo a esta funcion que trae 50 items mas mediante la api siempre y cuando la cantidad total de items no sea superada
        Log.d("OffSet", String.valueOf(offset));
        Log.d("Total of items", String.valueOf(totalitems));
        if(offset < totalitems)
        {
            pDialog.setMessage("Searching...");
            showDialog();
            Meli.asyncGet("/sites/MLA/search?q=" + search + "&offset=" + offset,this);
        }
       else
        {
            new AlertDialog.Builder(this)
                    .setTitle("No more items to show")
                    .setMessage("Oooops, there are no more items to look for")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    }).show();
        }
    }

    @Override
    public void onRequestProcessed(int requestCode, ApiResponse payload) {
        //Guardo lo que responde la api en una variable string para luego mediante Json poder decodificarla
        String response = payload.getContent();
        try {
            //Primero creo un JSONObject mediante lo que trae la api
            JSONObject searchresult = new JSONObject(response);
            //Luego en un JSONArray guardo los 50 items
            JSONArray result = searchresult.getJSONArray("results");
            //Por ultimo creo otro JSONArray con el objetivo de poder manejar el offset para luego traer otros 50 items
            JSONObject paging  = searchresult.getJSONObject("paging");
            totalitems = paging.getInt("total");
            Log.d("Items totales", String.valueOf(totalitems));

            for (int i = 0; i < result.length(); i++) {
                //Mediante una clase Item y un for voy guardando en variables los valores por item para luego guardarlo en un arraylist asi luego se muestra por un listview con un adapter personalizado
                Item item = new Item();
                JSONObject searchitem  = result.getJSONObject(i);
                String title = searchitem.getString("title");
                String price = searchitem.getString("price");
                String thumbnail = searchitem.getString("thumbnail");
                String id = searchitem.getString("id");

                Log.d("Titulo del item", title);
                Log.d("Precio del item", price);
                Log.d("Imagen portada del item", thumbnail);
                Log.d("id del item", id);


                item.setTitle(title);
                item.setPrice(price);
                item.setThumbnail(thumbnail);
                item.setId(id);

                items.add(item);
            }
            //sumo 50 al offset para que si apretan more cargue desde el ultimo item que cargo la anterior llamada
            offset += 50;
            Log.d("Offset + 50", String.valueOf(offset));
            AdapterItem adapter = new AdapterItem(this, items);
            listItems.setAdapter(adapter);
            hideDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestStarted(int requestCode) {

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
