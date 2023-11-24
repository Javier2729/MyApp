package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class Ir extends AppCompatActivity {

    private PlacesClient placesClient;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir);

        String apiKey = "TU_CLAVE_DE_API_DE_PLACES";
        Places.initialize(getApplicationContext(), apiKey);
        placesClient = Places.createClient(this);

        ListView listView = findViewById(R.id.listViewResults);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        // Configurar clic en la lista para obtener detalles del lugar
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String placeId = adapter.getItem(position);
                if (placeId != null) {
                    // Lanzar Google Maps con detalles del lugar
                    launchGoogleMaps(placeId);
                }
            }
        });
    }

    private void performPlaceSearch(String keyword) {
        // Realizar una solicitud de predicciones de lugares
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("US") // Ajusta según tu región
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setQuery(keyword)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
            updateResultsList(predictions);
        }).addOnFailureListener((exception) -> {
            // Manejar errores
            Toast.makeText(this, "Error al buscar lugares: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateResultsList(List<AutocompletePrediction> predictions) {
        List<String> results = new ArrayList<>();
        for (AutocompletePrediction prediction : predictions) {
            results.add(prediction.getPlaceId());
        }
        adapter.clear();
        adapter.addAll(results);
        adapter.notifyDataSetChanged();
    }

    private void launchGoogleMaps(String placeId) {
        // Crear un Intent para abrir Google Maps con el lugar específico
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.google.com/maps/place/?q=place_id:" + placeId));
        startActivity(intent);
    }
}
