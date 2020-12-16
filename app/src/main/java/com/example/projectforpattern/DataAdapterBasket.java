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

public class DataAdapterBasket extends RecyclerView.Adapter<DataAdapterBasket.ViewHolderBasket> implements DataAdapter {

    List<Products> BasketList;
    LayoutInflater inflater;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;

    public class ViewHolderBasket extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameOfBasket, descriptionOfBasket;
        ImageView imgBasket, clearItemOfBasket;
        Button btnBuy;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolderBasket(@NonNull View itemView) {
            super(itemView);
            nameOfBasket = (TextView) itemView.findViewById(R.id.nameOfProductOfBasket);
            descriptionOfBasket = (TextView) itemView.findViewById(R.id.descriptionOfProductOfBasket);
            imgBasket = (ImageView) itemView.findViewById(R.id.imgProductOfBasket);
            clearItemOfBasket = (ImageView) itemView.findViewById(R.id.clearItemOfProductOfBasket);

            btnBuy = (Button) itemView.findViewById(R.id.btnBuy);

            btnBuy.setOnClickListener(this);
            clearItemOfBasket.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.clearItemOfProductOfBasket) {
                db.collection("Basket").document(BasketList.get(getAdapterPosition()).photoID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                removeAt(getAdapterPosition());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("logmy", "Error deleting document", e);
                            }
                        });

            } else if (view.getId() == R.id.btnBuy){
                db.collection("Basket").document(BasketList.get(getAdapterPosition()).photoID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String, Object> userdb = new HashMap<>();
                                userdb.put("name", BasketList.get(getAdapterPosition()).name);
                                userdb.put("description", BasketList.get(getAdapterPosition()).description);
                                userdb.put("photoID", BasketList.get(getAdapterPosition()).photoID);
                                Log.d("logmy", "Cей час будем добавлять документ в коллекцию");
                                db.collection("myPurchase").document("" + BasketList.get(getAdapterPosition()).photoID)
                                        .set(userdb);
                                removeAt(getAdapterPosition());
                                Toast.makeText(itemView.getContext(), "Спасибо за покупку!", Toast.LENGTH_SHORT).show();
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
        BasketList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, BasketList.size());
    }

    public DataAdapterBasket(Context context, ArrayList<Products> BasketList){
        this.BasketList = BasketList;
        this.inflater = LayoutInflater.from(context);
        Log.d("logmy", "конструктор адаптера");
    }

    @NonNull
    @Override
    public DataAdapterBasket.ViewHolderBasket onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("logmy", "OnCreateViewHolder");
        View view = inflater.inflate(R.layout.item_product_of_basket, parent, false);
        return new DataAdapterBasket.ViewHolderBasket(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataAdapterBasket.ViewHolderBasket holder, int position) {
        Log.d("logmy", "onBindViewHolder");
        holder.nameOfBasket.setText(BasketList.get(position).name);
        holder.descriptionOfBasket.setText(BasketList.get(position).description);
        storageRef = storage.getReferenceFromUrl("gs://projectforpattern.appspot.com/" +
                "photoOfProduct").child("Product" + BasketList.get(position).photoID);
        final File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.imgBasket.setImageBitmap(bitmap);
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
        if(BasketList == null) {
            Log.d("logmy", "getItemCount: насчитал 0");
            return 0;
        }
        Log.d("logmy", "getItemCount: насчитал несколько");
        return BasketList.size();
    }
}