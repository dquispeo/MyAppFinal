package pe.dquispe.myappfinal.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import pe.dquispe.myappfinal.R;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = RegistroActivity.class.getSimpleName();
    private EditText nombreUsuInput;
    private EditText correoUsuInput;
    private EditText passwordUsuInput;
    private Button registrarUsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombreUsuInput =(EditText) findViewById(R.id.edit_usu_nombre);
        correoUsuInput =(EditText) findViewById(R.id.edit_usu_email);
        passwordUsuInput =(EditText) findViewById(R.id.edit_usu_password);
        registrarUsu=(Button) findViewById(R.id.btn_usu_registro);
    }
}
