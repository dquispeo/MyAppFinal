package pe.dquispe.myappfinal.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pe.dquispe.myappfinal.R;
import pe.dquispe.myappfinal.models.Mascota;
import pe.dquispe.myappfinal.services.ApiService;
import pe.dquispe.myappfinal.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.app.Activity.RESULT_OK;

public class RegistroMascotaFragment extends Fragment {

    private static final String TAG = RegistroMascotaFragment.class.getSimpleName();
    private static final int REQUEST_CAMERA = 100;
    String CAMERA_PERMISSION = android.Manifest.permission.CAMERA;

    private ImageView imagenPreview;

    private EditText nombreMascotaInput;
    private EditText razaMascotaInput;
    private EditText edadMascotaInput;
    private Button registrarMascota,tomarFoto;
    private Long usuid;

    public RegistroMascotaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_registro_mascota, container, false);

        imagenPreview =(ImageView) v.findViewById(R.id.imagen_preview);
        nombreMascotaInput =(EditText) v.findViewById(R.id.edit_mascota_nombre_reg);
        razaMascotaInput =(EditText) v.findViewById(R.id.edit_mascota_raza_reg);
        edadMascotaInput =(EditText) v.findViewById(R.id.edit_mascota_edad_reg);
        registrarMascota=(Button) v.findViewById(R.id.btn_registrar_mascota);
        tomarFoto=(Button) v.findViewById(R.id.btn_takePicture);

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camara();
            }
        });

        registrarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRegister();
            }
        });

        return v;
    }

    ///////////// Escaner codigo QR /////////////
    private void camara(){
        //////camara
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getContext(), "Permiso ya otorgado", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getContext(), "Permiso concedido, ahora puedes acceder a la cámara", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }else {
                        Toast.makeText(getContext(), "Permiso denegado, no puede acceder y cámara", Toast.LENGTH_LONG).show();
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
        new android.support.v7.app.AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    private Bitmap bitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                bitmap = scaleBitmapDown(bitmap, 800);  // Redimensionar
                imagenPreview.setImageBitmap(bitmap);
            }
        }
    }

    public void callRegister(){

        String nombre = nombreMascotaInput.getText().toString();
        String raza = razaMascotaInput.getText().toString();
        String edad = edadMascotaInput.getText().toString();

        if (nombre.isEmpty() || raza.isEmpty() || edad.isEmpty()) {
            Toast.makeText(getContext(), "Nombre, Raza y Edad son campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<Mascota> call;

        if(bitmap == null){
            call = service.createMascota(nombre, raza, edad);
        } else {

            // De bitmap a ByteArray
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // ByteArray a MultiPart
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
            MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen", "photo.jpg", requestFile);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            usuid = sp.getLong("usuid", 0L);
            //usuid = getActivity().getIntent().getExtras().getLong("ID");
            Log.e(TAG, "usuid:" + usuid);

            // Paramestros a Part
            RequestBody nombrePart = RequestBody.create(MultipartBody.FORM, nombre);
            RequestBody razaPart = RequestBody.create(MultipartBody.FORM, raza);
            RequestBody edadPart = RequestBody.create(MultipartBody.FORM, edad);
            RequestBody usuidPart = RequestBody.create(MultipartBody.FORM, String.valueOf(usuid));

            call = service.createMascota(nombrePart, razaPart, edadPart, usuidPart, imagenPart);
        }

        call.enqueue(new Callback<Mascota>() {
            @Override
            public void onResponse(@NonNull Call<Mascota> call, @NonNull Response<Mascota> response) {
                try {
                    if(response.isSuccessful()) {

                        Mascota mascota = response.body();
                        Log.d(TAG, "mascota: " + mascota);

                        Toast.makeText(getContext(), "Registro guardado satisfactoriamente", Toast.LENGTH_SHORT).show();

                        Fragment fragment = new ListaFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();

                    }else{
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }
                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mascota> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    // Redimensionar una imagen bitmap
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }


}
