package pe.dquispe.myappfinal.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class ListaFragment extends Fragment {

    private static final int REQUEST_REGISTER_FORM = 100;
    private FloatingActionButton floatingActionButton;
    private static final String TAG = ListaFragment.class.getSimpleName();
    private RecyclerView mascotasList;
    public SwipeRefreshLayout swipeRefreshLayout;


    public ListaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lista, container, false);

        floatingActionButton=v.findViewById(R.id.btn_showRegister);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new RegistroMascotaFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack("tag").commit();
            }
        });

        mascotasList = v.findViewById(R.id.recyclerview_lista_mascotas);
        mascotasList.setLayoutManager(new LinearLayoutManager(getContext()));

        mascotasList.setAdapter(new MascotaAdapter());

        swipeRefreshLayout=v.findViewById(R.id.swiperefresh_lista_mascotas);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initialize();
            }
        });

        initialize();


        return v;
    }

    private void initialize() {
        swipeRefreshLayout.setRefreshing(true);

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        service.getMascota().enqueue(new Callback<List<Mascota>>() {

            @Override
            public void onResponse(@NonNull Call<List<Mascota>> call, @NonNull Response<List<Mascota>> response) {
                try {

                    if (response.isSuccessful()) {

                        List<Mascota> mascotas = response.body();
                        Log.d(TAG, "productos: " + mascotas);

                        MascotaAdapter adapter = (MascotaAdapter) mascotasList.getAdapter();
                        adapter.setMascotas(mascotas);
                        adapter.notifyDataSetChanged();

                    } else {
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                } catch (Throwable t) {
                    Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }finally {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Mascota>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
