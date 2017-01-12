package com.example.aureliedebrot.fingerprint;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by aurelie.debrot on 12.01.2017.
 */

public class FingerPrintHandlerSec extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context appContext;


    public FingerPrintHandlerSec(Context context){
        appContext = context;
    }

    //démarre l'authentification
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject){
        cancellationSignal = new CancellationSignal();

        if(ActivityCompat.checkSelfPermission(appContext, Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal,0,this,null);
    }
    public  void onAuthenticationError(int errMsgId, CharSequence errString){
        Toast.makeText(appContext,"Authentication erro\n"+errString,Toast.LENGTH_LONG).show();
    }
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString){
        Toast.makeText(appContext, "Authentication help\n"+helpString,Toast.LENGTH_LONG).show();
    }
    public void onAuthenticationFailed(){
        Toast.makeText(appContext,"Authentication failed",Toast.LENGTH_LONG).show();
    }
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result ){
        Toast.makeText(appContext,"Authentication succeeded ",Toast.LENGTH_LONG).show();
        //Ouvrir une nouvelle page avec une application à faire dans la main activity plutôt qu'ici
        Log.d("App","Open App");
        //Est lancé après deux authentifications
        Intent intent = new Intent();
        intent.setClass(appContext,AuthSucceedActivity.class);
        appContext.startActivity(intent);
    }
}
