package com.example.aureliedebrot.fingerprint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by aurelie.debrot on 17.11.2016.
 *
 * Cette classe affiche la bonne vue lorsque l'authentification est un succés, via la méthode onCreate.
 */

public class AuthSucceedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authsucceed_activity);
    }
}
