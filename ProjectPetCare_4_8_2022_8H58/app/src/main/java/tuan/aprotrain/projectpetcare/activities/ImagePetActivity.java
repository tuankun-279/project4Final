package tuan.aprotrain.projectpetcare.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import tuan.aprotrain.projectpetcare.Adapter.DisplayPetImageAdapter;
import tuan.aprotrain.projectpetcare.Adapter.PetImageAdapter;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Image;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tuan.aprotrain.projectpetcare.entity.Image;

public class ImagePetActivity extends AppCompatActivity {
    private static final int IMAGE_CODE = 1;

    boolean isRemembered = false;
    String urlToken;

    Image image;

    private List<String> fileNameList;
    private List<Uri> fileUriList;
    private List<String> fileDoneList;
    private List<String> nameImageDbList = new ArrayList<>();
    ArrayList<String> listKey = new ArrayList<>();
    private ArrayList<Image> images;
    private ArrayList<String> fileNameWithoutFormatList = new ArrayList<>();

    private PetImageAdapter petImageAdapter;
    private DisplayPetImageAdapter displayPetImageAdapter;

    RecyclerView recyclerViewDisplayWaitingImageId;
    RecyclerView recyclerViewRetrieveImageId;
    RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;

    private StorageReference mStorage;
    private DatabaseReference mReference;

    private FirebaseDatabase database;

    // Define ActionBar object
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pet);

        toolbar = findViewById(R.id.toolbar_uploading_pet);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorage = FirebaseStorage.getInstance().getReference();
        mReference = FirebaseDatabase.getInstance().getReference();

        fileNameList = new ArrayList<>();
        fileUriList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        images = new ArrayList<>();

        petImageAdapter = new PetImageAdapter(fileNameList, fileUriList, fileDoneList);

        recyclerViewRetrieveImageId = findViewById(R.id.recyclerViewRetrieveImageId);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerViewRetrieveImageId.setLayoutManager(gridLayoutManager);
        recyclerViewRetrieveImageId.setLayoutManager(layoutManager);
        recyclerViewRetrieveImageId.setHasFixedSize(true);
        //clear Arraylist
        clearAll();


        //get Data method
        GetDataFromDatabase();


        //get List image name from realtime db
        getListNameFromRD();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean userRegistered = true;
        MenuItem deleteItem = menu.findItem(R.id.menu_delete);
        MenuItem selectAllItem = menu.findItem(R.id.menu_select_all);
        deleteItem.setVisible(false);
        selectAllItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    private void GetDataFromDatabase() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
