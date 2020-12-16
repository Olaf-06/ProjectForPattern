package com.example.projectforpattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class DataAdapterForMyPurchase extends RecyclerView.Adapter<DataAdapterForMyPurchase.ViewHolderPurchase> implements DataAdapter {

    List<Products> PurchaseList;
    LayoutInflater inflater;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;

    public class ViewHolderPurchase extends RecyclerView.ViewHolder{

        TextView nameOfPurchase, descriptionOfPurchase;
        ImageView imgPurchase;

        public ViewHolderPurchase(@NonNull View itemView) {
            super(itemView);
            nameOfPurchase = (TextView) itemView.findViewById(R.id.nameOfPurchase);
            descriptionOfPurchase = (TextView) itemView.findViewById(R.id.descriptionOfPurchase);
            imgPurchase = (ImageView) itemView.findViewById(R.id.imgPurchases);
        }
    }

    public DataAdapterForMyPurchase(Context context, ArrayList<Products> PurchaseList){
        this.PurchaseList = PurchaseList;
        this.inflater = LayoutInflater.from(context);
        Log.d("logmy", "конструктор адаптера");
    }

    @NonNull
    @Override
    public DataAdapterForMyPurchase.ViewHolderPurchase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("logmy", "OnCreateViewHolder");
        View view = inflater.inflate(R.layout.item_purchase, parent, false);
        return new DataAdapterForMyPurchase.ViewHolderPurchase(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataAdapterForMyPurchase.ViewHolderPurchase holder, int position) {
        Log.d("logmy", "onBindViewHolder");
        holder.nameOfPurchase.setText(PurchaseList.get(position).name);
        holder.descriptionOfPurchase.setText(PurchaseList.get(position).description);
        storageRef = storage.getReferenceFromUrl("gs://projectforpattern.appspot.com/" +
                "photoOfProduct").child("Product" + PurchaseList.get(position).photoID);
        final File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.imgPurchase.setImageBitmap(bitmap);
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
        if(PurchaseList == null) {
            Log.d("logmy", "getItemCount: насчитал 0");
            return 0;
        }
        Log.d("logmy", "getItemCount: насчитал несколько");
        return PurchaseList.size();
    }
}
