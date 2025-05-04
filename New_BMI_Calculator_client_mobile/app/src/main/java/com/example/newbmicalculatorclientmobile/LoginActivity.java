
package com.example.newbmicalculatorclientmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
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
import com.example.newbmicalculatorclientmobile.DashboardActivity;
import com.example.newbmicalculatorclientmobile.RegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnGoRegister;
    private static final String BASE_URL = "http://10.0.2.2:5000/v1/service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);

        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject data = new JSONObject();
            try {
                data.put("email", email);
                data.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL + "/login",
                    data,
                    response -> {
                        try {
                            String token = response.getString("token");
                            int userId = response.getInt("user_id");
                            String firstName = response.getString("first_name");
                            String lastName = response.getString("last_name");
                            String sex = response.getString("sex");

                            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                            sp.edit()
                                    .putString("token", token)
                                    .putInt("user_id", userId)
                                    .putString("first_name", firstName)
                                    .putString("last_name", lastName)
                                    .putString("sex", sex)
                                    .apply();

                            startActivity(new Intent(this, DashboardActivity.class));
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Erreur de parsing", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(this, "Identifiants invalides", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    java.util.Map<String, String> headers = new java.util.HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);

        });
    }
}
