package com.example.newbmicalculatorclientmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> historyList;
    private Context context;

    public HistoryAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History h = historyList.get(position);
        holder.txtDate.setText("Date: " + h.date);
        holder.txtWeight.setText("Poids: " + h.weight + " kg");
        holder.txtHeight.setText("Taille: " + h.height + " cm");
        holder.txtWaist.setText("Tour taille: " + h.waist + " cm");
        holder.txtBMI.setText("IMC: " + h.bmi);
        holder.txtClassification.setText("Classification: " + h.bmi_classification);
        holder.txtMeasure.setText("Mesure: " + h.new_measure);

        holder.btnDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer cette entrée ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                        String token = sp.getString("token", "");
                        int historyId = h.history_id;

                        String url = "http://10.0.2.2:5000/v1/service/history/" + historyId;

                        //  désactiver le bouton temporairement
                        holder.btnDelete.setEnabled(false);

                        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                                response -> {
                                    Toast.makeText(context, "Supprimé", Toast.LENGTH_SHORT).show();
                                    historyList.remove(position);
                                    notifyItemRemoved(position);
                                },
                                error -> {
                                    Toast.makeText(context, "Erreur de suppression", Toast.LENGTH_SHORT).show();
                                    holder.btnDelete.setEnabled(true); // Réactiver en cas d'erreur
                                }) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", token);
                                return headers;
                            }
                        };

                        Volley.newRequestQueue(context).add(request);
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });



    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtWeight, txtHeight, txtWaist, txtBMI, txtClassification, txtMeasure;
        View btnDelete;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtWeight = itemView.findViewById(R.id.txtWeight);
            txtHeight = itemView.findViewById(R.id.txtHeight);
            txtWaist = itemView.findViewById(R.id.txtWaist);
            txtBMI = itemView.findViewById(R.id.txtBMI);
            txtClassification = itemView.findViewById(R.id.txtClassification);
            txtMeasure = itemView.findViewById(R.id.txtMeasure);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
