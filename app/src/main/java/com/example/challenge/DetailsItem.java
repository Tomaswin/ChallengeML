package com.example.challenge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.mercadolibre.android.sdk.ApiRequestListener;
import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsItem extends AppCompatActivity implements ApiRequestListener {
    private ProgressDialog pDialog;
    ViewFlipper viewFlipper;
    TextView title;
    TextView availableQuantity;
    TextView soldQuantity;
    boolean firstTime = true;
    TextView price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_item);

        //Inicializo un progressDialog para que el usuario sepa que esta cargando su busqueda
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Inicializo los campos que se van a mostrar en detalle y el viewFlipper para mostrar las imagenes en carrousel
        viewFlipper = (ViewFlipper)findViewById(R.id.flipperid);
        title = (TextView) findViewById(R.id.title);
        price = (TextView) findViewById(R.id.price);
        availableQuantity = (TextView) findViewById(R.id.availablequantity);
        soldQuantity = (TextView) findViewById(R.id.soldquantity);

        //Obtengo el id del item para obtener los detalles del mismo
        Intent intent = getIntent();
        String itemId = intent.getStringExtra("Item");
        Log.d("Item id", itemId);
        itemDetails(itemId);
    }

    private void itemDetails(String response)
    {
        pDialog.setMessage("Loading...");
        showDialog();
        Meli.asyncGet("/items/" + response,this);
        Meli.asyncGet("/items/" + response + "/description", this);
    }

    @Override
    public void onRequestProcessed(int requestCode, ApiResponse payload) {
        String respuesta = payload.getContent();
        //Como llamo dos veces a la api, la primera para obtener toda la informacion del item en detalle y la segunda para obtener su descripcion
        try {
            JSONObject searchresult = new JSONObject(respuesta);
            //Si es la primera vez obtengo las imagenes y la informacion del item
            //Si es la segunda vez obtengo la descripcion del item
            if(!searchresult.has("plain_text"))
            {
                firstTime = false;
                JSONArray picturesArray = searchresult.getJSONArray("pictures");
                title.setText(searchresult.getString("title"));
                price.setText(searchresult.getString("currency_id") + " $" + searchresult.getString("price"));
                availableQuantity.setText("Quantity Available: " + searchresult.getString("available_quantity"));
                soldQuantity.setText("Quantity Sold: " + searchresult.getString("sold_quantity"));
                JSONObject pictures;
                for (int i = 0; i < picturesArray.length(); i++)
                {
                    final ImageView imageViews = new ImageView(this);
                    imageViews.setLayoutParams(new LinearLayout.LayoutParams(480,480));
                    pictures = picturesArray.getJSONObject(i);
                    bringImage(pictures.getString("url"), imageViews);

                    viewFlipper.addView(imageViews);
                }
                viewFlipper.startFlipping();
            }
            else
            {
                TextView description = (TextView) findViewById(R.id.description);
                description.setText(searchresult.getString("plain_text"));
                description.setMovementMethod(new ScrollingMovementMethod());
                hideDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            }
    }

    @Override
    public void onRequestStarted(int requestCode) {

    }

    public void bringImage(String url, ImageView pic)
    {
        Picasso.with(DetailsItem.this).load(url).placeholder(R.mipmap.ic_launcher)
                .into(pic,new com.squareup.picasso.Callback(){


                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
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
