package tuan.aprotrain.projectpetcare.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tuan.aprotrain.projectpetcare.Adapter.BookingDialogAdapter;
import tuan.aprotrain.projectpetcare.Adapter.ExpandLVCheckBox;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Booking;
import tuan.aprotrain.projectpetcare.entity.BookingDetail;
import tuan.aprotrain.projectpetcare.entity.Category;
import tuan.aprotrain.projectpetcare.entity.Pet;
import tuan.aprotrain.projectpetcare.entity.Recycle;
import tuan.aprotrain.projectpetcare.entity.Service;

public class BookingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //TextView nameService;
    /*
      Phần khai báo cho adapter category
       */
    ExpandLVCheckBox listAdapter;
    ExpandableListView expListView;
    ArrayList<String> listCategory;
    HashMap<String, List<Service>> listService;
    /*
    Phần khai báo cho date and time
     */
    //private TextView date_time_input;
    private Activity activity;
    private SimpleDateFormat simpleDateFormat;
    private Calendar calendar;
    String currentDateTime;
    /*
    Phần khai báo cho adapter của spinner chọn pet name và payment
     */
    private Spinner spinnerPetName;
    private Spinner spinnerPayment;
    private Spinner spinnerAddress;

    // code cua tuan
    private EditText notePet;

    private DatabaseReference reference;
    String startDate;
    TextView dateStart, dateEnd;
    private Boolean chooseDateStart = true;
    TextView btnSubmit;
    Recycle recycle;
    String idButton;
    String category;
    private Boolean isUpdating = false;
//    ImageButton ib_back_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        Toolbar toolbarTop = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);

//        ib_back_btn = findViewById(R.id.ib_back_btn);
//        ib_back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(BookingActivity.this,MainActivity.class));
//            }
//        });

