package com.example.m3_projeto_idosos;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PhotoData implements Parcelable {
    private Uri photoUri;
    private double latitude;
    private double longitude;
    private String timestamp;
    private String description;

    public PhotoData() {
        // Construtor vazio necessário para Parcelable
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Implementação de Parcelable
    protected PhotoData(Parcel in) {
        photoUri = in.readParcelable(Uri.class.getClassLoader());
        latitude = in.readDouble();
        longitude = in.readDouble();
        timestamp = in.readString();
        description = in.readString();
    }

    public static final Creator<PhotoData> CREATOR = new Creator<PhotoData>() {
        @Override
        public PhotoData createFromParcel(Parcel in) {
            return new PhotoData(in);
        }

        @Override
        public PhotoData[] newArray(int size) {
            return new PhotoData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(photoUri, flags);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(timestamp);
        dest.writeString(description);
    }
}