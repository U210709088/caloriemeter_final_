package com.example.calorimeter5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> foodList;
    private OnFoodItemDeleteListener deleteListener;

    public FoodAdapter(Context context, ArrayList<HashMap<String, String>> foodList, OnFoodItemDeleteListener deleteListener) {
        this.context = context;
        this.foodList = foodList;
        this.deleteListener = deleteListener;
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
            holder = new ViewHolder();
            holder.tvFoodName = convertView.findViewById(R.id.tvFoodName);
            holder.tvFoodDetails = convertView.findViewById(R.id.tvFoodDetails);
            holder.ivFoodImage = convertView.findViewById(R.id.ivFoodImage);
            holder.btnDelete = convertView.findViewById(R.id.btnDeleteFood);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> foodItem = foodList.get(position);

        holder.tvFoodName.setText(foodItem.get("name"));
        holder.tvFoodDetails.setText(foodItem.get("calories") + " kcal - " + foodItem.get("weight") + " g");

        // Picasso ile resim yükleme
        String photoUrl = foodItem.get("photo");
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.placeholder_image_foreground)
                    .error(R.drawable.error_image_foreground)
                    .into(holder.ivFoodImage);
        } else {
            holder.ivFoodImage.setImageResource(R.drawable.placeholder_image_foreground);
        }

        // Silme düğmesi tıklama olayı
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvFoodName, tvFoodDetails;
        ImageView ivFoodImage;
        Button btnDelete;
    }

    public interface OnFoodItemDeleteListener {
        void onDelete(int position);
    }
}