//        mTitle.setText(category);
        // tuan
        reference = FirebaseDatabase.getInstance().getReference();
        //selectedService = new ArrayList<>();
        notePet = findViewById(R.id.notePet);

        idButton = getIntent().getStringExtra("ID_BUTTON");
        //nameService = findViewById(R.id.textServiceInfo);

        spinnerPetName = findViewById(R.id.spnPetName);
        ArrayAdapter<String> petNameAdapter = new ArrayAdapter<>(BookingActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getListPetName());
        petNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPetName.setAdapter(petNameAdapter);
        spinnerPetName.setOnItemSelectedListener(this);

        spinnerPayment = findViewById(R.id.spnPayment);
        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this, R.array.payment, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setOnItemSelectedListener(this);
        spinnerPayment.setAdapter(paymentAdapter);

        spinnerAddress = findViewById(R.id.spnAddress);
        ArrayAdapter<CharSequence> addressAdapter = ArrayAdapter.createFromResource(this, R.array.adress, android.R.layout.simple_spinner_item);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddress.setOnItemSelectedListener(this);
        spinnerAddress.setAdapter(addressAdapter);
        recycle = new Recycle();
        //selectedService = new ArrayList<>();

        expListView = (ExpandableListView) findViewById(R.id.expandLV);
        prepareListData();
        listAdapter = new ExpandLVCheckBox(this, listCategory, listService);
        expListView.setAdapter(listAdapter);
        recycle = new Recycle();

        dateStart = findViewById(R.id.appointment);
        dateEnd = findViewById(R.id.endDateHotel);

        activity = this;
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        currentDateTime = simpleDateFormat.format(new Date());
        dateStart = (TextView) findViewById(R.id.appointment);
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingActivity.this.chooseDateStart = true;
                calendar = Calendar.getInstance();
                new DatePickerDialog(activity, mDateDataSet, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        if (idButton.equals("Homestay")) {
            dateEnd.setVisibility(View.VISIBLE);
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            dateEnd = (TextView) findViewById(R.id.endDateHotel);
            dateEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookingActivity.this.chooseDateStart = false;
                    calendar = Calendar.getInstance();
                    new DatePickerDialog(activity, mDateDataSet, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        }
    /*
        Hàm của expandable listview checkbox
    */
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listCategory.get(groupPosition)
                                + " : "
                                + listService.get(
                                listCategory.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        });


        btnSubmit = findViewById(R.id.btnSubmit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String petName = spinnerPetName.getSelectedItem().toString().trim();
                String timeStart = dateStart.getText().toString().trim();
                String timeEnd = dateEnd.getText().toString().trim();
                String payment = spinnerPayment.getSelectedItem().toString().trim();
                String address = spinnerAddress.getSelectedItem().toString().trim();
                String note = notePet.getText().toString();
                if (!timeStart.isEmpty() && !timeEnd.isEmpty() && getCheckedService().size() > 0) {
                    float totalPrice = 0;
                    for (Service service : getCheckedService()) {
                        totalPrice += service.getServicePrice();
                    }
                    getSelectedItem(petName, timeStart, timeEnd, payment, address, note, totalPrice);


                }
                if(petName.equals("Choose Pet")){
                    Toast.makeText(BookingActivity.this, "Choose a valid pet", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(BookingActivity.this, "All field are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /* Code cua kien (phan date & time) */
    private final View.OnClickListener textListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            calendar = Calendar.getInstance();
            new DatePickerDialog(activity, mDateDataSet, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(activity, mTimeDataSet, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }
    };

    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            //dang bam vao end
            if (idButton.equals("Homestay")) {
                if (BookingActivity.this.chooseDateStart) {
                    try {
                        Date dateC = simpleDateFormat.parse(recycle.CalculateDate(currentDateTime,180));
                        if(dateC.compareTo(calendar.getTime())>0){
                            Toast.makeText(BookingActivity.this, "Please set appointment 3 hour from now", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dateStart.setText(simpleDateFormat.format(calendar.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Date date = simpleDateFormat.parse(recycle.CalculateDate(dateStart.getText().toString().trim(),180));
                        if(date.compareTo(calendar.getTime())>0){
                            Toast.makeText(BookingActivity.this, "Minimum 3 hour from appointment", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dateEnd.setText(simpleDateFormat.format(calendar.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    Date dateS = simpleDateFormat.parse(recycle.CalculateDate(currentDateTime,180));
                    if(dateS.compareTo(calendar.getTime())>0){
                        Toast.makeText(BookingActivity.this, "Please set appointment 3 hour from now", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dateStart.setText(simpleDateFormat.format(calendar.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startDate = simpleDateFormat.format(calendar.getTime());
                long serviceTime = 0;
                for (Service service : getCheckedService()) {
                    serviceTime += service.getServiceTime();
                }

                dateEnd.setText(recycle.CalculateDate(startDate, serviceTime));
            }
        }
    };

    // tuan


    public void getSelectedItem(String petName, String startDate, String endDate,
                                String payment, String address, String note, float totalPrice) {

        reference.child("Pets").orderByChild("petName").equalTo(petName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long idPet = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot pets : snapshot.getChildren()) {
                        idPet = pets.getValue(Pet.class).getPetId();
                    }
                }
                Booking booking = new Booking(recycle.idHashcode(petName),
                        startDate, endDate, address,
                        note, totalPrice, payment, idPet, getCheckedService());
                openDialog(Gravity.BOTTOM, booking, petName);
                System.out.println("pet id: " + idPet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private List<String> getListPetName() {
        List<String> petNameList = new ArrayList<>();
        petNameList.add(0,"Choose Pet");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference.child("Pets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    if (petSnapshot.child("userId").getValue(String.class).equals(currentUser.getUid())) {
                        petNameList.add(petSnapshot.child("petName").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return petNameList;
    }

    private void prepareListData() {
        listCategory = new ArrayList<String>();
        listService = new HashMap<String, List<Service>>();
        //Intent pass data
        System.out.println(idButton);
        category = idButton + " Services";
        //nameService.setText(category);
        listCategory.add(category);

        List<Service> serviceList = new ArrayList<Service>();
        reference.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotCategory) {
                snapshotCategory.getChildren().forEach(categories -> {
                    Category category = categories.getValue(Category.class);
                    if (category.getCategoryName().equals(idButton)) {
                        long id = category.getCategoryId();
                        reference.child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotService) {
                                serviceList.clear();
                                snapshotService.getChildren().forEach(services -> {
                                    if (services.getValue(Service.class).getCategoryId() == id) {
                                        serviceList.add(services.getValue(Service.class));
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listService.put(listCategory.get(0), serviceList);
    }

    //
    public ArrayList<Service> getCheckedService() {
        ArrayList<Service> selectedService = new ArrayList<>();
        for (int mGroupPosition = 0; mGroupPosition < listAdapter.getGroupCount(); mGroupPosition++) {
            selectedService = listAdapter.getListCheckedChild(mGroupPosition, category);
        }
        return selectedService;
    }

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
                getCheckedService());
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
        Button btnSend = dialog.findViewById(R.id.btnBooking);

        //Toast.makeText(this, "Dialog info:" + getCheckedService().get(1), Toast.LENGTH_SHORT).show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("Bookings").child(booking.getBookingId()).setValue(booking);

                List<BookingDetail> bookingDetail = new ArrayList<>();
                for (Service service : getCheckedService()) {
                    bookingDetail.add(new BookingDetail(booking.getBookingId(), service.getServiceId()));
                }
                reference.child("BookingDetails").setValue(bookingDetail, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(BookingActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    }
                });
                //reference.child("Booking").child(booking.getBookingId()).child("Selected Services").setValue(booking.getSelectedService());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(getApplicationContext(), "Booking Successfully", Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
