package com.example.uilogin.ui.home;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.uilogin.R;
import com.example.uilogin.obj.GLCM;
import com.example.uilogin.obj.GaborFilter;
import com.example.uilogin.obj.ImgTesting;
import com.example.uilogin.obj.ImgTraining;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.utils.Converters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_8UC3;


public class HomeFragment extends Fragment {
    static {
        System.loadLibrary("opencv_java3");
        //System.loadLibrary("native-lib");
    }

    //Uri haha;

    private ImageView mImageview;
    private Button openImg;
    private Button loadData;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private FirebaseAuth auth;
    private String GetUserID;


    //Request Code Digunakan Untuk Menentukan Permintaan dari User
    public static final int REQUEST_CODE_CAMERA = 001;
    public TextView meanText;
    public TextView ketText;

    public double entropy;
    public double mean;
    public double homogenitas;
    public double contrast;
    public double energy;
    public Uri imageUri;
    List<ImgTraining> mDataTraining;
    Mat dataTes;
    Mat matGabor;
    String hasil;
    String ket;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mImageview = view.findViewById(R.id.showImg);
        openImg = view.findViewById(R.id.open_image);
     // loadData = view.findViewById(R.id.loadDataTraining);
        meanText = view.findViewById(R.id.text_mean);
        ketText = view.findViewById(R.id.text_ket);
        final TextView title = view.findViewById(R.id.text_gallery);
        final TextView email = view.findViewById(R.id.textView2);



        auth = FirebaseAuth.getInstance();

        //Mendapatkan User ID dari akun yang terautentikasi
        FirebaseUser user = auth.getCurrentUser();
        GetUserID = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("dataTrainig3");
        mDataTraining = new ArrayList<>();

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataTraining.clear();

                for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                    ImgTraining data = teacherSnapshot.getValue(ImgTraining.class);
                    data.setKey(teacherSnapshot.getKey());
                    mDataTraining.add(data);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


     //loadData.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { calcDataTraining(); } });
        openImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRequestImage();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();


