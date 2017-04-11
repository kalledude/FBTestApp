package com.example.kalle.firebasetestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import Models.User;

public class MainActivity extends AppCompatActivity {

    private String userId;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView textDetails;
    private EditText inputName, inputEmail;
    private Button btnUpdate;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFireBaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        textDetails = (TextView) findViewById(R.id.text_header);
        inputName = (EditText) findViewById(R.id.editText_name);
        inputEmail = (EditText) findViewById(R.id.editText_email);
        btnUpdate = (Button) findViewById(R.id.button_update);

        mFireBaseInstance = FirebaseDatabase.getInstance();
        mFireBaseDatabase = mFireBaseInstance.getReference();

        mFireBaseInstance.getReference("app_title").setValue("Realtime Database");
        mFireBaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);
                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to update.", databaseError.toException());
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();

                if(TextUtils.isEmpty(userId)) {
                    createUser(name, email);
                }
                    else{
                        updateUser(name, email);
                    }
                }
        });


    }

    private void updateUser(String name, String email) {
        if(!TextUtils.isEmpty(userId))
        {
            mFireBaseDatabase.child(userId).child("name").setValue(name);
            mFireBaseDatabase.child(userId).child("email").setValue(email);
        }
    }

    private void createUser(String name, String email) {
        if(TextUtils.isEmpty(userId))
        {
            userId = mFireBaseDatabase.push().getKey();
        }
        User user = new User(name, email);
        mFireBaseDatabase.child(userId).setValue(user);
        addUserChangeListener();
    }

    private void addUserChangeListener() {
        mFireBaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if(user == null)
                {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email);

                textDetails.setText(user.name + ", " + user.email);
                inputEmail.setText("");
                inputName.setText("");

                toggleButton();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void toggleButton() {
        if(TextUtils.isEmpty(userId))
        {
            btnUpdate.setText("Save");
        }
        else
        {
            btnUpdate.setText("Update");
        }
    }
}
