package com.demo.iotandroid.helpers;

import com.demo.iotandroid.models.Video;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseVideoHelper {

    private StorageReference mStorageRef;
    private StorageReference mVideosRef;
    private DatabaseReference mDataReference;
    private ProgressDialog progressDialog;
    private Context mContext;
    private FirebaseAuth mAuth;
    private Activity mActivity;

    public FirebaseVideoHelper(Context context, Activity activity) {
        mActivity = activity;
        mAuth = FirebaseAuth.getInstance();
        signIn();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDataReference = FirebaseDatabase.getInstance().getReference("videos");
        mContext = context;
        progressDialog = new ProgressDialog(mContext);
    }

    private void signIn() {
        //do not change
        mAuth.signInWithEmailAndPassword("test@test.test", "123test")
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("LOG", "signInWithEmail:success");
                        } else {
                            Log.w("LOG", "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    public void getAllVideos(final OnNewVideoListener listener) {
        mDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> objectMap = (HashMap<String, Object>)
                        dataSnapshot.getValue();
                List<Video> videos = new ArrayList<>();
                if (objectMap != null) {
                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> mapObj = (Map<String, Object>) obj;
                            Video video = new Video();
                            video.setUrl((String) mapObj.get("url"));
                            video.setName((String) mapObj.get("name"));
                            video.setDate((String) mapObj.get("date"));
                            videos.add(video);
                        }
                    }
                    listener.callback(videos);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @SuppressWarnings("VisibleForTests")
    public void sendFile(Uri file) {
        mVideosRef = mStorageRef.child(file.toString());
        mVideosRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String name = taskSnapshot.getMetadata().getName();
                        String url = taskSnapshot.getDownloadUrl().toString();
                        writeNewImageInfoToDB(name, url);
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setTitle("Uploading...");
                        progressDialog.show();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }

    private void writeNewImageInfoToDB(String name, String url) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date today = Calendar.getInstance().getTime();

        String reportDate = df.format(today);

        Video info = new Video(name, url, reportDate);
        String key = mDataReference.push().getKey();
        mDataReference.child(key).setValue(info);
    }
}