        return view;
    }


    //Method Ini Digunakan Untuk Membuka Image dari Galeri atau Kamera
    private void setRequestImage() {

        Intent intentCamera = new Intent(CropImage.getPickImageChooserIntent(getActivity()));

        startActivityForResult(intentCamera, REQUEST_CODE_CAMERA);

    }

    //Method Ini Digunakan Untuk Menapatkan Hasil pada Activity, dari Proses Yang kita buat sebelumnya
    //Dan Mendapatkan Hasil File Photo dari Galeri atau Kamera


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            //haha = data.getData();
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this
                    );

        }

        //Menampilkan Gambar hasil Cropping Image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //Mengambil gambar hasil crop
                imageUri = result.getUri();

                //ekstraksi
                extractCiri(imageUri);
                //Matriks vektor data tes dengan cirinya
                dataTes = new Mat(1,5,CV_32F);
                dataTes.put(0,0,entropy);
                dataTes.put(0,1,mean);
                dataTes.put(0,2,energy);
                dataTes.put(0,3,homogenitas);
                dataTes.put(0,4,contrast);
                //KNN
                final double labelHasil = deteksiImage(dataTes);


                //hasil ekstraksi ke database
                mStorageRef = FirebaseStorage.getInstance().getReference("Admins").child(GetUserID).child("HasilDiagnosis");
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("Admins").child(GetUserID).child("HasilDiagnosis");
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(imageUri));
                mUploadTask = fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (taskSnapshot.getMetadata() != null) {
                                    if (taskSnapshot.getMetadata().getReference() != null) {
                                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String imageUrl = uri.toString();
                                                ImgTesting upload = new ImgTesting(entropy,mean,energy,homogenitas,contrast,imageUrl,labelHasil);
                                                String uploadId = mDatabaseRef.push().getKey();
                                                mDatabaseRef.child(uploadId).setValue(upload);
                                            }
                                        });
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        });



                //Tampilan hasil

                if(labelHasil==0){
                    hasil = "Pustule";
                    ket = "Merupakan jenis jerawat dengan tingkat peradangan ringan-sedang.\nJerawat ini dapat meninggalkan bekas, tergantung tingkat keparahan " +
                            "peradangannya dan lama jerawat muncul.\n\nTreatment tips : Menggunakan produk yang mengandung aha, bha, retinoids, benzoyl peroxide," +
                            "niacinamide, azelaic acid, sulfur.\n(sc:Claudia Christine, Ph.D/ig:@funskincare)";
                }
                if(labelHasil==1){
                    hasil = "Papula";
                    ket = "Merupakan jenis jerawat dengan tingkat peradangan ringan-sedang.\nJerawat ini dapat meninggalkan bekas, tergantung tingkat keparahan " +
                            "peradangannya dan lama jerawat muncul.\n\nTreatment tips : Menggunakan produk yang mengandung aha, bha, retinoids, benzoyl peroxide," +
                            "niacinamide, azelaic acid, sulfur.\n(sc:Claudia Christine, Ph.D/ig:@funskincare)";
                }
                if(labelHasil==2){
                    hasil = "Nodul";
                    ket = "Merupakan jenis jerawat dengan tingkat peradangan berat. Jerawat ini sangat beresiko meninggalkan bekas.\n\nTreatment tips :Segera periksa dengan dokter spesialis kulit terdekat anda.\n(sc:Claudia Christine, Ph.D/ig:@funskincare)";
                }
                if(labelHasil==3){
                    hasil = "Tidak ada jerawat inflamasi";
                    ket="";
                }



                    //Menampilkan Gambar pada ImageView
                //Picasso.get().load(imageUri).into(mImageview);
                matGabor.convertTo(matGabor, CV_8UC3);
                Bitmap bitmap = Bitmap.createBitmap(matGabor.cols(), matGabor.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(matGabor, bitmap);
                mImageview.setImageBitmap(bitmap);
                //String ciri = "entropi :" + entropy + " mean :" + mean + " homogenitas :" + homogenitas + " contrast :" + contrast + " energi :" + energy;
                //String t =""+glcmMat.type();
                meanText.setText(hasil);
                ketText.setText(ket);


            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Menangani Jika terjadi kesalahan
                String error = result.getError().toString();
                Log.d("Exception", error);
                Toast.makeText(getActivity(), "Crop Image Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
       // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(getActivity()) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, getActivity(),
                baseLoaderCallback);
    }


    private File[] loadDataTraining() {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] data = root.listFiles();
        if (data != null) {

            Toast.makeText(getActivity(), "Ada ", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(getActivity(), "gagal", Toast.LENGTH_SHORT).show();
        }
        return data;
    }

    private void calcDataTraining()  {
        File[] data = loadDataTraining();
        int l =0;
       for (int i = 0; i < data.length; i++) {
           Uri imageUri2 = Uri.fromFile(data[i]);

           extractCiri(imageUri2);


           ImgTraining imgt = new ImgTraining(imageUri2.getPath(), entropy, mean, energy, homogenitas, contrast, 3);
           uploadDataTraining(imgt);

       }
    }

    private void uploadDataTraining(ImgTraining imgt) {
        String uploadId = mDatabase.push().getKey();
        mDatabase.child("dataTrainig4").child(uploadId).setValue(imgt);
    }
    //private String getFileExtension(Uri uri) {
    //  ContentResolver cR = getC ;
    //MimeTypeMap mime = MimeTypeMap.getSingleton();
    //return mime.getExtensionFromMimeType(cR.getType(uri));
    //}

    public double deteksiImage(Mat tes){
        List<Integer> trainLabs = new ArrayList<Integer>();
        Mat fiturData = new Mat(mDataTraining.size(),5,CV_32F);
        for(int i=0; i<mDataTraining.size();i++){
            ImgTraining data = mDataTraining.get(i);
            for(int j=0; j<5; j++){
                if(j==0) { fiturData.put(i,j,data.entropi);}
                if(j==1){ fiturData.put(i,j,data.mean); }
                if(j==2){ fiturData.put(i,j,data.energi); }
                if(j==3){ fiturData.put(i,j,data.homogenitas); }
                if(j==4){ fiturData.put(i,j,data.contrast); }
            }
            trainLabs.add(data.label);
        }
        KNearest knn = KNearest.create();
        knn.train(fiturData, Ml.ROW_SAMPLE, Converters.vector_int_to_Mat(trainLabs));
        Mat res = new Mat();
        float p = knn.findNearest(tes,5,res);
        return res.get(0,0)[0];
    }

    public void extractCiri(Uri img)  {
        //ekstrasi Gabor
        GaborFilter gf = new GaborFilter();
        gf.extract(img, 20, 0.79, 40, 0.5, 0);
        //entropy
        entropy = gf.getEntropy();
        mean = gf.getMean();
        matGabor = gf.getMat();

        //Ekstraksi GLCM
        GLCM glcm = new GLCM(img, 3);
        glcm.extract();
        homogenitas = glcm.getHomogenity();
        contrast = glcm.getContrast();
        energy = glcm.getEnergy();
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}









