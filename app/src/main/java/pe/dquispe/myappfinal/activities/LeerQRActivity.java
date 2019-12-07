package pe.dquispe.myappfinal.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pe.dquispe.myappfinal.R;

import static android.Manifest.permission.CAMERA;

public class LeerQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    private static final String TAG = LeerQRActivity.class.getSimpleName();
    Long negidverificar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_qr);

        escanearQR();
    }

    /////permisos////
    //////////Camara
    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    ///////////// Escaner codigo QR /////////////
    private void escanearQR(){
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permiso ya otorgado", Toast.LENGTH_LONG).show();

                mScannerView = new ZXingScannerView(this);
                setContentView(mScannerView);
               /* mScannerView.setAutoFocus(true);
                mScannerView.setLaserColor(R.color.colorAccent);
                mScannerView.setMaskColor(R.color.colorAccent);*/
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permiso concedido, ahora puedes acceder a la cámara", Toast.LENGTH_LONG).show();
                        mScannerView = new ZXingScannerView(this);
                        setContentView(mScannerView);
                        mScannerView.setResultHandler(this);
                        mScannerView.startCamera();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permiso denegado, no puede acceder y cámara", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("Debe acceder a los permisos",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(LeerQRActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result rawResult) {

        final String result = rawResult.getText();
        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resultado:");
        builder.setPositiveButton("Escanear Nuevamente", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScannerView.resumeCameraPreview(LeerQRActivity.this);
            }
        });

        builder.setNeutralButton("Ver datos de esta mascota", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(LeerQRActivity.this, VerDatosQRActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("dato",result);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    Log.d(TAG, "RESULT_______URL: " + result);

                    finish();
                }
            });

        //builder.setMessage("Mascota encontrada");
        AlertDialog alert1 = builder.create();
        alert1.show();
    }
}
