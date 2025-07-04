package com.example.findit;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.*;

public class ImageUtils {

    // Save Bitmap image to internal storage and return the file path
    public static String saveImageToInternalStorage(Context context, Bitmap bitmap) {
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FindItImages");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert a URI to Bitmap
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
