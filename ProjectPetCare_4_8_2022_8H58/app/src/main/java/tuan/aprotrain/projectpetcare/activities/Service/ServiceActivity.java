package tuan.aprotrain.projectpetcare.activities.Service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Category;
import tuan.aprotrain.projectpetcare.entity.Service;

public class ServiceActivity extends AppCompatActivity  {
    EditText editTextServiceName;
    EditText editTextServicePrice;
    EditText editTextServiceTime;
    private Spinner spinnerCategoryName;
    long serviceId;
    Button btnSave;
    //    DatabaseReference reference;
    DatabaseReference refServices;
    DatabaseReference refCategory;
    private Boolean isUpdating = false;
    Service services;

    // Define ActionBar object
    Toolbar toolbar;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //reference = FirebaseDatabase.getInstance().getReference();
        refServices = FirebaseDatabase.getInstance().getReference().child("Services");
        refCategory = FirebaseDatabase.getInstance().getReference().child("Categories");
        setContentView(R.layout.activity_service);

        toolbar = findViewById(R.id.toolbar_uploading_pet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerCategoryName = findViewById(R.id.spnCategoryName);
        ArrayAdapter<String> categoryNameAdapter = new ArrayAdapter(ServiceActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                getListCategoryName());
        categoryNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryName.setAdapter(categoryNameAdapter);
        spinnerCategoryName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        editTextServiceName = findViewById(R.id.serviceName);
        editTextServicePrice = findViewById(R.id.servicePrice);
        editTextServiceTime = findViewById(R.id.serviceTime);
        btnSave = findViewById(R.id.btnServiceAdd);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUpdating = true;

                if (spinnerCategoryName.equals("Choose Category") || TextUtils.isEmpty(editTextServiceName.getText().toString()) ||
                        TextUtils.isEmpty(editTextServicePrice.getText().toString()) || TextUtils.isEmpty(editTextServiceTime.getText().toString())) {
                    Toast.makeText(ServiceActivity.this, "All field are required", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String CategoryName = spinnerCategoryName.getSelectedItem().toString();
                    String ServiceName = editTextServiceName.getText().toString().trim();
                    Float ServicePrice = Float.parseFloat(editTextServicePrice.getText().toString().trim());
                    Long ServiceTime = Long.parseLong(editTextServiceTime.getText().toString().trim());
                    ServiceAdd(CategoryName, ServiceName, ServicePrice, ServiceTime);
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void ServiceAdd(String CategoryName, String ServiceName, Float ServicePrice, Long ServiceTime){
        //reference.child("Services");
        services = new Service();
        long categoryId;
        refCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot categorySnapshot: snapshot.getChildren()){
                    Category category = categorySnapshot.getValue(Category.class);
                    if(category.getCategoryName().equals(CategoryName)){
                        services.setCategoryId(category.getCategoryId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!isUpdating) {
                    return;
                }
                serviceId = System.currentTimeMillis();
                services.setServiceId(serviceId);
                services.setServiceName(ServiceName);
                services.setServicePrice(ServicePrice);
                services.setServiceTime(ServiceTime);


                refServices.child(String.valueOf(serviceId)).setValue(services);
                isUpdating = false;
                Toast.makeText(ServiceActivity.this, "Data Inserted Succesfully", Toast.LENGTH_LONG).show();
                //finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isUpdating = false;
            }
        });

    }
    private List<String> getListCategoryName() {
        //reference.child("Categories");
        List<String> categoryNameList = new ArrayList<>();
        categoryNameList.add("Choose Category");
        refCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot cateSnapshot : snapshot.getChildren()) {
                    //Pet pet = petSnapshot.getValue(Pet.class);
                    categoryNameList.add(cateSnapshot.child("categoryName").getValue(String.class));
                    //System.out.println("Service:"+serviceSnapshot.child("serviceName").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return categoryNameList;
    }

}