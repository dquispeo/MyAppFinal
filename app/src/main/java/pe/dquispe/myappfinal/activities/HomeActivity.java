package pe.dquispe.myappfinal.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import pe.dquispe.myappfinal.R;
import pe.dquispe.myappfinal.fragment.ListaFragment;
import pe.dquispe.myappfinal.fragment.ListaTodasFragment;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;
    private Long usuid;
    private String usucorreo,usuusu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setear Toolbar como action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, android.R.string.ok, android.R.string.cancel);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set NavigationItemSelectedListener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Do action by menu item id
                switch (menuItem.getItemId()){
                    case R.id.nav_todos:
                        showTodasMascotas();
                        Toast.makeText(HomeActivity.this, "Todas las mascotas", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_datos:
                        showMascotas();
                        Toast.makeText(HomeActivity.this, "Mis mascotas", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_qr:
                        escanearMascotas();
                        Toast.makeText(HomeActivity.this, "Escanear QR", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_logout:
                        logout();
                        Toast.makeText(HomeActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                        break;
                }

                // Close drawer
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
        showTodasMascotas();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        usuid = sp.getLong("usuid", 0L);
        usuusu= sp.getString("usuusu", "");
        usucorreo= sp.getString("correo", "");
        //usuid = getActivity().getIntent().getExtras().getLong("ID");
        Log.e(TAG, "usuid:" + usuid);

        TextView fullnameText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_nombre_nav);
        fullnameText.setText(usuusu);
        TextView emailText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_correo_nav);
        emailText.setText(usucorreo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Option open drawer
                if(!drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.openDrawer(GravityCompat.START);   // Open drawer
                else
                    drawerLayout.closeDrawer(GravityCompat.START);    // Close drawer
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ///////// Todas las Mascotas ///////////
    private void showTodasMascotas(){
        Fragment fragment = new ListaTodasFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
    }
    ///////// Mis Mascotas ///////////
    private void showMascotas(){
        Fragment fragment = new ListaFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
    }
    ///////// QR ///////////
    private void escanearMascotas(){
        Intent intent=new Intent(HomeActivity.this, LeerQRActivity.class);
        startActivity(intent);
    }
    /////// Cerrar Sesión//////////
    private void logout(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove("islogged").remove("usuid").remove("usuusu").remove("correo").commit();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


}
