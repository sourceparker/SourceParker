package com.siat.diayan.sourceparker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtEmail;
    private EditText editPassword;
    private EditText edtPhoneNumber;
    private Button btnSignUp;

    User userDetails = new User();

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtFirstName = findViewById(R.id.editFirstName);
        edtLastName = findViewById(R.id.editLastName);
        edtEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.edtPassword);
        edtPhoneNumber = findViewById(R.id.editPhone);

        btnSignUp = findViewById(R.id.btnSignup);


        //creating an instance of firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //setting state listeners to look out for any changes in state
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Toast.makeText(SignUpActivity.this, "authentication success", Toast.LENGTH_SHORT).show();

//                    next activity comes here
                    Intent intent = new Intent(SignUpActivity.this, MapsActivity.class);
                    startActivity(intent);

                }
            }
        };

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignUp();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private void startSignUp() {
        //if isUser is true
        String email = edtEmail.getText().toString();
        String password = editPassword.getText().toString();

        //forces the user to fill all the fields instead of just edtEmail and editPassword
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(edtFirstName.getText().toString())
                || TextUtils.isEmpty(edtLastName.getText().toString())
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(edtPhoneNumber.getText().toString())) {
            Toast.makeText(this, "fill all fields to sign up", Toast.LENGTH_LONG).show();
        } else {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        mDatabase = FirebaseDatabase.getInstance();
                        mDatabaseReference = mDatabase.getReference();
                        userDetails = new User(edtFirstName.getText().toString(), edtLastName.getText().toString(),
                                edtEmail.getText().toString(), editPassword.getText().toString(),
                                edtPhoneNumber.getText().toString());
                        mDatabaseReference.child("User")
                                .push().setValue(userDetails);

                        edtFirstName.setText("");
                        edtLastName.setText("");
                        edtEmail.setText("");
                        editPassword.setText("");
                        edtPhoneNumber.setText("");

                        //
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(SignUpActivity.this, "Sign up success", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();



                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
