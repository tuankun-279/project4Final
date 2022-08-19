package tuan.aprotrain.projectpetcare.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

import tuan.aprotrain.projectpetcare.Adapter.BookingDialogAdapter;
import tuan.aprotrain.projectpetcare.Adapter.BookingManagerAdapter;
import tuan.aprotrain.projectpetcare.Adapter.ServiceManagerAdapter;
import tuan.aprotrain.projectpetcare.Adapter.UserManagerAdapter;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.activities.Service.ServiceActivity;
import tuan.aprotrain.projectpetcare.entity.Booking;
import tuan.aprotrain.projectpetcare.entity.CaptureAct;
import tuan.aprotrain.projectpetcare.entity.Pet;
import tuan.aprotrain.projectpetcare.entity.Service;
import tuan.aprotrain.projectpetcare.entity.User;

public class AdminActivity extends AppCompatActivity {
    List<User> usersList;
    List<Booking> bookingsList;
    List<Service> servicesList;
    private ListView listView;
    ImageView user, service, booking;
    DatabaseReference databaseReferenceUser, databaseReferenceService, databaseReferenceBooking;
    private boolean menuEnable = true;
    private boolean deleteCheck = true;
    String idButton;
    private ArrayAdapter adapterBooking;
    private ArrayAdapter adapterService;
    private ArrayAdapter adapterUser;

    FloatingActionButton fab, fab2;
    Animation fabOpen,fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ImageView logout = findViewById(R.id.logoutBtn);

        listView = findViewById(R.id.listView);
        usersList = new ArrayList<>();
        bookingsList = new ArrayList<>();
        servicesList = new ArrayList<>();
        user = findViewById(R.id.userImg);
        service = findViewById(R.id.serviceImg);
        booking = findViewById(R.id.bookingImg);

        registerForContextMenu(listView);

        adapterBooking = new BookingManagerAdapter(AdminActivity.this, bookingsList);
        adapterService = new ServiceManagerAdapter(AdminActivity.this, servicesList);
        adapterUser = new UserManagerAdapter(AdminActivity.this, usersList);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuEnable = false;
                databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Users");
                databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        usersList.clear();
                        for (DataSnapshot model : dataSnapshot.getChildren()) {
                            User user = model.getValue(User.class);
                            usersList.add(user);
                        }
//                        ListAdapter adapter = new UserManagerAdapter(AdminActivity.this,usersList);
                        listView.setAdapter(adapterUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuEnable = true;
                deleteCheck = true;
                databaseReferenceService = FirebaseDatabase.getInstance().getReference("Services");
                databaseReferenceService.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        servicesList.clear();
                        for (DataSnapshot model : dataSnapshot.getChildren()) {
                            Service service = model.getValue(Service.class);
                            servicesList.add(service);
                        }
//                        ListAdapter adapter = new ServiceManagerAdapter(AdminActivity.this,servicesList);
                        listView.setAdapter(adapterService);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuEnable = true;
                deleteCheck = false;
                databaseReferenceBooking = FirebaseDatabase.getInstance().getReference("Bookings");
                databaseReferenceBooking.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookingsList.clear();
                        for (DataSnapshot model : dataSnapshot.getChildren()) {
                            Booking booking = model.getValue(Booking.class);
                            bookingsList.add(booking);
                        }

                        listView.setAdapter(adapterBooking);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            }
        });

//        one=findViewById(R.id.clientsClick);
//        two=findViewById(R.id.LayoutFollowing);
//        three=findViewById(R.id.LayoutImpacted);
//
//        one.onTouchEvent(new )
        final ImageView scan_QrCode = (ImageView) findViewById(R.id.scan_QrCode);
        scan_QrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, ServiceActivity.class));
            }
        });
    }

    private void animateFab(){
        if (isOpen){
            fab.startAnimation(rotateForward);
            fab2.startAnimation(fabClose);
            fab2.setClickable(false);
            isOpen = false;
        }else {
            fab.startAnimation(rotateBackward);
            fab2.startAnimation(fabOpen);
            fab2.setClickable(true);
            isOpen = true;
        }
    }


    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
//            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//            builder.setTitle("Result");
//            builder.setMessage(result.getContents());
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            }).show();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Bookings").orderByChild("bookingId").equalTo(result.getContents())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot bookingSnapshot) {
                    if (bookingSnapshot.exists()){
                        for (DataSnapshot bookings:bookingSnapshot.getChildren()){
                            Booking booking = bookings.getValue(Booking.class);
                            reference.child("Pets").orderByChild("petId")
                                    .equalTo(bookings.getValue(Booking.class).getPetId())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot petSnapshot) {
                                    if (petSnapshot.exists()){
                                        for (DataSnapshot pets:petSnapshot.getChildren()){
                                            openDialog(Gravity.CENTER,booking,pets.getValue(Pet.class).getPetName());
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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    });


    public void openDialog(int gravity, Booking booking, String petName) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_dialog_booking);

        TextView petName_Details, startTimeTxt, endTimeTxt, totalPrice;
        petName_Details = dialog.findViewById(R.id.petName_Details);
        startTimeTxt = dialog.findViewById(R.id.startTimeTxt);
        endTimeTxt = dialog.findViewById(R.id.endTimeTxt);
        totalPrice = dialog.findViewById(R.id.totalPrice);
        ImageView qr_code = dialog.findViewById(R.id.qr_code);

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

        petName_Details.setText(petName);
        startTimeTxt.setText(booking.getBookingStartDate());
        endTimeTxt.setText(booking.getBookingEndDate());
        totalPrice.setText(booking.getTotalPrice() + "$");

        ListView listView = dialog.findViewById(R.id.listViewService);
        BookingDialogAdapter bookingDialogAdapter = new BookingDialogAdapter(dialog.getContext(),
                booking.getSelectedService());
        listView.setAdapter(bookingDialogAdapter);
        //qr code
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(booking.getBookingId(), BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        Button btnCancel = dialog.findViewById(R.id.btnCancelDialog);
        Button btnBooking = dialog.findViewById(R.id.btnBooking);

        //Toast.makeText(this, "Dialog info:" + getCheckedService().get(1), Toast.LENGTH_SHORT).show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Booking Successfully", Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(AdminActivity.this, LoginActivity.class ));
        } else {

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.admin_menu_option, menu);
        if (menuEnable == false) {
            menu.findItem(R.id.delete).setVisible(false);
        } else {
            menu.findItem(R.id.delete).setVisible(true);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
//                Toast.makeText(this, "Delete Selected", Toast.LENGTH_SHORT).show();
                //String name = ServiceManagerAdapter.getServiceName();

                new AlertDialog.Builder(this).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this entry?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (deleteCheck == true) {
                                    Service services = (Service) adapterService.getItem(info.position);
                                    databaseReferenceService.child(String.valueOf(services.getServiceId())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(AdminActivity.this, "Delete Service Succesfully", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(AdminActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Booking bookings = (Booking) adapterBooking.getItem(info.position);
                                    databaseReferenceBooking.child(String.valueOf(bookings.getBookingId())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(AdminActivity.this, "Delete Booking Succesfully", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(AdminActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .show();

                return false;

            default:
                return super.onContextItemSelected(item);
        }
    }

}