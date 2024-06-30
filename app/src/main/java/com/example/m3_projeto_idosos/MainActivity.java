package com.example.m3_projeto_idosos;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private boolean isFirstRun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        Button btnCapture = findViewById(R.id.btnCapture);
        recyclerView = findViewById(R.id.recyclerView);
        emptyAlbumTextView = findViewById(R.id.emptyAlbumTextView);

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

        // Recuperar fotos do banco de dados apenas se não for a primeira execução
        loadPhotosFromDatabase();
        updateAlbumVisibility();
        // dbHelper.clearAlbum();


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategorySelectionDialog();
            }
        });
    }

// Método para exibir o AlertDialog com opções de categoria
        private void showCategorySelectionDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecione a categoria");

            // Array de categorias disponíveis (exemplo)
            final String[] categories = {"Paisagem", "Objetos", "Pessoas", "Construções", "Animais", "Outros"};

            builder.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedCategory = categories[which];
                    dispatchTakePictureIntent(selectedCategory);
                }
            });

            builder.show();
        }

        @SuppressLint("QueryPermissionsNeeded")
        private void dispatchTakePictureIntent(String category) {
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
                    takePictureIntent.putExtra("CATEGORY", category); // Passando a categoria como extra
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
            if (data != null && data.hasExtra("CATEGORY")) {
                String category = data.getStringExtra("CATEGORY");
                updatePhotoList(currentPhotoPath, category);
            } else {
                // Lógica para definir uma categoria padrão caso não seja passada
                String defaultCategory = "Outros";
                updatePhotoList(currentPhotoPath, defaultCategory);
            }
            updateAlbumVisibility();
            refreshPhotoList();
        }
    }

    private void updatePhotoList(String photoPath, String category) {
        PhotoData photoData = new PhotoData();
        photoData.setPhotoUri(Uri.fromFile(new File(photoPath)));
        photoData.setTimestamp(new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()));
        photoData.setDescription("Adicione uma descrição");
        photoData.setCategory(category); // Definindo a categoria da foto

        Location location = getLocationFromImage(photoPath);
        if (location != null) {
            photoData.setLatitude(location.getLatitude());
            photoData.setLongitude(location.getLongitude());

            Log.d("PhotoData", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
        } else {
            Toast.makeText(this, "Não foi possível obter a localização da foto", Toast.LENGTH_SHORT).show();
            Log.e("PhotoData", "Não foi possível obter a localização da foto");
        }

        dbHelper.insertPhoto(photoData);
        photoDataList.add(photoData);

        Toast.makeText(this, "Foto capturada e adicionada ao álbum", Toast.LENGTH_SHORT).show();
        updateAlbumVisibility();
        refreshPhotoList();
    }

    private Location getLocationFromImage(String photoPath) {
        try {
            ExifInterface exif = new ExifInterface(photoPath);
            float[] latLong = new float[2];
            if (exif.getLatLong(latLong)) {
                Location location = new Location("");
                location.setLatitude(latLong[0]);
                location.setLongitude(latLong[1]);
                return location;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadPhotosFromDatabase() {
        List<PhotoData> photosFromDb = dbHelper.getAllPhotos();
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
        photoAdapter.notifyDataSetChanged();
    }
}
