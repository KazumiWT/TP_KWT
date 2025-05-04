package com.example.newbmicalculatorclientmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFirstName, edtLastName, edtAge, edtEmail, edtPassword;
    private RadioGroup radioSex;
    private Button btnRegister;

    private static final String BASE_URL = "http://10.0.2.2:5000/v1/service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtAge = findViewById(R.id.edtAge);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        radioSex = findViewById(R.id.radioSex);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            btnRegister.setEnabled(false); // Empêche les doubles clics

            String firstName = edtFirstName.getText().toString().trim();
            String lastName = edtLastName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim().toLowerCase();
            String password = edtPassword.getText().toString().trim();
            int age;
            try {
                age = Integer.parseInt(edtAge.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Âge invalide", Toast.LENGTH_SHORT).show();
                btnRegister.setEnabled(true);
                return;
            }

            String sex = radioSex.getCheckedRadioButtonId() == R.id.radioMale ? "Homme" : "Femme";

            JSONObject data = new JSONObject();
            try {
                data.put("first_name", firstName);
                data.put("last_name", lastName);
                data.put("email", email);
                data.put("password", password);
                data.put("age", age);
                data.put("sex", sex);
                data.put("debug_id", System.currentTimeMillis());
            } catch (JSONException e) {
                e.printStackTrace();
                btnRegister.setEnabled(true);
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL + "/register",
                    data,
                    response -> {
                        Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                        btnRegister.setEnabled(true); // Réactiver le bouton
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    },
                    error -> {
                        error.printStackTrace();
                        btnRegister.setEnabled(true); // Réactiver le bouton même en cas d’erreur

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject dataErr = new JSONObject(responseBody);
                                String errorMessage = dataErr.optString("error", "Erreur inconnue");
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, "Erreur inconnue", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Erreur réseau", Toast.LENGTH_LONG).show();
                        }
                    }
            );

            Volley.newRequestQueue(this).add(request);
        });
    }
}
