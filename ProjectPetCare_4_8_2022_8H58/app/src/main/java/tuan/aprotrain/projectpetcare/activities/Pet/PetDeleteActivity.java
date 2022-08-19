package tuan.aprotrain.projectpetcare.activities.Pet;

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
import tuan.aprotrain.projectpetcare.entity.Category;
import tuan.aprotrain.projectpetcare.entity.Pet;

public class PetDeleteActivity extends AppCompatActivity {
    Button btnDeletePet;
    Spinner spinnerPetName;
    DatabaseReference refPetDelete;
    Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pet_delete);

        spinnerPetName = findViewById(R.id.spnDeletePetName);
        ArrayAdapter<String> petNameAdapter = new ArrayAdapter(PetDeleteActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                getListPetName());
        petNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPetName.setAdapter(petNameAdapter);
        spinnerPetName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnDeletePet = findViewById(R.id.btnDeletePet);

        btnDeletePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String PetName = spinnerPetName.getSelectedItem().toString();
                DeletePet(PetName);
            }
        });
    }

    private void DeletePet(String petName) {
        refPetDelete = FirebaseDatabase.getInstance().getReference("Pets");
        refPetDelete.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(pet ->{
                    if(pet.getValue(Pet.class).getPetName().equals(petName)){
                        refPetDelete.child(String.valueOf(pet.getValue(Pet.class).getPetId())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(PetDeleteActivity.this, "Pet has been deleted", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(PetDeleteActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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

    private void DeleteUserPet(String userId) {
        refPetDelete = FirebaseDatabase.getInstance().getReference();
        refPetDelete.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.child("Pets").getChildren().forEach(pet ->{
                    if(pet.getValue(Pet.class).getUserId().equals(userId)){
                        refPetDelete.child(String.valueOf(pet.getValue(Pet.class).getPetId())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(PetDeleteActivity.this, "Pet has been deleted", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(PetDeleteActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                refPetDelete.child("Users").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(PetDeleteActivity.this, "Pet has been deleted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PetDeleteActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private List<String> getListPetName() {
        //reference.child("Categories");
        refPetDelete = FirebaseDatabase.getInstance().getReference("Pets");
        List<String> petNameList = new ArrayList<>();
        petNameList.add("Choose Your Pet");
        refPetDelete.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot cateSnapshot : snapshot.getChildren()) {
                    //Pet pet = petSnapshot.getValue(Pet.class);
                    petNameList.add(cateSnapshot.child("petName").getValue(String.class));
                    //System.out.println("Service:"+serviceSnapshot.child("serviceName").getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                int zzz= 111;
            }
        });
        return petNameList;
    }
}
