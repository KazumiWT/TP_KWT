package com.example.newbmicalculatorclientmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private Button btnBack, btnLogout;

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<History> historyList = new ArrayList<>();
    private static final String BASE_URL = "http://10.0.2.2:5000/v1/service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(v -> {
            finish(); // Retourne à DashboardActivity
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            sp.edit().clear().apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String token = sp.getString("token", "");
        int userId = sp.getInt("user_id", -1);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL + "/history/" + userId,
                null,
                response -> {
                    historyList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            History h = new History();
                            h.history_id = obj.getInt("history_id");
                            h.date = obj.getString("date");
                            h.weight = obj.getDouble("weight");
                            h.height = obj.getDouble("height");
                            h.waist = obj.getDouble("waist");
                            h.bmi = obj.getDouble("bmi");
                            h.bmi_classification = obj.getString("bmi_classification");
                            h.new_measure = obj.getString("new_measure");
                            historyList.add(h);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter = new HistoryAdapter(this, historyList);
                    recyclerView.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Erreur de récupération", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
