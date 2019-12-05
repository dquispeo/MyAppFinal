package pe.dquispe.myappfinal.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pe.dquispe.myappfinal.R;
import pe.dquispe.myappfinal.models.ApiError;
import pe.dquispe.myappfinal.models.Usuario;
import pe.dquispe.myappfinal.services.ApiService;
import pe.dquispe.myappfinal.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText correoInput,passwordInput;
    private Button btnIngresarUsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        correoInput = findViewById(R.id.edit_email);
        passwordInput = findViewById(R.id.edit_password);
        btnIngresarUsu=findViewById(R.id.btn_ingresar);

        btnIngresarUsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
                //Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                //startActivity(intent);
                Toast.makeText(LoginActivity.this, "Lista de mascotas", Toast.LENGTH_SHORT).show();
            }
        });

        loadLastUsername();

        verifyLoginStatus();

    }

    private void login(){

        String correo = correoInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(correo.isEmpty()){
            Toast.makeText(this, "Ingrese el correo de se usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.isEmpty()){
            Toast.makeText(this, "Ingrese el password", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<Usuario> call = service.login(correo, password);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()) { // code 200
                    Usuario usuario = response.body();
                    Log.d(TAG, "usuario" + usuario);

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    sp.edit()
                            .putLong("usuid", usuario.getId())
                            .putString("usuusu", usuario.getNombre())
                            .putString("correo", usuario.getCorreo())
                            .putBoolean("islogged", true)
                            .commit();

                    // Go Main Activity
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                    Toast.makeText(LoginActivity.this, "Bienvenido " + usuario.getNombre(), Toast.LENGTH_LONG).show();

                }else{
                    ApiError error = ApiServiceGenerator.parseError(response);
                    Toast.makeText(LoginActivity.this, "onError:" + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "onFailure: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void loadLastUsername(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        String correoo = sp.getString("correoo", null);
        if(correoo != null){
            correoInput.setText(correoo);
        }
    }

    private void verifyLoginStatus(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean islogged = sp.getBoolean("islogged", false);

        if(islogged){
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

    }

}
