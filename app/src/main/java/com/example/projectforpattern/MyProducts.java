package com.example.projectforpattern;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyProducts extends AppCompatActivity implements View.OnClickListener {

    RecyclerView RVProducts;
    ArrayList<Products> Products = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FloatingActionButton floatingActionButton;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);
        RVProducts = (RecyclerView) findViewById(R.id.my_products_recycler);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        RVProducts.setLayoutManager(new LinearLayoutManager(this));
        final DataAdapterForMyProducts adapter = new DataAdapterForMyProducts(this, Products);
        RVProducts.setAdapter(adapter);

        db.collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Products ProductsClass = document.toObject(Products.class);
                                if(ProductsClass.userID.equals(uid)){
                                    Log.d("logmy", "равенство выполнено");
                                    Products.add(new Products(ProductsClass.name, ProductsClass.description, ProductsClass.photoID, ProductsClass.userID));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            Log.d("logmy", "прогрузились документы");
                        } else {
                            Log.w("logmy", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                Intent intent = new Intent(MyProducts.this, AddProduct.class);
                startActivity(intent);
                break;
        }
    }
}