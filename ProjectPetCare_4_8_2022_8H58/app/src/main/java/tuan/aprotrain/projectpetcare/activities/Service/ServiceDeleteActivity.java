package tuan.aprotrain.projectpetcare.activities.Service;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Service;
import tuan.aprotrain.projectpetcare.entity.User;

public class ServiceDeleteActivity extends AppCompatActivity {
    Spinner spinnerServiceDelete;
    Button btnServiceDelete;
    DatabaseReference refServiceDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_delete);
        btnServiceDelete = findViewById(R.id.btnServiceDelete);
        spinnerServiceDelete = findViewById(R.id.spnServiceDelete);
        ArrayAdapter<String> serviceNameAdapter = new ArrayAdapter<>(ServiceDeleteActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                getListServiceName());
        serviceNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceDelete.setAdapter(serviceNameAdapter);
        spinnerServiceDelete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnServiceDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ServiceName = spinnerServiceDelete.getSelectedItem().toString();
                ServiceDelete(ServiceName);
            }
        });
    }

    private void ServiceDelete(String serviceName) {
        refServiceDelete = FirebaseDatabase.getInstance().getReference("Services");
        refServiceDelete.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(service ->{
                    if(service.getValue(Service.class).getServiceName().equals(serviceName)){
                        refServiceDelete.child(String.valueOf(service.getValue(Service.class).getServiceName())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ServiceDeleteActivity.this, "User has been deleted", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ServiceDeleteActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private List<String> getListServiceName() {
        List<String> serviceNameList = new ArrayList<>();
        refServiceDelete = FirebaseDatabase.getInstance().getReference("Services");
        serviceNameList.add("Choose Services");
        refServiceDelete.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {
                    //Pet pet = petSnapshot.getValue(Pet.class);
                    serviceNameList.add(serviceSnapshot.child("serviceName").getValue(String.class));
                    //System.out.println("Service:"+serviceSnapshot.child("serviceName").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return serviceNameList;
    }
}