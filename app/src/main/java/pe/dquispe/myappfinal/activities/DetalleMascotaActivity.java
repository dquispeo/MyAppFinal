package pe.dquispe.myappfinal.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.List;

import pe.dquispe.myappfinal.R;
import pe.dquispe.myappfinal.models.Mascota;
import pe.dquispe.myappfinal.models.Usuario;
import pe.dquispe.myappfinal.services.ApiService;
import pe.dquispe.myappfinal.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleMascotaActivity extends AppCompatActivity {

    private static final String TAG = DetalleMascotaActivity.class.getSimpleName();
    private Long id;
    private ImageView fotoImage;
    private TextView nombreText;
    private TextView razaText;
    private TextView edadText,dueñoText,correoText;
    String dueño,correo,idUsu;
    private ImageView CodeImageViewQR;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota);

        fotoImage = findViewById(R.id.foto_image);
        nombreText = findViewById(R.id.txt_detalle_mascota_nombre);
        razaText = findViewById(R.id.txt_detalle_mascota_raza);
        edadText = findViewById(R.id.txt_detalle_mascota_edad);
        dueñoText = findViewById(R.id.txt_detalle_mascota_dueño);
        correoText = findViewById(R.id.txt_detalle_mascota_correo);
        CodeImageViewQR = findViewById(R.id.qrCodeImageView);

        id = getIntent().getExtras().getLong("ID");
        Log.e(TAG, "id:" + id);

        initialize();

    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<Mascota> call = service.showMascota(id);
        call.enqueue(new Callback<Mascota>() {
            String dueñoUsu,correoUsu;
            @Override
            public void onResponse(@NonNull Call<Mascota> call, @NonNull Response<Mascota> response) {
                try {
                    if (response.isSuccessful()) {
                        Mascota mascota = response.body();
                        Log.d(TAG, "LOG__mascotas : " + mascota);
                        Log.d(TAG, "LOG__usuario__mascota : " + mascota.getUsuario_id());

                        ApiService serviceusu = ApiServiceGenerator.createService(ApiService.class);
                        Call<Usuario> callusu=serviceusu.showUsuario(mascota.getUsuario_id());

                        callusu.enqueue(new Callback<Usuario>() {
                            @Override
                            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                try{
                                    if(response.isSuccessful()){
                                        usuario=response.body();
                                        Log.d(TAG, "LOG__usuario : " + usuario);
                                        dueñoUsu=usuario.getNombre();
                                        correoUsu=usuario.getCorreo();

                                        dueñoText.setText(dueñoUsu);
                                        correoText.setText(correoUsu);
                                    }
                                } catch (Throwable t) {
                                    Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                                    Toast.makeText(DetalleMascotaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Usuario> call, Throwable t) {
                                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                                Toast.makeText(DetalleMascotaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                        nombreText.setText(mascota.getNombre());
                        razaText.setText(mascota.getRaza());
                        edadText.setText(String.valueOf(mascota.getEdad()));
                        String url = ApiService.API_BASE_URL + "/api/mascotas/images/" + mascota.getImagen();
                        Picasso.with(DetalleMascotaActivity.this).load(url).into(fotoImage);

                        ///////////////
                        generarQR();
                        ///////////////

                    } else {
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                    Toast.makeText(DetalleMascotaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mascota> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(DetalleMascotaActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    ///////////////
    private void generarQR(){
        String nombreTextqr=nombreText.getText().toString();
        String razaTextqr=razaText.getText().toString();
        String edadTextqr=edadText.getText().toString();
        String dueñoTextqr=dueñoText.getText().toString();
        String correoTextqr=correoText.getText().toString();

        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
        try {
            BitMatrix bitMatrix=multiFormatWriter.encode(String.valueOf(id), BarcodeFormat.QR_CODE,1000,1000);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
            CodeImageViewQR.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }
    ///////////////

}
