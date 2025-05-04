package com.example.newbmicalculatorclientmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {

    private EditText edtWeight, edtHeight, edtWaist;
    private TextView txtWelcome;
    private Button btnSubmit, btnSeeHistory, btnLogout;

    private static final String BASE_URL = "http://10.0.2.2:5000/v1/service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtWeight = findViewById(R.id.edtWeight);
        edtHeight = findViewById(R.id.edtHeight);
        edtWaist = findViewById(R.id.edtWaist);
        txtWelcome = findViewById(R.id.txtWelcome);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSeeHistory = findViewById(R.id.btnSeeHistory);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String token = sp.getString("token", "");
        int userId = sp.getInt("user_id", -1);
        String first = sp.getString("first_name", "");
        String last = sp.getString("last_name", "");
        String sex = sp.getString("sex", "");

        txtWelcome.setText("Bienvenue " + first + " " + last);

        btnSubmit.setOnClickListener(v -> {
            try {
                double weight = Double.parseDouble(edtWeight.getText().toString());
                double height = Double.parseDouble(edtHeight.getText().toString());
                double waist = Double.parseDouble(edtWaist.getText().toString());

                JSONObject data = new JSONObject();
                data.put("user_id", userId);
                data.put("weight", weight);
                data.put("height", height);
                data.put("waist", waist);
                data.put("sex", sex);

                JsonObjectRequest req = new JsonObjectRequest(
                        Request.Method.POST,
                        BASE_URL + "/bmi",
                        data,
                        response -> Toast.makeText(this, "Enregistré avec succès !", Toast.LENGTH_SHORT).show(),
                        error -> Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show()
                ) {
                    @Override
                    public java.util.Map<String, String> getHeaders() {
                        java.util.Map<String, String> headers = new java.util.HashMap<>();
                        headers.put("Authorization", token);
                        return headers;
                    }
                };

                Volley.newRequestQueue(this).add(req);

            } catch (Exception e) {
                Toast.makeText(this, "Veuillez remplir les champs correctement", Toast.LENGTH_SHORT).show();
            }
        });

        btnSeeHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            sp.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
