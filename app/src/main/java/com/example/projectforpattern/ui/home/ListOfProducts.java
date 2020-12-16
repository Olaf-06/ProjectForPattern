package com.example.projectforpattern.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforpattern.DataAdapterForAllProducts;
import com.example.projectforpattern.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import com.example.projectforpattern.Products;

public class ListOfProducts extends Fragment {

    RecyclerView RVProducts;
    ArrayList<Products> products = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RVProducts = (RecyclerView) view.findViewById(R.id.products_recycler);
        RVProducts.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        final DataAdapterForAllProducts adapter = new DataAdapterForAllProducts(this.getActivity(), products);
        RVProducts.setAdapter(adapter);

        db.collection("Products")
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
        return view;
    }
}



