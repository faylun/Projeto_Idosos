package com.example.m3_projeto_idosos;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationHelper {

    private static final String TAG = "LocationHelper";

    // Método estático para obter a última localização conhecida do dispositivo
    public static Location getLastKnownLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            // Verifica permissão de acesso à localização
            if (locationManager != null && context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    return location;
                }
            } else {
                Toast.makeText(context, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Erro ao obter a última localização conhecida: " + e.getMessage());
        }
        return null;
    }

    // Método estático para configurar o serviço de atualizações de localização
    public static void configureLocationService(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Ação a ser tomada quando a localização muda
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // Ação a ser tomada quando o status do provedor de localização muda
                }

                public void onProviderEnabled(String provider) {
                    // Ação a ser tomada quando o provedor de localização é ativado
                }

                public void onProviderDisabled(String provider) {
                    // Ação a ser tomada quando o provedor de localização é desativado
                }
            };
            // Solicita atualizações de localização através do provedor GPS_PROVIDER
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException ex) {
            Toast.makeText(context, "Erro ao configurar o serviço de localização: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
