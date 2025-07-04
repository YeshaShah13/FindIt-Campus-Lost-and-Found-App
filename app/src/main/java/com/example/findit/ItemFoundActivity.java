package com.example.findit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ItemFoundActivity extends AppCompatActivity {

    EditText etName, etDescription, etLocation;
    Button btnDate, btnTime, btnUpload, btnSubmit;
    ImageView imgPreview;
    String selectedDate = "", selectedTime = "", imagePath = "";

    DatabaseHelper dbHelper;
    Uri imageUri = null;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(this, imageUri);
                    if (bitmap != null) {
                        imagePath = ImageUtils.saveImageToInternalStorage(this, bitmap);
                        imgPreview.setImageBitmap(bitmap);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    if (bitmap != null) {
                        imagePath = ImageUtils.saveImageToInternalStorage(this, bitmap);
                        imgPreview.setImageBitmap(bitmap);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_found); // Make sure you copy the XML and rename it

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etItemDescription);
        etLocation = findViewById(R.id.etLocation);
        btnDate = findViewById(R.id.btnSelectDate);
        btnTime = findViewById(R.id.btnSelectTime);
        btnUpload = findViewById(R.id.btnUploadImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPreview = findViewById(R.id.imgPreview);

        btnDate.setOnClickListener(v -> showDatePicker());
        btnTime.setOnClickListener(v -> showTimePicker());
        btnUpload.setOnClickListener(v -> showImageChooserDialog());

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();
            String loc = etLocation.getText().toString().trim();

            if (name.isEmpty() || desc.isEmpty() || loc.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ItemModel item = new ItemModel(
                    0, "Found", name, desc, loc, selectedDate, selectedTime, imagePath
            );

            dbHelper.insertItem(item);

            Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedDate = (month + 1) + "/" + day + "/" + year;
                    btnDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hour, minute) -> {
                    selectedTime = String.format("%02d:%02d", hour, minute);
                    btnTime.setText(selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        dialog.show();
    }

    private void showImageChooserDialog() {
        String[] options = {"Camera", "Gallery"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Choose Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraLauncher.launch(cameraIntent);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryLauncher.launch(galleryIntent);
                    }
                })
                .show();
    }
}
