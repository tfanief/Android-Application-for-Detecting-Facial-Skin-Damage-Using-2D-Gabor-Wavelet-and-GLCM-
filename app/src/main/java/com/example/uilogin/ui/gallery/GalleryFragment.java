package com.example.uilogin.ui.gallery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.uilogin.R;
import com.example.uilogin.obj.ImgTesting;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseDatabase getDatabase;
    private DatabaseReference getRefenence;
    private String GetUserID;



    ListView lView;

    ListAdapter lAdapter;

    String hasil2;

    List<ImgTesting> imgTestings;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);

        auth = FirebaseAuth.getInstance();

        //Mendapatkan User ID dari akun yang terautentikasi
        FirebaseUser user = auth.getCurrentUser();
        GetUserID = user.getUid();

        getDatabase = FirebaseDatabase.getInstance();
        getRefenence = getDatabase.getReference();

        imgTestings = new ArrayList<>();

        getRefenence.child("Admins").child(GetUserID).child("HasilDiagnosis").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    imgTestings.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        ImgTesting imgTesting = dataSnapshot1.getValue(ImgTesting.class);
                        imgTesting.setKey(dataSnapshot1.getKey());
                        imgTestings.add(imgTesting);

                    }

                    String[] images ;

                    String[] hasil;
                    hasil = new String[imgTestings.size()];
                    images = new String[imgTestings.size()];
                    for(int i=0;i<imgTestings.size();i++){
                        ImgTesting data = imgTestings.get(i);
                        if(data.label==0){
                            hasil[i]="Pustula";
                        }
                        if(data.label==1){
                            hasil[i]="Papula";
                        }
                        if(data.label==2){
                            hasil[i]="Nodul";
                        }
                        if(data.label==3){
                            hasil[i]="Tidak ada jerawat inflamasi";
                        }
                        images[i]=data.name;

                    }

                    lView = (ListView) root.findViewById(R.id.androidList);

                    lAdapter = new ListAdapter(getContext(), hasil, images);

                    lView.setAdapter(lAdapter);

                    lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Toast.makeText(getActivity(), "Berhasil", Toast.LENGTH_SHORT).show();

                        }
                    });
                }else {
                    textView.setText("Belum Ada History");
                }

                //Mengambil value dari salah satu child, dan akan memicu data baru setiap kali diubah
               // Toast.makeText(getActivity(), "Data berhasil diambil "+imgTestings.get(1).label, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Digunakan untuk menangani kejadian Error
                Log.e("MyListData", "Error: ", databaseError.toException());
            }
        });

        return root;


    }
}