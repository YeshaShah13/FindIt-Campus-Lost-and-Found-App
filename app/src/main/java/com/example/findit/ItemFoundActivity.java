package com.example.findit;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.HashMap;
import java.util.Map;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Calendar;

public class ItemFoundActivity extends AppCompatActivity {

    EditText etName, etDescription, etLocation, etContactInfo;
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
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getExtras() != null) {
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    if (bitmap != null) {
                        imagePath = ImageUtils.saveImageToInternalStorage(this, bitmap);
                        imgPreview.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(this, "Camera image not captured", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Camera capture canceled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_found);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etItemDescription);
        etLocation = findViewById(R.id.etLocation);
        etContactInfo = findViewById(R.id.etContactInfo);
        btnDate = findViewById(R.id.btnSelectDate);
        btnTime = findViewById(R.id.btnSelectTime);
        btnUpload = findViewById(R.id.btnUploadImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPreview = findViewById(R.id.imgPreview);

        btnDate.setOnClickListener(v -> showDatePicker());
        btnTime.setOnClickListener(v -> showTimePicker());
        btnUpload.setOnClickListener(v -> showImageChooserDialog());

        btnSubmit.setOnClickListener(v -> submitItem());
    }

    public void uploadToFirebase(ItemModel item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("type", item.getType());
        itemMap.put("name", item.getName());
        itemMap.put("description", item.getDescription());
        itemMap.put("location", item.getLocation());
        itemMap.put("date", item.getDate());
        itemMap.put("time", item.getTime());
        itemMap.put("imagePath", item.getImagePath());
        itemMap.put("contactInfo", item.getContactInfo());
        itemMap.put("userId", item.getUserId());

        db.collection("items")
                .add(itemMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FirebaseUpload", "Item uploaded: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseUpload", "Upload failed", e);
                });
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedDate = (month + 1) + "/" + day + "/" + year;
                    btnDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hour, minute) -> {
                    selectedTime = String.format("%02d:%02d", hour, minute);
                    btnTime.setText(selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false);
        dialog.show();
    }

    private void showImageChooserDialog() {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Choose Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndOpenCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private void submitItem() {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String loc = etLocation.getText().toString().trim();
        String contact = etContactInfo.getText().toString().trim();


        // ✅ Validate fields
        if (name.isEmpty() || desc.isEmpty() || loc.isEmpty()
                || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Get current Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid(); // ✅ Safe to use

        // ✅ Create item and set userId
        ItemModel item = new ItemModel(
                "0", // ID auto-incremented in SQLite
                "Found",
                name,
                desc,
                loc,
                selectedDate,
                selectedTime,
                imagePath
        );
        item.setUserId(userId); // ✅ Important for delete button to work
        item.setContactInfo(contact);

        // ✅ Save to SQLite
        dbHelper.insertItem(item);

        // ✅ Save to Firestore and show Toast/log
        FirebaseFirestore.getInstance()
                .collection("items")
                .add(item)
                .addOnSuccessListener(docRef -> {
                    Log.d("FIRESTORE_UPLOAD", "Uploaded to Firestore: " + docRef.getId());
                    Toast.makeText(this, "Uploaded to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Upload failed: " + e.getMessage());

                });

        Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();

        // ✅ Navigate to Dashboard
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    // Handle camera permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
