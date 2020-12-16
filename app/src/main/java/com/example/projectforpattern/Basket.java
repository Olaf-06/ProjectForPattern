package com.example.projectforpattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Basket extends AppCompatActivity {

    RecyclerView RVProducts;
    ArrayList<Products> products = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        RVProducts = (RecyclerView) findViewById(R.id.basket_recycler);
        RVProducts.setLayoutManager(new LinearLayoutManager(this));
        final DataAdapterBasket adapter = new DataAdapterBasket(this, products);
        RVProducts.setAdapter(adapter);

        db.collection("Basket")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Products productsClass = document.toObject(Products.class);
                                products.add(new Products(productsClass.name, productsClass.description, productsClass.photoID, productsClass.userID));
                                adapter.notifyDataSetChanged();
                            }
                            Log.d("logmy", "прогрузились документы");
                        } else {
                            Log.w("logmy", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}