package pe.dquispe.myappfinal.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import pe.dquispe.myappfinal.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnIngresarUsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnIngresarUsu=findViewById(R.id.btn_ingresar);

        btnIngresarUsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ListFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
                Toast.makeText(LoginActivity.this, "Lista de mascotas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
