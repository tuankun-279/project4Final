package tuan.aprotrain.projectpetcare.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tuan.aprotrain.projectpetcare.Fragment.breeding.BreedingFragment;
import tuan.aprotrain.projectpetcare.Fragment.home.HomeFragment;
import tuan.aprotrain.projectpetcare.activities.Pet.PetActivity;
import tuan.aprotrain.projectpetcare.databinding.ActivityMainBinding;
import tuan.aprotrain.projectpetcare.entity.Image;
import tuan.aprotrain.projectpetcare.entity.Pet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import tuan.aprotrain.projectpetcare.Adapter.petListNavDerAdapter;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_GALLERY = 1;
    private static final int FRAGMENT_SHARE = 2;

    private int mCurrentFragment = FRAGMENT_HOME;

    String urlToken;


    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseStorage storage;
    DatabaseReference petUpdateReference;
    private StorageReference mStorage;
    private DatabaseReference mReference;

    private RecyclerView recyclerView;
    private petListNavDerAdapter adapter;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private BottomNavigationView mBottomNavigationView;

    ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList("Home", "Feedback", "Contact Us", "Share us",
            "Version Code", "Live Watch", "Add Items"));

    private ArrayList<Pet> pets;
    private ArrayList<Image> images;
    ActivityResultLauncher<String> launcher;

    private DrawerLayout mDrawerLayout;

    private ImageView img;
    public Uri imageUri;

    private ImageView imageProfileUserView;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {

                final StorageReference reference = storage.getReference().child("ImageProfile");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap User = new HashMap();
                                User.put("userUrl", urlToken);
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {

                                    petUpdateReference = FirebaseDatabase.getInstance().getReference("Users");
                                    petUpdateReference.child(user.getUid()).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Update Image complete", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
//        replaceFragment(new HomeFragment());

        mStorage = FirebaseStorage.getInstance().getReference();
        mReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerListPet);

        setSupportActionBar(binding.appBarMain.toolbar);

        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, ReminderActivity.class);
                startActivity(myIntent);
            }
        });

        //tuan
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (getIntent().hasExtra("Yes")) {
            Toast.makeText(this, "Hello Accept", Toast.LENGTH_SHORT).show();
        } else if (getIntent().hasExtra("No")) {
            Toast.makeText(this, "Hello No", Toast.LENGTH_SHORT).show();
        }

        // bottom drawer
        DrawerLayout drawer = binding.drawerLayout;

        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_breeding, R.id.nav_about_us, R.id.nav_booking_history)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // bottom navigation
        binding.appBarMain.contentMain.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
//                    replaceFragment(new HomeFragment());
                    getSupportActionBar().show();
                    NavOptions navOptionsHome = new NavOptions.Builder()
                            .setPopUpTo(R.id.action_home, true)
                            .build();
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_home, null, navOptionsHome);
                    break;
                case R.id.action_breeding:
                    getSupportActionBar().show();
//                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.action_breeding, new BreedingFragment()).commit();

                    NavOptions navOptionsBreeding = new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_breeding, true)
                            .build();
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_breeding, null, navOptionsBreeding);
                    break;
                case R.id.action_about_us:
//                    replaceFragment(new AboutUsFragment());
                    getSupportActionBar().show();
                    NavOptions navOptionsSlideShow = new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_about_us, true)
                            .build();
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_about_us, null, navOptionsSlideShow);
                    break;
            }
            return true;
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);

//        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_share)
//                .setOpenableLayout(mDrawerLayout)
//                .build();


        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new petListNavDerAdapter(pets, images, MainActivity.this);
        //Conecting Adapter Class with the recycler view
        recyclerView.setAdapter(adapter);

        GetPetNameDataFromDatabase();
        GetListImagePetFromDatabase();
        GetProfileUserFromDatabase();

        NavigationView navigationViewDisplay = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationViewDisplay.getHeaderView(0);

        imageProfileUserView = headerView.findViewById(R.id.imageProfileUserView);
        imageProfileUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
//                imgUser.setImageBitmap(bitmap);
            }
        });

        TextView tvUserNameProfileNavDrawer = headerView.findViewById(R.id.tv_user_name_profile_nav_drawer);
        tvUserNameProfileNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRequestDialog(Gravity.CENTER);
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.getData() != null && data != null) {
                imageUri = data.getData();
                imageProfileUserView.setImageURI(imageUri);
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    petUpdateReference = database.getReference();
//            Query query = petUpdateReference.orderByChild("userId").equalTo(user.getUid());

                    DatabaseReference userRef = petUpdateReference.child("Users").child(user.getUid()).child("userUrl");
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            String value = task.getResult().getValue(String.class);
                            if (value != null) {
                                StorageReference sReference = storage.getReferenceFromUrl(value);


                                sReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        uploadPicture();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });
                            } else {
                                uploadPicture();
                            }
                        }
                    });


                }
            }
        }

    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image....");
        pd.show();


        //upload new image
        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = mStorage.child("imagesProfile/" + randomKey);

        riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = String.valueOf(uri);
                        urlToken = url;
                        HashMap User = new HashMap();
                        User.put("userUrl", urlToken);
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
//                            petUpdateReference.child("Users").child(user.getUid()).child("userUrl").setValue(urlToken);
                            petUpdateReference = FirebaseDatabase.getInstance().getReference("Users");
                            petUpdateReference.child(user.getUid()).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to Uploaded.", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) progressPercent + "%");
            }
        });
    }

    private void openRequestDialog(int gravity) {

        mReference = FirebaseDatabase.getInstance().getReference("Image");

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_dialog_edit_username);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);

        dialog.setCanceledOnTouchOutside(true);

