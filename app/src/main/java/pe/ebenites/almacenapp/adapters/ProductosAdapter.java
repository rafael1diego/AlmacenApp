package pe.ebenites.almacenapp.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pe.ebenites.almacenapp.ApiMessage;
import pe.ebenites.almacenapp.DetailActivity;
import pe.ebenites.almacenapp.R;
import pe.ebenites.almacenapp.models.Producto;
import pe.ebenites.almacenapp.services.ApiService;
import pe.ebenites.almacenapp.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {

    private static final String TAG = ProductosAdapter.class.getSimpleName();

    private List<Producto> productos;

    public ProductosAdapter(){
        this.productos = new ArrayList<>();
    }

    public void setProductos(List<Producto> productos){
        this.productos = productos;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView fotoImage;
        TextView nombreText;
        TextView precioText;
        ImageButton menuButton;

        ViewHolder(View itemView) {
            super(itemView);
            fotoImage = itemView.findViewById(R.id.foto_image);
            nombreText = itemView.findViewById(R.id.nombre_text);
            precioText = itemView.findViewById(R.id.precio_text);
            menuButton = itemView.findViewById(R.id.menu_button);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        final Producto producto = this.productos.get(position);

        viewHolder.nombreText.setText(producto.getNombre());
        viewHolder.precioText.setText("S/. " + producto.getPrecio());

        String url = ApiService.API_BASE_URL + "/api/productos/images/" + producto.getImagen();
        //Picasso.with(viewHolder.itemView.getContext()).load(url).into(viewHolder.fotoImage);
        ApiServiceGenerator.createPicasso(viewHolder.itemView.getContext()).load(url).into(viewHolder.fotoImage);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewHolder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("ID", producto.getId());
                viewHolder.itemView.getContext().startActivity(intent);
            }
        });


        viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove_button:

                                ApiService service = ApiServiceGenerator.createService(v.getContext(), ApiService.class);

                                service.destroyProducto(producto.getId()).enqueue(new Callback<ApiMessage>() {
                                    @Override
                                    public void onResponse(@NonNull Call<ApiMessage> call, @NonNull Response<ApiMessage> response) {
                                        try {

                                            int statusCode = response.code();
                                            Log.d(TAG, "HTTP status code: " + statusCode);

                                            if (response.isSuccessful()) {

                                                ApiMessage apiMessage = response.body();
                                                Log.d(TAG, "apiMessage: " + apiMessage);

                                                // Eliminar item del recyclerView y notificar cambios
                                                productos.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, productos.size());

                                                Toast.makeText(v.getContext(), apiMessage.getMessage(), Toast.LENGTH_LONG).show();

                                            } else {
                                                Log.e(TAG, "onError: " + response.errorBody().string());
                                                throw new Exception("Error en el servicio");
                                            }

                                        } catch (Throwable t) {
                                            Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                                            Toast.makeText(v.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<ApiMessage> call, @NonNull Throwable t) {
                                        Log.e(TAG, "onFailure: " + t.getMessage(), t);
                                        Toast.makeText(v.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                });

                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {

        return this.productos.size();
    }


}

