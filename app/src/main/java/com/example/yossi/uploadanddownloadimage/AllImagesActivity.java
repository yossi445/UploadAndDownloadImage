package com.example.yossi.uploadanddownloadimage;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllImagesActivity extends AppCompatActivity {


    ListView lv;
    ArrayList<User> usersList;
    AllUsersAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_images);


        lv = findViewById(R.id.lv);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        retrieveData();

    }

    private void retrieveData() {


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList = new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    User user = data.getValue(User.class);
                    usersList.add(user);
                }


                adapter = new AllUsersAdapter(AllImagesActivity.this,0,0,usersList);
                lv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
