package com.example.m3_projeto_idosos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<PhotoData> photoDataList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PhotoData item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PhotoAdapter(List<PhotoData> photoDataList, OnItemClickListener listener) {
        this.photoDataList = photoDataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PhotoData photoData = photoDataList.get(position);
        holder.bind(photoData, listener);
    }

    @Override
    public int getItemCount() {
        return photoDataList.size();
    }

    public void updateData(List<PhotoData> newData) {
        photoDataList = newData;
        notifyDataSetChanged();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(final PhotoData photoData, final OnItemClickListener listener) {
            imageView.setImageURI(photoData.getPhotoUri());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(photoData);
                }
            });
        }
    }
}