//            extras.getString("idPet");
            Long idPet = getIntent().getLongExtra("ID_PET", 0);

            Query query = mReference.child("Image").orderByChild("petId").equalTo(idPet);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    clearAll();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("imageId").getValue() != null) {
                            Image image = new Image();
                            image.setImageId(Long.parseLong(snapshot.child("imageId").getValue().toString()));
                            image.setUrl(snapshot.child("url").getValue().toString());
                            image.setFileName(snapshot.child("fileName").getValue().toString());
                            images.add(image);
                        }
                    }
                    displayPetImageAdapter = new DisplayPetImageAdapter(getApplicationContext(), images);
                    recyclerViewRetrieveImageId.setAdapter(displayPetImageAdapter);
                    displayPetImageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void clearAll() {
        if (images != null) {
            images.clear();
            if (displayPetImageAdapter != null) {
                displayPetImageAdapter.notifyDataSetChanged();
            }
        }
        images = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_pet_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = 0;
        switch (item.getItemId()) {
            case R.id.itemUpload:
                for (String itemFileUploadFinish : fileDoneList) {
                    if (itemFileUploadFinish.equals("doneImage")) {
                        i++;
                    }
                }
                if (fileNameList.size() == i) {
                    removeListImageStored();
                }
                if (fileNameList.size() > 0) {
                    openRequestDialog(this);
                } else {
                    chooseImages();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chooseImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_CODE);
    }

    private void removeListImageStored() {
        fileNameList.clear();
        fileUriList.clear();
        fileDoneList.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int totalItemSelected = data.getClipData().getItemCount();
                for (int i = 0; i < totalItemSelected; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    String fileName = getFileName(fileUri);

                    if (fileNameList.contains(fileName)) {
                        Toast.makeText(this, "Duplicate image name is not permitted", Toast.LENGTH_LONG).show();
                    } else {
                        fileNameList.add(fileName);
                        fileUriList.add(fileUri);
                        fileDoneList.add("progress");
                        petImageAdapter.notifyDataSetChanged();
                    }
                }
            } else if (data.getData() != null) {
                Uri fileUri = data.getData();
                String fileName = getFileName(fileUri);
                if (fileNameList.contains(fileName)) {
                    Toast.makeText(this, "Duplicate image name is not permitted", Toast.LENGTH_LONG).show();
                } else {
                    fileNameList.add(fileName);
                    fileUriList.add(fileUri);
                    fileDoneList.add("progress");
                    petImageAdapter.notifyDataSetChanged();
                }
            }
            openRequestDialog(this);
        }

    }

    private void openRequestDialog(Activity activity) {

        mReference = FirebaseDatabase.getInstance().getReference("Image");

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_dialog_display_images);

        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);

        recyclerViewDisplayWaitingImageId = dialog.findViewById(R.id.recyclerViewDisplayWaitingImageId);

        recyclerViewDisplayWaitingImageId.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDisplayWaitingImageId.setHasFixedSize(true);
        recyclerViewDisplayWaitingImageId.setAdapter(petImageAdapter);
