package com.example.projectforpattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class DataAdapterForAllProducts extends RecyclerView.Adapter<DataAdapterForAllProducts.ViewHolderProducts> implements DataAdapter {

    List<Products> ProductsList;
    LayoutInflater inflater;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;

    public class ViewHolderProducts extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameOfProduct, descriptionOfProduct;
        ImageView imgProduct;
        Button btnInBasket;
        LinearLayout linLayoutProduct;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolderProducts(@NonNull View itemView) {
            super(itemView);
            nameOfProduct = (TextView) itemView.findViewById(R.id.nameOfProduct);
            descriptionOfProduct = (TextView) itemView.findViewById(R.id.descriptionOfProduct);
            imgProduct = (ImageView) itemView.findViewById(R.id.imgProducts);
            btnInBasket = (Button) itemView.findViewById(R.id.addInBasket);
            linLayoutProduct = (LinearLayout) itemView.findViewById(R.id.linLayoutProduct);

            btnInBasket.setOnClickListener(this);
            linLayoutProduct.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.addInBasket) {
                Map<String, Object> userdb = new HashMap<>();
                userdb.put("name", nameOfProduct.getText().toString());
                userdb.put("description", descriptionOfProduct.getText().toString());
                userdb.put("photoID", "" + idForPhoto(getAdapterPosition()));
                Log.d("logmy", "Cей час будем добавлять документ в коллекцию");
                db.collection("Basket").document("" + ProductsList.get(getAdapterPosition()).photoID)
                        .set(userdb);
            }
        }
    }

    public String idForPhoto(int position){
        return ProductsList.get(position).photoID;
    }

    public DataAdapterForAllProducts(Context context, ArrayList<Products> ProductsList){
        this.ProductsList = ProductsList;
        this.inflater = LayoutInflater.from(context);
        Log.d("logmy", "конструктор адаптера");
    }

    @NonNull
    @Override
    public DataAdapterForAllProducts.ViewHolderProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("logmy", "OnCreateViewHolder");
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new DataAdapterForAllProducts.ViewHolderProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataAdapterForAllProducts.ViewHolderProducts holder, int position) {
        Log.d("logmy", "onBindViewHolder");
        holder.nameOfProduct.setText(ProductsList.get(position).name);
        holder.descriptionOfProduct.setText(ProductsList.get(position).description);
        storageRef = storage.getReferenceFromUrl("gs://projectforpattern.appspot.com/" +
                "photoOfProduct").child("Product" + ProductsList.get(position).photoID);
        final File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.imgProduct.setImageBitmap(bitmap);
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

