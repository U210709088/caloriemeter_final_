package com.example.calorimeter5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> exerciseList;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public ExerciseAdapter(Context context, ArrayList<HashMap<String, String>> exerciseList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        HashMap<String, String> exercise = exerciseList.get(position);

        holder.tvExerciseName.setText(exercise.get("name"));
        holder.tvExerciseDetails.setText(exercise.get("duration") + " min - " + exercise.get("calories") + " kcal");

        holder.btnDeleteExercise.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvExerciseDetails;
        ImageButton btnDeleteExercise;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            tvExerciseDetails = itemView.findViewById(R.id.tvExerciseDetails);
            btnDeleteExercise = itemView.findViewById(R.id.btnDeleteExercise);
        }
    }
}
