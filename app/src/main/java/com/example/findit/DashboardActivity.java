package com.example.findit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    Button btnItemLost, btnItemFound;
    RecyclerView recyclerViewItems;
    ItemAdapter adapter;
    DatabaseHelper dbHelper;
    List<ItemModel> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // View bindings
        btnItemLost = findViewById(R.id.btnItemLost);
        btnItemFound = findViewById(R.id.btnItemFound);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);

        dbHelper = new DatabaseHelper(this);
        itemList = dbHelper.getAllItems();

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(this, itemList);
        recyclerViewItems.setAdapter(adapter);

        btnItemLost.setOnClickListener(v -> startActivity(new Intent(this, ItemLostActivity.class)));

        btnItemFound.setOnClickListener(v -> startActivity(new Intent(this, ItemFoundActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshItems();
    }

    private void refreshItems() {
        itemList.clear();
        itemList.addAll(dbHelper.getAllItems());
        adapter.notifyDataSetChanged();
    }
}
