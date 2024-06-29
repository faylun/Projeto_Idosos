package com.example.m3_projeto_idosos;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Uri> photoUris;

    public PhotoAdapter(List<Uri> photoUris) {
        this.photoUris = photoUris;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri uri = photoUris.get(position);

        // Usando Picasso para carregar a imagem
        Picasso.get()
                .load(uri)
               .into(holder.imageView);

        // Opcional: definir um título dinâmico ou outra informação
        holder.tvPhotoTitle.setText("Photo " + (position + 1));
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvPhotoTitle;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvPhotoTitle = itemView.findViewById(R.id.tvPhotoTitle);
        }
    }
}
