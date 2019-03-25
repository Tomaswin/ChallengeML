package com.example.challenge;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mercadolibre.android.sdk.Identity;
import com.mercadolibre.android.sdk.Meli;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 999;
    private EditText etSearch;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearch = (EditText)findViewById(R.id.searchBar);
        btnSearch = (Button)findViewById(R.id.btnSearch);

        //Inicializo el sdk de mercadolibre y llamo a la funcion startlogin para que el usuario ingrese con su cuenta de ML
        Meli.initializeSDK(getApplicationContext());
        Meli.startLogin(this, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                processLoginProcessCompleted();
            } else {
                processLoginProcessWithError();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Si entra a esta funcion el proceso de login es correcto por lo que le aviso al usuario que ya no requiere iniciar sesion las proximas veces
    private void processLoginProcessCompleted() {
        Identity identity = Meli.getCurrentIdentity(getApplicationContext());
        if (identity != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Login successful")
                    .setMessage("Your user has been saved so you no longer need to log in back")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    }).show();
        }
    }

    //En cambio si entra aca significa que hubo un problema por lo que es necesario reiniciar la aplicacion para volver que se vuelva a loguear
    private void processLoginProcessWithError() {
        new AlertDialog.Builder(this)
                .setTitle("Login wrong")
                .setMessage("Oooops, something went wrong with the login process, please restart the application")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    public void search(View v)
    {
        //obtengo el item buscado por el usuario
        String search = etSearch.getText().toString();

        //valido que no me ingrese vacio y le notifico al usuario en el caso que se haya equivocado
        Log.d("Busqueda hecha",search);
        if(search.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Oooops, please enter something to search", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Mando lo que el usuario busco hacia la vista que le va a mostrar los resultados
            Intent intent = new Intent(getApplicationContext(), ListResult.class);
            intent.putExtra("Search", search);
            startActivity(intent);
        }
    }
}
