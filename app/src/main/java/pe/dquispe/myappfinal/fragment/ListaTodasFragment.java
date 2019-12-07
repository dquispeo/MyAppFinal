package pe.dquispe.myappfinal.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import pe.dquispe.myappfinal.R;
import pe.dquispe.myappfinal.adapter.MascotaAdapter;
import pe.dquispe.myappfinal.models.Mascota;
import pe.dquispe.myappfinal.services.ApiService;
import pe.dquispe.myappfinal.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaTodasFragment extends Fragment {

    private ProgressDialog progress;
    private static final String TAG = ListaTodasFragment.class.getSimpleName();
    private RecyclerView todasmascotasList;
    public SwipeRefreshLayout swipeRefreshLayout;
    public FloatingActionButton floatingActionButton;

    public ListaTodasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lista_todas, container, false);

        todasmascotasList = v.findViewById(R.id.recyclerview_lista_mascotas_todas);
        todasmascotasList.setLayoutManager(new LinearLayoutManager(getContext()));

        todasmascotasList.setAdapter(new MascotaAdapter());

        swipeRefreshLayout=v.findViewById(R.id.swiperefresh_lista_mascotas_todas);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initialize();
            }
        });

        initialize();

        return v;
    }

    private void initialize(){
        swipeRefreshLayout.setRefreshing(true);

        ApiService service= ApiServiceGenerator.createService(ApiService.class);
        service.getMascota().enqueue(new Callback<List<Mascota>>() {
            @Override
            public void onResponse(Call<List<Mascota>> call, Response<List<Mascota>> response) {
                try{
                    if(response.isSuccessful()){
                        List<Mascota> mascotas=response.body();
                        Log.d(TAG,"mascotas: "+mascotas);

                        MascotaAdapter adapter=(MascotaAdapter)todasmascotasList.getAdapter();
                        adapter.setMascotas(mascotas);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Lista de Mascotas", Toast.LENGTH_LONG).show();
                    }else{
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }
                }catch (Throwable t){
                    Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Error en el Servidor al listar las mascotas", Toast.LENGTH_LONG).show();
                } finally {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Mascota>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(getContext(), "No se puede conectar, verifique el acceso a internet e intente nuevamente", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