//        recyclerViewDisplayWaitingImageId.setNestedScrollingEnabled(false);

        Button btnHide = dialog.findViewById(R.id.btn_hide);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnUpload = dialog.findViewById(R.id.btn_upload);
        Button btnChooseMoreImage = dialog.findViewById(R.id.btn_choose_more_image);
        Button btnRemoveUpload = (Button) dialog.findViewById(R.id.btn_remove_upload); //loi la null
        ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);

        progressBar.setProgress(0);


        //get key realtime db
        List<String> keyList = new ArrayList<>();
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String key = null;
                Map<String, Object> td = (HashMap<String, Object>) snapshot.getValue();
                for (Map.Entry<String, Object> entry : td.entrySet()) {
                    if (entry != null) {
                        if (entry.getKey().equals("Image")) {
                            keyList.add(entry.getKey());
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                removeListImageStored();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            StorageReference fileToUpload;

            @Override
            public void onClick(View view) {
                getListNameRaw();
                if (fileNameList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please upload more images", Toast.LENGTH_LONG).show();
                } else {
                    mStorage = FirebaseStorage.getInstance().getReference("Images");


//                    int progressMo = 0;
                    int totalProgress = 100;
                    int progressUploading = totalProgress / fileNameList.size();

                    int check = 0;
                    for (String nameImageItem : nameImageDbList) {
                        for (String fileNameWithoutFormatItem : fileNameWithoutFormatList) {
                            if (nameImageItem.equals(fileNameWithoutFormatItem)) {
                                check++;
                            }
                        }
                    }

                    if (keyList.size() > 0) {
                        mReference = FirebaseDatabase.getInstance().getReference("Image");
                        if (check > 0) {
                            Toast.makeText(getApplicationContext(), "Duplicate Image file name", Toast.LENGTH_LONG).show();
                        } else {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                btnUpload.setVisibility(View.GONE);
                                btnChooseMoreImage.setVisibility(View.GONE);
                                btnCancel.setVisibility(View.GONE);
                                recyclerViewDisplayWaitingImageId.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);
                                btnHide.setVisibility(View.GONE);


                                final StorageReference ImageName = mStorage.child(fileNameList.get(i));
                                int finalI = i;
                                ImageName.putFile(fileUriList.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String url = String.valueOf(uri);
                                                urlToken = url;


                                                mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Bundle extras = getIntent().getExtras();
                                                        if (extras != null) {
                                                            Long idPet = getIntent().getLongExtra("ID_PET",0);
                                                            if (snapshot.exists()) {
                                                                int getLatestKey = 0;
                                                                for (DataSnapshot keyDataSnapshot : snapshot.getChildren()) {
                                                                    listKey.add(keyDataSnapshot.getKey());
                                                                }
                                                                if (listKey.size() == 0) {
                                                                    getLatestKey = 0;
                                                                } else {
                                                                    getLatestKey = Integer.parseInt(listKey.get(listKey.size() - 1));
                                                                }
                                                                String[] cutText = fileNameList.get(finalI).split("\\.");
                                                                image = new Image();
                                                                image.setImageId(getLatestKey + 1);
                                                                image.setFileName(cutText[0]);
                                                                image.setUrl(urlToken);
                                                                image.setPetId(idPet);
                                                                mReference.child(String.valueOf(getLatestKey + 1)).setValue(image);
//                                                            fileNameList.remove(finalI);
                                                                fileDoneList.remove(finalI);
                                                                fileDoneList.add(finalI, "doneImage");
                                                                petImageAdapter.notifyDataSetChanged();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                                progressBar.incrementProgressBy(progressUploading);
                                                if (progressBar.getProgress() >= 99) {
                                                    dialog.dismiss();
//                                                    removeListImageStored();
//                                                    recyclerViewDisplayWaitingImageId.getRecycledViewPool().clear();
//                                                    petImageAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                });


                            }
                        }
                    } else {
                        mReference.child("Image").setValue("");

                        for (int i = 0; i < fileNameList.size(); i++) {
                            btnUpload.setVisibility(View.GONE);
                            btnChooseMoreImage.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.GONE);
                            recyclerViewDisplayWaitingImageId.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            btnHide.setVisibility(View.GONE);

                            final StorageReference ImageName = mStorage.child(fileNameList.get(i));
                            int finalI = i;


                            ImageName.putFile(fileUriList.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                                        uploadListAdapter.notifyDataSetChanged();
                                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String url = String.valueOf(uri);
                                            urlToken = url;


                                            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Bundle extras = getIntent().getExtras();
                                                    if (extras != null) {
                                                        Long idPet = getIntent().getLongExtra("ID_PET",0);
                                                        if (snapshot.exists()) {
                                                            int getLatestKey = 0;
                                                            for (DataSnapshot keyDataSnapshot : snapshot.getChildren()) {
                                                                listKey.add(keyDataSnapshot.getKey());
                                                            }
                                                            if (listKey.size() == 0) {
                                                                getLatestKey = 0;
                                                            } else {
                                                                getLatestKey = listKey.size() - 1;
                                                            }
                                                            String[] cutText = fileNameList.get(finalI).split("\\.");
                                                            image = new Image();
                                                            image.setImageId(getLatestKey + 1);
                                                            image.setFileName(cutText[0]);
                                                            image.setUrl(urlToken);
                                                            image.setPetId(idPet);
                                                            mReference.child("Image").child(String.valueOf(getLatestKey + 1)).setValue(image);
//                                                        fileNameList.remove(finalI);
                                                            fileDoneList.remove(finalI);
                                                            fileDoneList.add(finalI, "doneImage");
                                                            petImageAdapter.notifyDataSetChanged();
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            progressBar.incrementProgressBy(progressUploading);
                                            if (progressBar.getProgress() >= 99) {
                                                dialog.dismiss();
//                                                    removeListImageStored();
//                                                recyclerViewDisplayWaitingImageId.getRecycledViewPool().clear();
//                                                petImageAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            });

                        }
                    }

                }

            }
        });

        btnChooseMoreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImages();
            }
        });

        dialog.show();
    }

    private void getListNameRaw() {
        fileNameWithoutFormatList.clear();
        for (int i = 0; i < fileNameList.size(); i++) {
            String[] cutText = fileNameList.get(i).split("\\.");
            fileNameWithoutFormatList.add(cutText[0]);
        }
    }

    private void getListNameFromRD() {
        nameImageDbList.clear();

        Query query = mReference.child("Image");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    nameImageDbList.add(snapshot.child("fileName").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }
}