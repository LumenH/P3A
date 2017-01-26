package com.example.aureliedebrot.fingerprint;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.startActivity;

//From : http://www.techotopia.com/index.php/An_Android_Fingerprint_Authentication_Tutorial(03.11.16)

/**
 * Classe gère tout ce qui a attrait à l'authentification.
 * Classe inspirée de : http://www.techotopia.com/index.php/An_Android_Fingerprint_Authentication_Tutorial (03.11.16)
 */

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback{

    private CancellationSignal cancellationSignal;
    private Context appContext;

    /**
     * Constructeur de la classe
     * @param context
     */
    public FingerPrintHandler(Context context){
        appContext = context;
    }

    /**
     * Méthode appelée lors du démarrage de l'authentification
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
     * Méthode appelée en cas d'erreur lors de la phase d'authentification
     * @param errMsgId
     * @param errString
     */
    public  void onAuthenticationError(int errMsgId, CharSequence errString){
        Toast.makeText(appContext,"Authentication erro\n"+errString,Toast.LENGTH_LONG).show();
    }

    /**
     * Méthode appelée si de l'aide est nécessaire lors de la phase d'authentification
     * @param helpMsgId
     * @param helpString
     */
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString){
        Toast.makeText(appContext, "Authentication help\n"+helpString,Toast.LENGTH_LONG).show();
    }

    /**
     * Méthode appelée si l'authentification échoue.
     */
    public void onAuthenticationFailed(){
        Toast.makeText(appContext,"Authentication failed",Toast.LENGTH_LONG).show();
    }

    /**
     * Méthode appelée sir l'authentification est un sccès. Elle permet de passer à une autre activité.
     * @param result
     */
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result ){
        Toast.makeText(appContext,"Authentication succeeded ",Toast.LENGTH_LONG).show();
            Intent intentSec = new Intent();
            intentSec.setClass(appContext, SecondSucess.class);
            appContext.startActivity(intentSec);
    }
}
