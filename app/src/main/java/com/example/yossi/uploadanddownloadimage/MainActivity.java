package com.example.yossi.uploadanddownloadimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

     Button btn;
     StorageReference mStorageRef;
     DatabaseReference databaseReference;
     ProgressDialog progressDialog;
     Uri imageUri;

     ImageView iv;


     static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        iv = findViewById(R.id.iv);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("users");


    }

    @Override
    public void onClick(View v) {

        openFileChooser();
    }

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK)
        {
            imageUri = data.getData();

            uploadFile();
        }
    }

    private void uploadFile() {

        final String fileChildId = String.valueOf(System.currentTimeMillis());
         StorageReference fileRef = mStorageRef.child(fileChildId);
        progressDialog.setMessage("uploading image...");
        progressDialog.show();

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "upload complete", Toast.LENGTH_SHORT).show();
                User user = new User("popo",taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                String userId = databaseReference.push().getKey();
                databaseReference.child(userId).setValue(user);

                //-------

                /*String url  = taskSnapshot.getDownloadUrl().toString();
                Picasso.get().load(url).into(iv);*/

                /*String url = String.valueOf(fileRef.getDownloadUrl().getException());
                Picasso.get().load(url).into(iv);*/



                mStorageRef.child(fileChildId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png' in uri
                        String url = uri.toString();
                        Picasso.get().load(uri).into(iv);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });




            }



        });


    }
}
