package pe.dquispe.myappfinal.services;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pe.dquispe.myappfinal.models.Mascota;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    String API_BASE_URL = "http://10.0.2.2:8080"; //http://10.0.2.2:8080

    @GET("/api/mascotas")
    Call<List<Mascota>> getMascota();


    @FormUrlEncoded
    @POST("/api/mascotas")
    Call<Mascota> createMascota(@Field("nombre") String nombre,
                                 @Field("precio") String precio,
                                 @Field("detalles") String detalles);


    @Multipart
    @POST("/api/mascotas")
    Call<Mascota> createMascota(@Part("nombre") RequestBody nombre,
                                @Part("raza") RequestBody raza,
                                @Part("edad") RequestBody edad,
                                @Part MultipartBody.Part imagen
    );

    @GET("/api/mascotas/{id}")
    Call<Mascota> showMascota(@Path("id") Long id);

}
