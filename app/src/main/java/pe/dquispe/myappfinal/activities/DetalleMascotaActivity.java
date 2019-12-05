package pe.dquispe.myappfinal.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import pe.dquispe.myappfinal.R;
import pe.dquispe.myappfinal.models.Mascota;
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
    private TextView edadText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota);

        fotoImage = findViewById(R.id.foto_image);
        nombreText = findViewById(R.id.txt_detalle_mascota_nombre);
        razaText = findViewById(R.id.txt_detalle_mascota_raza);
        edadText = findViewById(R.id.txt_detalle_mascota_edad);

        id = getIntent().getExtras().getLong("ID");
        Log.e(TAG, "id:" + id);

        initialize();

    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<Mascota> call = service.showMascota(id);

        call.enqueue(new Callback<Mascota>() {
            @Override
            public void onResponse(@NonNull Call<Mascota> call, @NonNull Response<Mascota> response) {
                try {

                    if (response.isSuccessful()) {

                        Mascota mascota = response.body();
                        Log.d(TAG, "mascotas : " + mascota);

                        nombreText.setText(mascota.getNombre());
                        razaText.setText(mascota.getRaza());
                        edadText.setText(String.valueOf(mascota.getEdad()));

                        String url = ApiService.API_BASE_URL + "/api/mascotas/images/" + mascota.getImagen();
                        Picasso.with(DetalleMascotaActivity.this).load(url).into(fotoImage);

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

}
