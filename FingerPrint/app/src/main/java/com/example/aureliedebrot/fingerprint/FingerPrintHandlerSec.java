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
 *
 * Classe permettant de gérer la deuxième phase d'authentification
 * Classe inspirée de : http://www.techotopia.com/index.php/An_Android_Fingerprint_Authentication_Tutorial (03.11.16)
 */

public class FingerPrintHandlerSec extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context appContext;

    /**
     * Constructeur de la classe
     * @param context
     */
    public FingerPrintHandlerSec(Context context){
        appContext = context;
    }

    //démarre l'authentification

    /**
     * Méthode permettant de démarrer la seconde authentification
     * @param manager
     * @param cryptoObject
     */
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject){
        cancellationSignal = new CancellationSignal();

        if(ActivityCompat.checkSelfPermission(appContext, Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal,0,this,null);
    }

    /**
     * Méthode appelée lorsqu'une erreur survient lors de la deuxième phase d'authentification
     * @param errMsgId
     * @param errString
     */
    public  void onAuthenticationError(int errMsgId, CharSequence errString){
        Toast.makeText(appContext,"Authentication erro\n"+errString,Toast.LENGTH_LONG).show();
    }

    /**
     * Méthode appelée lorsqu'une aide est nécessaire lors de la deuxième phase d'authentification
     * @param helpMsgId
     * @param helpString
     */
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString){
        Toast.makeText(appContext, "Authentication help\n"+helpString,Toast.LENGTH_LONG).show();
    }

    /**
     * Méthode appelée lorsque que la deuxième authentification est un échec
     */
    public void onAuthenticationFailed(){
        Toast.makeText(appContext,"Authentication failed",Toast.LENGTH_LONG).show();
    }

    /**
     * Méthode appelée lorsque la deuxième authentification est un succés. Elle lance alors une nouvelle vue.
     * @param result
     */
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result ){
        Toast.makeText(appContext,"Authentication succeeded ",Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setClass(appContext,AuthSucceedActivity.class);
        appContext.startActivity(intent);
    }
}