//        ImageView imgUser = dialog.findViewById(R.id.imgAvatar_user);

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnUpdate = dialog.findViewById(R.id.btn_update);



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etUserName = dialog.findViewById(R.id.et_user_name);
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {

                    if (etUserName.getText() != null) {
                        String userName = String.valueOf(etUserName.getText());

                        HashMap User = new HashMap();
                        User.put("userName", userName);

                        petUpdateReference = FirebaseDatabase.getInstance().getReference("Users");
                        petUpdateReference.child(user.getUid()).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Update User Name complete", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill User Name", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
    }

    private void GetProfileUserFromDatabase() {
        NavigationView navigationViewDisplay = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationViewDisplay.getHeaderView(0);
        ImageView imgProfile = headerView.findViewById(R.id.imageProfileUserView);
        TextView tvEmailProfileNavDrawer = headerView.findViewById(R.id.tv_email_profile_nav_drawer);
        TextView tvUserNameProfileNavDrawer = headerView.findViewById(R.id.tv_user_name_profile_nav_drawer);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        mReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getValue(User.class).getUserId().equals(currentUser.getUid())){
                            if (snapshot.child("userUrl").getValue() == null || snapshot.child("userUrl").getValue().equals("")) {
                                Glide.with(getApplicationContext()).load(R.mipmap.ic_launcher_round).apply(new RequestOptions()
                                        .override(170, 170)).into(imgProfile);
                            } else {
                                Glide.with(getApplicationContext()).load(snapshot.child("userUrl").getValue()).apply(new RequestOptions()
                                        .override(170, 170)).into(imgProfile);
                            }

                            String email = snapshot.child("email").getValue().toString();
                            tvEmailProfileNavDrawer.setText(email);
                            if (snapshot.child("userName").getValue() == null) {
                                tvUserNameProfileNavDrawer.setText("Update your username");
                            } else
                                tvUserNameProfileNavDrawer.setText(snapshot.child("userName").getValue().toString());
                            }
                        }

                    adapter = new petListNavDerAdapter(pets, images, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getCurrentUserData(){

    }


//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.content_frame, fragment);
//        fragmentTransaction.commit();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //navigation drawer hold long click pet

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 101:
                Snackbar.make(findViewById(R.id.drawer_layout),
                        "You clicked Edit Pet", Snackbar.LENGTH_LONG).show();


                long id = adapter.EditSelectedPet();

                Bundle bundle = new Bundle();
                bundle.putLong("idPet", id);
                NavOptions navOptionsEditPet = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_add_edit_pet, true)
                        .build();
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                        .navigate(R.id.nav_add_edit_pet, bundle, navOptionsEditPet);
                return true;
            case 102:
                Snackbar.make(findViewById(R.id.drawer_layout),
                        "You clicked Delete Pet", Snackbar.LENGTH_LONG).show();
                adapter.RemoveSelectedPet();
                return true;
            case 103:
                Snackbar.make(findViewById(R.id.drawer_layout),
                        "You clicked Image Pet", Snackbar.LENGTH_LONG).show();
                id = adapter.EditSelectedPet();

//                Bundle bundleImage = new Bundle();
//                bundleImage.putLong("idPet", id);

                Intent i = new Intent(MainActivity.this, ImagePetActivity.class);
                i.putExtra("ID_PET", id);
                startActivity(i);

                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void GetPetNameDataFromDatabase() {
        Query query = mReference.child("Pets");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    clearPetNameAll();
                    pets = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getValue(Pet.class).getUserId().equals(currentUser.getUid())) {
                            pets.add(snapshot.getValue(Pet.class));
                        }
                    }
                    adapter = new petListNavDerAdapter(pets, images, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
//                GetMatchingDataPetNameAndImage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void GetListImagePetFromDatabase() {
        Query query = mReference.child("Image");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearPetImageAll();
                images = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    images.add(snapshot.getValue(Image.class));
                }
                adapter = new petListNavDerAdapter(pets, images, getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
//                GetMatchingDataPetNameAndImage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearPetNameAll() {
        if (pets != null) {
            pets.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
        pets = new ArrayList<>();
    }

    private void clearPetImageAll() {
        if (images != null) {
            images.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
        images = new ArrayList<>();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Pet Options");
        menu.add(0, 101, 0, "Edit it");
        menu.add(1, 102, 1, "Remove it");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_add_edit_pet:
                NavOptions navOptionsAddEditPet = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_add_edit_pet, true)
                        .build();
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_add_edit_pet, null, navOptionsAddEditPet);
                return true;
            case R.id.nav_booking_history:
                NavOptions navOptionsShare = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_booking_history, true)
                        .build();
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_booking_history, null, navOptionsShare);
                return true;
            case R.id.nav_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
        }

        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            mReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (!snapshot.child(user.getUid()).getValue(User.class).getUserRole().equals("user")) {
                            startActivity(new Intent(MainActivity.this, AdminActivity.class));
                        } else {

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}