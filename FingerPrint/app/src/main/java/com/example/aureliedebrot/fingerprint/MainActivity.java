package com.example.aureliedebrot.fingerprint;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Classe inspirée de : http://www.techotopia.com/index.php/An_Android_Fingerprint_Authentication_Tutorial (03.11.16)
 * Elle gère les étapes liées à la partie sécurité de l'authentification
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_NAME = "exemple_key";
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    /**
     * Méthode permettant de lancer la vue de la première authentification.
     * Elle obtient aussi les références aux service fingerPrintManager et keyGuardManager.
     * Les permissions suivants sont vérifiées :
     * - Le verrouillage de l'écran est activé et une séurité est disponible
     * - La permission d'utiliser le lecteur d'empreinte est bien précisée dans le manifest
     * - Une empreinte a bien été enregistrée sur le téléphone
     * Une fois cela fait, une clé est générée.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtenir les références aux services fingerPrintManager et keyGuardManager
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        //Verification des permissions d'utilisation, qu'une empreinte soit bien enregistrée
        //Test si le verouillage de l'écran est bien activé, s'il y a bien une sécurité.
        if(!keyguardManager.isKeyguardSecure()){
            Toast.makeText(this,"Lock screen security not enabled in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }
        //test si la premission est bien précisée dans le manifest
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Fingerprint authentication permission not enabled",
                    Toast.LENGTH_LONG).show();
            return;
        }
        //Vérifie qu'une empreinte ait bien été enregistrée.
        if(!fingerprintManager.hasEnrolledFingerprints()){
            Toast.makeText(this,"Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //génération de la clé
        generateKey();
        if(cipherInit()){
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerPrintHandler helper = new FingerPrintHandler(this);
            //L'authentification commence ici
            helper.startAuth(fingerprintManager,cryptoObject);

        }
    }


    /**
     * Méthode permettant d'obtenir l'accès à l'endroit où est stocké l'empreinte. Une clé est générée.
     */
    protected void generateKey(){
        try{
            keyStore = keyStore.getInstance("AndroidKeyStore");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            keyGenerator = keyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException e){
            throw new RuntimeException("Failed to get KeyGenerator instance",e);
        }
        //Génération de la clé (cryptographie)
        try{
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
            KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setUserAuthenticationRequired(true).setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7
                    ).build());
            keyGenerator.generateKey();
        }
        catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                CertificateException | IOException e){
            throw new RuntimeException(e);
        }
    }


    /**
     * Méthode permettant d'initialiser un cipher avec le clé précédemment créée.
     * @return
     */
    public boolean cipherInit(){
        try{
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
            + KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException e){
            throw new RuntimeException("Failed to get Cipher",e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            return true;
        }
        catch (KeyPermanentlyInvalidatedException e){
            return false;
        }
        catch (KeyStoreException | CertificateException | UnrecoverableKeyException
                | IOException | NoSuchAlgorithmException | InvalidKeyException e){
            throw  new RuntimeException("Failed to init Cipher",e);
        }
    }
}
