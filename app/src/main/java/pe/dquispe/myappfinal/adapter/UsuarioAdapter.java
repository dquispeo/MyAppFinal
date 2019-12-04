package pe.dquispe.myappfinal.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pe.dquispe.myappfinal.R;
import pe.dquispe.myappfinal.models.Usuario;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {
    private static final String TAG = UsuarioAdapter.class.getSimpleName();

    private List<Usuario> usuarios;

    public UsuarioAdapter(){
        this.usuarios = new ArrayList<>();
    }

    public void setUsuarios(List<Usuario> usuarios){
        this.usuarios = usuarios;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
