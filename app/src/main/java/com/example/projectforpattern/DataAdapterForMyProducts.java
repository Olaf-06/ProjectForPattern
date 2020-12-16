package com.example.projectforpattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAdapterForMyProducts extends RecyclerView.Adapter<DataAdapterForMyProducts.ViewHolderProducts> implements DataAdapter{

    List<Products> ProductsList;
    LayoutInflater inflater;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;

    public class ViewHolderProducts extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameOfProductAdd, descriptionOfProductAdd;
        ImageView imgProductAdd, clearItemOfProducts;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolderProducts(@NonNull View itemView) {
            super(itemView);
            nameOfProductAdd = (TextView) itemView.findViewById(R.id.nameOfProductAdd);
            descriptionOfProductAdd = (TextView) itemView.findViewById(R.id.descriptionOfProductAdd);
            imgProductAdd = (ImageView) itemView.findViewById(R.id.imgProductAdd);
            clearItemOfProducts = (ImageView) itemView.findViewById(R.id.clearItemOfProductAdd);

            clearItemOfProducts.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.clearItemOfProductAdd) {
                db.collection("Products").document(ProductsList.get(getAdapterPosition()).photoID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                storageRef = storage.getReferenceFromUrl("gs://projectforpattern.appspot.com" +
                                        "photoOfProduct").child("Product" + ProductsList.get(getAdapterPosition()).photoID);
                                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        removeAt(getAdapterPosition());// File deleted successfully
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.d("logmy", "onFailure: фотка не удалилась");
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("logmy", "Error deleting document", e);
                            }
                        });

            }
        }
    }

    public void removeAt(int position) {
        ProductsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ProductsList.size());
    }

    public DataAdapterForMyProducts(Context context, ArrayList<Products> ProductsList){
        this.ProductsList = ProductsList;
        this.inflater = LayoutInflater.from(context);
        Log.d("logmy", "конструктор адаптера");
    }

    @NonNull
    @Override
    public DataAdapterForMyProducts.ViewHolderProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("logmy", "OnCreateViewHolder");
        View view = inflater.inflate(R.layout.item_product_add, parent, false);
        return new DataAdapterForMyProducts.ViewHolderProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataAdapterForMyProducts.ViewHolderProducts holder, int position) {
        Log.d("logmy", "onBindViewHolder");
        holder.nameOfProductAdd.setText(ProductsList.get(position).name);
        holder.descriptionOfProductAdd.setText(ProductsList.get(position).description);
        storageRef = storage.getReferenceFromUrl("gs://projectforpattern.appspot.com/" +
                "photoOfProduct").child("Product" + ProductsList.get(position).photoID);
        final File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.imgProductAdd.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("logmy", "onFailure: фотка не загрузилась ");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("logmy", "onBindViewHolder: ставлю на вьюшки значения");
    }

    @Override
    public int getItemCount() {
        if(ProductsList == null) {
            Log.d("logmy", "getItemCount: насчитал 0");
            return 0;
        }
        Log.d("logmy", "getItemCount: насчитал несколько");
        return ProductsList.size();
    }
}