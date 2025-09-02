package com.example.findit;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final Context context;
    private List<ItemModel> itemList;

    public ItemAdapter(Context context, List<ItemModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void updateList(List<ItemModel> updatedList) {
        this.itemList.clear();
        this.itemList.addAll(updatedList);
        notifyDataSetChanged();
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

        // ✅ Load image safely
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(item.getImagePath());
            if (bitmap != null) {
                holder.itemImage.setImageBitmap(bitmap);
            } else {
                holder.itemImage.setImageResource(R.drawable.ic_baseline_person_24); // Fallback
            }
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_baseline_person_24); // Default image
        }

        if (item.getContactInfo() != null && !item.getContactInfo().isEmpty()) {
            holder.itemContact.setText("Contact: " + item.getContactInfo());
            holder.itemContact.setVisibility(View.VISIBLE);
        } else {
            holder.itemContact.setVisibility(View.GONE);
        }


        // ✅ Check if current user is the owner
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : "";

        if (item.getUserId() != null && item.getUserId().equals(currentUserId)) {
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // ✅ Delete from local SQLite
                            DatabaseHelper dbHelper = new DatabaseHelper(context);
                            try {
                                dbHelper.deleteItem(Integer.parseInt(item.getId()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("ItemAdapter", "SQLite delete failed: " + e.getMessage());
                            }

                            // ✅ Delete from Firestore
                            FirebaseFirestore.getInstance()
                                    .collection("items")
                                    .whereEqualTo("name", item.getName())
                                    .whereEqualTo("date", item.getDate())
                                    .whereEqualTo("time", item.getTime())
                                    .whereEqualTo("userId", currentUserId)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        for (DocumentSnapshot doc : snapshot) {
                                            doc.getReference().delete();
                                        }
                                    })
                                    .addOnFailureListener(e ->
                                            Log.e("ItemAdapter", "Firestore delete failed: " + e.getMessage())
                                    );

                            // ✅ Remove from adapter list
                            itemList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, itemList.size());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemDesc, itemDetails, itemContact;
        ImageButton btnDelete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemDesc = itemView.findViewById(R.id.itemDesc);
            itemDetails = itemView.findViewById(R.id.itemDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            itemContact = itemView.findViewById(R.id.itemContact);

        }
    }
}
