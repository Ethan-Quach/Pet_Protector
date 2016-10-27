package edu.orangecoastcollege.cs273.equach3.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;


public class PetListActivity extends AppCompatActivity {

    private ImageView petImageView;

    // This member variable stores the URI to whatever image has been selected
    // Default: none.jpg (R.drawable.none)
    private Uri imageURI;

    private static final int REQUEST_CODE = 573;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list);

        petImageView = (ImageView) findViewById(R.id.petImageView);

        // Constructs a fullURI to any Android resource (id, drawable, color, layout, etc.)
        imageURI = getUriToResource(this, R.drawable.none);

        petImageView.setImageURI(imageURI);
    }

    public void selectPetImage (View view) {

        // List of permissions we need to request from the user
        ArrayList<String> permList = new ArrayList<>();

        // Do we have permission to camera? Reading external storage? Writing to external storage?
        // If not, add permissions to permList.
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermission != PackageManager.PERMISSION_GRANTED)
            permList.add(Manifest.permission.CAMERA);

        int readExStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readExStorage != PackageManager.PERMISSION_GRANTED)
            permList.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        int writeExStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeExStorage != PackageManager.PERMISSION_GRANTED)
            permList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If the permList has items (i.e. size > 0), we need to request permissions from the user.
        if (permList.size() > 0) {
            String[] args = new String[permList.size()];


            //Request permissions from user
            ActivityCompat.requestPermissions(this, permList.toArray(args), REQUEST_CODE);
        }

        // If we have all three permissions, open ImageGallery
        if (cameraPermission == PackageManager.PERMISSION_GRANTED &&
                readExStorage == PackageManager.PERMISSION_GRANTED &&
                writeExStorage == PackageManager.PERMISSION_GRANTED) {

            // Use an Intent to launch the gallery and take pictures
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_CODE);
        }
        else
            Toast.makeText(this, "Pet Protector requires camera and external storage permissions!", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // This is code to handle when the user closes the gallery,
        // either by selecting an image or pressing the back button.

        // The Intent data is the URI selected from the image gallery.

        // Did the user select an image?
        if (data != null && requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Set the data to the URI received
            imageURI = data.getData();
            petImageView.setImageURI(imageURI);
        }
    }

    public static Uri getUriToResource(@NonNull Context context, @AnyRes int resId) throws Resources.NotFoundException {
        Resources res = context.getResources();

        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
        "://" + res.getResourcePackageName(resId)
        + '/' + res.getResourceTypeName(resId)
        + '/' + res.getResourceEntryName(resId));
    }
}
