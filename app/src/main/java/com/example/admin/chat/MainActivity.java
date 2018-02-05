package com.example.admin.chat;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static com.example.admin.chat.NotificService.notifi;

public class MainActivity extends AppCompatActivity {

    public static int type = 1;
    public static int i;
    public static String user_id;
    public static ListView msglist;

    private FloatingActionButton fab;
    private EditText textmsg;
    private ImageView attach;

    private int SIGN_IN_Request_code = 1;
    private String txt, message, tag, checkusername;

    private ArrayList<Model> chatdetails = new ArrayList<>();
    private MyAdapter adapter;
    private Model model;

    private DatabaseReference firebaseDatabase;
    private NotificationManager notificationManager;

    private ProgressDialog progressDialog;
    private Dialog dialog;

    private PendingIntent pendingIntent;
    private Intent in;
    private Uri galleryFileURL;
    private Map<String, String> map;
    private Uri camerafileURL;
    private FirebaseUser username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab);
        attach = findViewById(R.id.attachment);
        textmsg = findViewById(R.id.edit_text);
        msglist = findViewById(R.id.list_chat);
        adapter = new MyAdapter(MainActivity.this, R.layout.activity_main, chatdetails);
        username = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("message");
        NetworkCheck(this);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_Request_code);
            user_id = username.getUid();
            checkusername = username.getDisplayName();
        } else {
            user_id = username.getUid();
            checkusername = username.getDisplayName();
            chathistory();
        }
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Image");
        progressDialog.setIndeterminate(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//UI base task is not work in thread so we used main thread for running this task
                txt = textmsg.getText().toString();
                try {
                    if (txt.equalsIgnoreCase("")) {
                        Toast.makeText(MainActivity.this, "Please type any message", Toast.LENGTH_LONG).show();
                    } else {
                        model = new Model(null, txt, username.getDisplayName(), user_id, null);
                        type = 1;
                        textmsg.setText("");                    //clear edittext box
                        firebaseDatabase.push().setValue(model);
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.huawei);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//   images
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]
                        {Manifest.permission.CAMERA}, 0);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]
                        {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

                dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
                dialog.setContentView(R.layout.imagesdialog);
                dialog.show();
                ImageView cameradialog = dialog.findViewById(R.id.cameradialog);
                cameradialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        camerafileURL = Uri.fromFile(getOutputMediaFile());
                        Log.e("CameraImagePath", camerafileURL.toString());
                        camera.putExtra(MediaStore.EXTRA_OUTPUT, camerafileURL);
                        startActivityForResult(camera, 0);
                        dialog.cancel();
                        type = 2;

                    }
                });

                ImageView gallerydialog = dialog.findViewById(R.id.gallerydialog);
                gallerydialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, SIGN_IN_Request_code);
                        dialog.cancel();
                        progressDialog.show();
                    }
                });
            }
        });
    }

    // Create folder fro store capture image
    private static File getOutputMediaFile() {
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "GroupChat");
        if (!f.exists()) {
            f.mkdirs();
        }
        String fname = "/IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        Log.e("CameraFileURl", f + fname);
        return new File(f.getPath() + fname);
    }

    private void chathistory() {
        firebaseDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                model = new Model(null, txt, username.getDisplayName(), user_id, null);

                try {
//default database
                    map = (Map) dataSnapshot.getValue();
                    message = map.values().toString();
                    model.setUsername(map.get("username"));
                    model.setTextmessage(map.get("textmessage"));
                    Long time = (Long) dataSnapshot.child("time").getValue();
                    model.setTime(time);
                    if (checkusername.equalsIgnoreCase(map.get("username"))) {
                        model.setIsme("me");
                    } else {
                        model.setIsme("you");
                    }
                    tag = model.getUsername();
                    Log.e(tag, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msglist.smoothScrollToPosition(chatdetails.size()-1);
                msglist.setAdapter(adapter);
                chatdetails.add(model);
                adapter.notifyDataSetChanged();
                startService(in);
                notification(notifi);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean NetworkCheck(Context context) {
//        check internet Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connect = false;
        if ((networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) || (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
            connect = true;
            in = new Intent(MainActivity.this, NotificService.class);
        } else {
            connect = false;
            Toast.makeText(this, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }
        return connect;
    }

    public void notification(Boolean notific) {
        if (notific == true)
            if (!model.getUsername().equalsIgnoreCase(username.getDisplayName())) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                int notification_id = 1;
                intent.putExtra("Notification", notification_id);
                pendingIntent = PendingIntent.getActivity(this, notification_id, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("GroupChat")
                        .setContentText(model.getTextmessage())
                        .setVibrate(new long[]{500, 500, 500, 500})
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setSubText(model.getUsername())
                        .setAutoCancel(true)
                        .setSound(sound);
                notificationManager.notify(notification_id, builder.build());
                stopService(in);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// camera case
            case 0:
                if (resultCode == RESULT_OK) {
                    String imagePath = "Images/" + username.getDisplayName() + "/" + new Date().getTime() + ".jpg";
                    Log.d("imagePath", imagePath);
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child(imagePath);
                    progressDialog.show();
                    mStorageRef.putFile(camerafileURL)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    type = 2;
                                    // Get a URL to the uploaded content
                                    Uri cameraURL = taskSnapshot.getDownloadUrl();
                                    Log.e("CameraImageURL", cameraURL.toString());
                                    model = new Model(null, cameraURL.toString(), username.getDisplayName(), user_id, null);
                                    firebaseDatabase.push().setValue(model);
                                    Toast.makeText(MainActivity.this, "Your image sent Successfully", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(MainActivity.this, exception.toString(), Toast.LENGTH_LONG).show();
                                    Log.e("error", exception.toString());
                                }
                            });
                }
                break;
// gallery case
            case 1:
                if (resultCode == RESULT_OK) {
                    galleryFileURL = data.getData();
                    try {
                        Log.e("FromGalleryFile", galleryFileURL.toString());
                        // bitmap = BitmapFactory.decodeFile(galleryURL.toString());

                        StorageReference mStorageRef = FirebaseStorage.getInstance()
                                .getReference().child("Images/" + username.getDisplayName() + "/" + new Date().getTime() + ".jpg");
                        mStorageRef.putFile(galleryFileURL)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        Uri galleryUrl = taskSnapshot.getDownloadUrl();
                                        Log.e("GalleryImageURL", galleryUrl.toString());
                                        model = new Model(null, galleryUrl.toString(), username.getDisplayName(), user_id, null);
                                        firebaseDatabase.push().setValue(model);

                                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.huawei);
                                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                            @Override
                                            public void onPrepared(MediaPlayer mp) {
                                                mp.start();
                                                Toast.makeText(MainActivity.this, "Your image sent Successfully", Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(MainActivity.this, exception.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                        ;
                    }
                }
                break;

            default:
                finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(in);
    }
}
