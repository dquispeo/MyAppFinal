package pe.dquispe.myappfinal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pe.dquispe.myappfinal.R;
import pe.dquispe.myappfinal.activities.DetalleMascotaActivity;
import pe.dquispe.myappfinal.models.Mascota;
import pe.dquispe.myappfinal.services.ApiService;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.ViewHolder> {

    private static final String TAG = MascotaAdapter.class.getSimpleName();

    private List<Mascota> mascotas;

    public MascotaAdapter(){
        this.mascotas = new ArrayList<>();
    }

    public void setMascotas(List<Mascota> mascotas){
        this.mascotas = mascotas;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView fotoImage;
        TextView nombreText;
        TextView razaText;
        TextView edadText;

        ViewHolder(View itemView) {
            super(itemView);
            fotoImage = itemView.findViewById(R.id.img_mascota);
            nombreText = itemView.findViewById(R.id.txt_mascota_nombre);
            razaText = itemView.findViewById(R.id.txt_mascota_raza);
            edadText = itemView.findViewById(R.id.txt_mascota_edad);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mascota_item, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Context context = viewHolder.itemView.getContext();

        final Mascota mascota = this.mascotas.get(position);

        viewHolder.nombreText.setText(mascota.getNombre());
        viewHolder.razaText.setText(mascota.getRaza());
        viewHolder.edadText.setText(mascota.getEdad());

        String url = ApiService.API_BASE_URL + "/mascotas/images/" + mascota.getImagen();
        Picasso.with(context).load(url).into(viewHolder.fotoImage);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetalleMascotaActivity.class);
                intent.putExtra("ID", mascota.getId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mascotas.size();
    }
}
