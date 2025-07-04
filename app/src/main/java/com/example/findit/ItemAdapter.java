package com.example.findit;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final Context context;
    private final List<ItemModel> itemList;

    public ItemAdapter(Context context, List<ItemModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemModel item = itemList.get(position);

        holder.itemName.setText(item.getType() + " - " + item.getName());
        holder.itemDesc.setText(item.getDescription());
        holder.itemDetails.setText("Location: " + item.getLocation() +
                "\nDate: " + item.getDate() + "  Time: " + item.getTime());

        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            holder.itemImage.setImageBitmap(BitmapFactory.decodeFile(item.getImagePath()));
        } else {
            holder.itemImage.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemDesc, itemDetails;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemDesc = itemView.findViewById(R.id.itemDesc);
            itemDetails = itemView.findViewById(R.id.itemDetails);
        }
    }
}
