package com.example.m3_projeto_idosos;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private DatabaseHelper dbHelper;

    private String currentPhotoPath;
    private ArrayList<PhotoData> photoDataList = new ArrayList<>();
    private PhotoAdapter photoAdapter;
    private RecyclerView recyclerView;
    private TextView emptyAlbumTextView;
    private String currentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        Button btnCapture = findViewById(R.id.btnCapture);
        recyclerView = findViewById(R.id.recyclerView);
        emptyAlbumTextView = findViewById(R.id.emptyAlbumTextView);

        setSupportActionBar(findViewById(R.id.toolbar));

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        photoAdapter = new PhotoAdapter(photoDataList, new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PhotoData photoData) {
                Intent intent = new Intent(MainActivity.this, PhotoDetailActivity.class);
                intent.putExtra("PHOTO_DATA", photoData);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(photoAdapter);

        loadPhotosFromDatabase();
        updateAlbumVisibility();

        btnCapture.setOnClickListener(v -> dispatchTakePictureIntent());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("MainActivity", "Error occurred while creating the file", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } else {
                Toast.makeText(this, "Erro ao criar o arquivo de imagem", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            showCategorySelectionDialog(currentPhotoPath);
        }
    }

    private void showCategorySelectionDialog(final String photoPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione a categoria");

        final String[] categories = {"Paisagem", "Objetos", "Pessoas", "Construções", "Animais", "Outros"};

        builder.setItems(categories, (dialog, which) -> {
            String selectedCategory = categories[which];
            updatePhotoList(photoPath, selectedCategory);
        });

        builder.show();
    }

    private void updatePhotoList(String photoPath, String category) {
        PhotoData photoData = new PhotoData();
        photoData.setPhotoUri(Uri.fromFile(new File(photoPath)));
        photoData.setLatitude(0.0);
        photoData.setLongitude(0.0);
        photoData.setTimestamp(new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()));
        photoData.setDescription("Adicione uma descrição");
        photoData.setCategory(category);

        dbHelper.insertPhoto(photoData);
        photoDataList.add(photoData);

        updateAlbumVisibility();
        refreshPhotoList();

        Toast.makeText(this, "Foto capturada e adicionada ao álbum", Toast.LENGTH_SHORT).show();
    }

    private void loadPhotosFromDatabase() {
        List<PhotoData> photosFromDb = dbHelper.getAllPhotos();
        photoDataList.clear(); // Limpa a lista antes de adicionar novos dados
        photoDataList.addAll(photosFromDb);
        refreshPhotoList();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void updateAlbumVisibility() {
        if (photoDataList.isEmpty()) {
            emptyAlbumTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyAlbumTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void refreshPhotoList() {
        Collections.sort(photoDataList, (photo1, photo2) -> photo2.getTimestamp().compareTo(photo1.getTimestamp()));
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter_all) {
            currentFilter = null;
            loadPhotosFromDatabase();
        } else if (id == R.id.filter_landscape) {
            currentFilter = "Paisagem";
            filterPhotosByCategory(currentFilter);
        } else if (id == R.id.filter_objects) {
            currentFilter = "Objetos";
            filterPhotosByCategory(currentFilter);
        } else if (id == R.id.filter_people) {
            currentFilter = "Pessoas";
            filterPhotosByCategory(currentFilter);
        } else if (id == R.id.filter_buildings) {
            currentFilter = "Construções";
            filterPhotosByCategory(currentFilter);
        } else if (id == R.id.filter_animals) {
            currentFilter = "Animais";
            filterPhotosByCategory(currentFilter);
        } else if (id == R.id.filter_others) {
            currentFilter = "Outros";
            filterPhotosByCategory(currentFilter);
        }
        return super.onOptionsItemSelected(item);
    }


    private void filterPhotosByCategory(String category) {
        List<PhotoData> photosFromDb = dbHelper.getPhotosByCategory(category);
        photoDataList.clear();
        photoDataList.addAll(photosFromDb);
        refreshPhotoList();
    }
}