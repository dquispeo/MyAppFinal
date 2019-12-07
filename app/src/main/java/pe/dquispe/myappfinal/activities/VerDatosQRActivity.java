package pe.dquispe.myappfinal.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pe.dquispe.myappfinal.R;
import pe.dquispe.myappfinal.models.Mascota;
import pe.dquispe.myappfinal.models.Usuario;
import pe.dquispe.myappfinal.services.ApiService;
import pe.dquispe.myappfinal.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerDatosQRActivity extends AppCompatActivity {

    private static final String TAG = DetalleMascotaActivity.class.getSimpleName();
    private Long rutaid;
    private ImageView fotoImage;
    private TextView nombreText;
    private TextView razaText;
    private TextView edadText,dueñoText,correoText;
    String dueño,correo,idUsu;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_datos_qr);

        fotoImage = findViewById(R.id.foto_verqr_image);
        nombreText = findViewById(R.id.txt_verqr_mascota_nombre);
        razaText = findViewById(R.id.txt_verqr_mascota_raza);
        edadText = findViewById(R.id.txt_verqr_mascota_edad);
        dueñoText = findViewById(R.id.txt_verqr_mascota_dueño);
        correoText = findViewById(R.id.txt_verqr_mascota_correo);


        Bundle bundle=getIntent().getExtras();
        String rutaidString=bundle.getString("dato","");

        try {
            rutaid = new Long(Long.parseLong(rutaidString));
        } catch (Exception e) {
            Toast.makeText(VerDatosQRActivity.this, "Error\n:No existe esa mascota.\nNo coincide con el código QR.", Toast.LENGTH_LONG).show();
        }


        Log.d(TAG, "LOG__rutaid : " + rutaid);

        initialize();
    }
    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<Mascota> call = service.showMascotaQR(rutaid);
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
                                    Toast.makeText(VerDatosQRActivity.this, "onThrowable "+t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Usuario> call, Throwable t) {
                                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                                Toast.makeText(VerDatosQRActivity.this, "onFailure "+t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                        nombreText.setText(mascota.getNombre());
                        razaText.setText(mascota.getRaza());
                        edadText.setText(String.valueOf(mascota.getEdad()));
                        String url = ApiService.API_BASE_URL + "/api/mascotas/images/" + mascota.getImagen();
                        Picasso.with(VerDatosQRActivity.this).load(url).into(fotoImage);

                    } else {
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                    Toast.makeText(VerDatosQRActivity.this, "No existe esa mascota.\nNo coincide con el código QR.\nError: "+t.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(VerDatosQRActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mascota> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(VerDatosQRActivity.this, "No existe esa mascota.\nNo coincide con el código QR.\nError: "+t.getMessage(), Toast.LENGTH_LONG).show();
                Intent intent=new Intent(VerDatosQRActivity.this, HomeActivity.class);
                startActivity(intent);
            }

        });
    }
}
