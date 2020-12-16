package com.example.projectforpattern.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectforpattern.EditProfile;
import com.example.projectforpattern.LoginActivity;
import com.example.projectforpattern.MyProducts;
import com.example.projectforpattern.MyPurchase;
import com.example.projectforpattern.R;
import com.example.projectforpattern.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Profile extends Fragment implements View.OnClickListener {

    ImageView img;
    TextView txtFirstName, txtLastName;
    Button btnEditName, btnExit, btnMyProducts, btnMyPurchase;
    SharedPreferences mySharedPreference;
    public static final String APP_PREFERENCES_SETTINGS_OF_ENTRY = "entry";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        img = (ImageView) root.findViewById(R.id.imageView);
        txtFirstName = (TextView) root.findViewById(R.id.txtFirstName);
        txtLastName = (TextView) root.findViewById(R.id.txtLastName);
        btnEditName = (Button) root.findViewById(R.id.btnEditName);
        btnExit = (Button) root.findViewById(R.id.btnExit);
        btnMyProducts = (Button) root.findViewById(R.id.btnMyProducts);
        btnMyPurchase = (Button) root.findViewById(R.id.btnMyPurchases);

        btnMyPurchase.setOnClickListener(this);
        btnMyProducts.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnEditName.setOnClickListener(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://projectforpattern.appspot.com/photoOfUsers").child(user.getUid());
        final File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    img.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Users users = documentSnapshot.toObject(Users.class);
                    if(!users.getFirstName().isEmpty() && !users.getLastName().isEmpty()){
                        txtFirstName.setText(users.getFirstName());
                        txtLastName.setText(users.getLastName());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEditName:
                Intent intent1 = new Intent(getContext(), EditProfile.class);
                startActivity(intent1);
                break;
            case R.id.btnExit:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intent);
                break;
            case R.id.btnMyProducts:
                Intent intent3 = new Intent(getContext(), MyProducts.class);
                startActivity(intent3);
                break;
            case R.id.btnMyPurchases:
                Intent intent2 = new Intent(getContext(), MyPurchase.class);
                startActivity(intent2);
                break;
        }
    }
}