package com.example.projectforpattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mySharedPreferences";
    public static final String APP_PREFERENCES_NAME = "username";
    public static final String APP_PREFERENCES_SETTINGS_OF_ENTRY = "entry";
    public static final String APP_PREFERENCES_SETTINGS_OF_REGISTRATION = "reg";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    private SharedPreferences mySharedPreferences;
    private Boolean entryBool, registration;
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mySharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        entryBool = mySharedPreferences.getBoolean(APP_PREFERENCES_SETTINGS_OF_ENTRY, true);
        nickName = mySharedPreferences.getString(APP_PREFERENCES_NAME, "");
        registration = mySharedPreferences.getBoolean(APP_PREFERENCES_SETTINGS_OF_REGISTRATION, false);
        if (entryBool) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && registration) {
                String uid = user.getUid();

                Map<String, Object> userdb = new HashMap<>();
                userdb.put("adminRights", "0");
                userdb.put("firstName", "");
                userdb.put("lastName", "");
                userdb.put("urlPhoto", "");
                Log.d("logmy", "Аутентификация прошла и сейчас будем добавлять документ в коллекцию");
                db.collection("users").document(uid)
                        .set(userdb)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                            }
                        });
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putBoolean(APP_PREFERENCES_SETTINGS_OF_REGISTRATION, false);
                editor.apply();
            }
            Log.d("logmy", "onCreate");
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart){
            Intent intent = new Intent(MainActivity.this, Basket.class);
            startActivity(intent);
        }
        return true;
    }
}