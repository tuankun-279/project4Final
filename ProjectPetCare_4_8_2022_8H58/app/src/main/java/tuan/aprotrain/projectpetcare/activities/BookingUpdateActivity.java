package tuan.aprotrain.projectpetcare.activities;



import static androidx.appcompat.R.layout.support_simple_spinner_dropdown_item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.databinding.ActivityBookingUpdateBinding;

public class BookingUpdateActivity extends AppCompatActivity  {
    EditText editTextBookingStartDate,
            editTextBookingEndDate,
            editTextBookingAddress,
            editTextBookingPayment,
            editTextNote;
    Button btnBookingUpdate;
    Spinner spinnerBookingUpdate;
    DatabaseReference referenceBooking;
    ActivityBookingUpdateBinding bookingUpdateBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookingUpdateBinding = ActivityBookingUpdateBinding.inflate(getLayoutInflater());
        setContentView(bookingUpdateBinding.getRoot());
        spinnerBookingUpdate = findViewById(R.id.spnBookingUpdate);
        ArrayAdapter<String> bookingUpdateAdapter = new ArrayAdapter(
                BookingUpdateActivity.this,
                support_simple_spinner_dropdown_item,
                getListBooking());
        bookingUpdateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBookingUpdate.setAdapter(bookingUpdateAdapter);
        spinnerBookingUpdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        editTextBookingStartDate = findViewById(R.id.bookingStartDate);
//        editTextBookingEndDate = findViewById(R.id.bookingEndDate);
//        editTextBookingAddress = findViewById(R.id.bookingAddressUpdate);
//        editTextBookingPayment = findViewById(R.id.bookingPayment);
//        editTextNote = findViewById(R.id.noteUpdate);

        bookingUpdateBinding.btnBookingUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookingStartDate = bookingUpdateBinding.bookingStartDate.getText().toString();
                String bookingEndDate = bookingUpdateBinding.bookingEndDate.getText().toString();
                String bookingAddress = bookingUpdateBinding.bookingAddressUpdate.getText().toString();
                String bookingPayment = bookingUpdateBinding.bookingPayment.getText().toString();
                String bookingNote = bookingUpdateBinding.noteUpdate.getText().toString();
                String bookingId =  bookingUpdateBinding.spnBookingUpdate.getSelectedItem().toString();
                updateBooking(bookingStartDate, bookingEndDate, bookingAddress, bookingPayment, bookingNote,bookingId);
            }
        });


    }

    private void updateBooking(String bookingStartDate, String bookingEndDate, String bookingAddress, String bookingPayment, String bookingNote, String bookingId) {
        HashMap booking = new HashMap();
        booking.put("bookingStartDate", bookingStartDate);
        booking.put("bookingEndDate", bookingEndDate);
        booking.put("bookingAddress", bookingAddress);
        booking.put("payment", bookingPayment);
        booking.put("notes", bookingNote);

        referenceBooking = FirebaseDatabase.getInstance().getReference("Bookings");
        referenceBooking.child(bookingId).updateChildren(booking).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    bookingUpdateBinding.bookingStartDate.setText("");
                    bookingUpdateBinding.bookingEndDate.setText("");
                    bookingUpdateBinding.bookingAddressUpdate.setText("");
                    bookingUpdateBinding.bookingPayment.setText("");
                    bookingUpdateBinding.noteUpdate.setText("");
                    Toast.makeText(BookingUpdateActivity.this, "Update data complete", Toast.LENGTH_SHORT).show();
                }
            }
        })
        ;}

    public List<String> getListBooking(){
        referenceBooking = FirebaseDatabase.getInstance().getReference("Bookings");
        List<String> bookingList = new ArrayList<>();
        bookingList.add("Choose booking");
        referenceBooking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot bookingSnapShot : snapshot.getChildren()){
                    bookingList.add(bookingSnapShot.child("bookingAddress").getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return bookingList;
    }
}


