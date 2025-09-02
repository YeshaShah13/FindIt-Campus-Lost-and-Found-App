package com.example.findit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.*;
import com.google.firebase.FirebaseApp;


import java.util.*;

public class DashboardActivity extends AppCompatActivity {

    Button btnItemLost, btnItemFound;
    Spinner spinnerFilter, spinnerSort;
    RecyclerView recyclerViewItems;
    SearchView searchView;

    ItemAdapter adapter;
    List<ItemModel> itemList, originalList;

    DatabaseHelper dbHelper;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_dashboard);

        // Initialize Views
        btnItemLost = findViewById(R.id.btnItemLost);
        btnItemFound = findViewById(R.id.btnItemFound);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        spinnerSort = findViewById(R.id.spinnerSort);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        searchView = findViewById(R.id.searchView);

        // Firebase and SQLite
        db = FirebaseFirestore.getInstance();
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // RecyclerView setup
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        originalList = new ArrayList<>();
        adapter = new ItemAdapter(this, itemList);
        recyclerViewItems.setAdapter(adapter);

        // Sync from Firestore and load into SQLite
        syncFromFirestore();

        // Setup filter spinner
        String[] filterOptions = {"All", "Lost", "Found"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterOptions);
        spinnerFilter.setAdapter(filterAdapter);
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterItems(filterOptions[position]);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Setup sort spinner
        String[] sortOptions = {"Sort by Name", "Sort by Date"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sortOptions);
        spinnerSort.setAdapter(sortAdapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) sortByName();
                else sortByDate();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                searchItems(newText);
                return true;
            }
        });

        btnItemLost.setOnClickListener(v -> startActivity(new Intent(this, ItemLostActivity.class)));
        btnItemFound.setOnClickListener(v -> startActivity(new Intent(this, ItemFoundActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItemsFromSQLite();  // Always refresh from local DB
    }

    private void syncFromFirestore() {
        db.collection("items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            ItemModel item = doc.toObject(ItemModel.class);

                            if (item.getName() != null && item.getDate() != null && item.getTime() != null) {
                                if (!dbHelper.itemExists(item)) {
                                    dbHelper.insertItem(item);
                                }
                            }

                        } catch (Exception e) {
                            Log.e("SYNC", "Parse error: " + e.getMessage());
                        }
                    }

                    loadItemsFromSQLite(); // Refresh after sync
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Sync failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadItemsFromSQLite() {
        itemList = dbHelper.getAllItems();
        originalList = new ArrayList<>(itemList);
        adapter.updateList(itemList);
    }

    private void filterItems(String type) {
        if (type.equals("All")) {
            itemList.clear();
            itemList.addAll(originalList);
        } else {
            List<ItemModel> tempList = new ArrayList<>();
            for (ItemModel item : originalList) {
                String itemType = item.getType() != null ? item.getType().toLowerCase() : "";
                if (itemType.equals(type.toLowerCase())) {
                    tempList.add(item);
                }
            }
            itemList.clear();
            itemList.addAll(tempList);
        }

        adapter.notifyDataSetChanged();
    }

    private void searchItems(String query) {
        itemList.clear();

        for (ItemModel item : originalList) {
            String name = item.getName() != null ? item.getName().toLowerCase() : "";
            String desc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
            String location = item.getLocation() != null ? item.getLocation().toLowerCase() : "";

            if (name.contains(query.toLowerCase()) ||
                    desc.contains(query.toLowerCase()) ||
                    location.contains(query.toLowerCase())) {
                itemList.add(item);
            }
        }

        adapter.notifyDataSetChanged();
    }


    private void sortByName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            itemList.sort((item1, item2) -> {
                String name1 = item1.getName();
                String name2 = item2.getName();

                if (name1 == null) return 1;  // nulls go to end
                if (name2 == null) return -1;

                return name1.compareToIgnoreCase(name2);
            });
        }
        adapter.notifyDataSetChanged();
    }


    private void sortByDate() {
        itemList.sort(Comparator.comparing(ItemModel::getDate));
        adapter.notifyDataSetChanged();
    }
}
