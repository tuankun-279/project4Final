package tuan.aprotrain.projectpetcare.activities.Pet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ipsec.ike.SaProposal;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import tuan.aprotrain.projectpetcare.databinding.ActivityPetUpdateBinding;

public class PetUpdateActivity extends AppCompatActivity {
    ActivityPetUpdateBinding petUpdateBinding;
    DatabaseReference petUpdateReference;
    Spinner spinnerPetName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        petUpdateBinding = ActivityPetUpdateBinding.inflate(getLayoutInflater());
        setContentView(petUpdateBinding.getRoot());

        spinnerPetName = findViewById(R.id.spnPetUpdate);
        ArrayAdapter<String> petNameAdapter = new ArrayAdapter(PetUpdateActivity.this,
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

        petUpdateBinding.btnPetUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PetName = petUpdateBinding.petNameUpdate.getText().toString();
                String PetHeight = petUpdateBinding.petHeightUpdate.getText().toString();
                String PetWeight = petUpdateBinding.petWeightUpdate.getText().toString();
                String PetIntact = petUpdateBinding.petIntactUpdate.getText().toString();
                String PetNote = petUpdateBinding.petNoteUpdate.getText().toString();
                String petId = petUpdateBinding.spnPetUpdate.getSelectedItem().toString();
                updatePetDate(PetName, PetHeight, PetWeight, PetIntact, PetNote, petId);
            }
        });
    }

    private void updatePetDate(String PetName, String PetHeight, String PetWeight, String PetIntact, String PetNote, String petId) {
        HashMap Pet = new HashMap();
        Pet.put("petName", PetName);
        Pet.put("petHeight", PetHeight);
        Pet.put("petWeight", PetWeight);
        Pet.put("intact", PetIntact);
        Pet.put("notes", PetNote);

        petUpdateReference = FirebaseDatabase.getInstance().getReference("Pets");
        petUpdateReference.child(petId).updateChildren(Pet).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    petUpdateBinding.petNameUpdate.setText("");
                    petUpdateBinding.petHeightUpdate.setText("");
                    petUpdateBinding.petWeightUpdate.setText("");
                    petUpdateBinding.petIntactUpdate.setText("");
                    petUpdateBinding.petNoteUpdate.setText("");

                    Toast.makeText(PetUpdateActivity.this, "Update data complete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private List<String> getListPetName() {
        //reference.child("Categories");
        petUpdateReference = FirebaseDatabase.getInstance().getReference("Pets");
        List<String> petNameList = new ArrayList<>();
        petNameList.add("Choose Your Pet");
        petUpdateReference.addValueEventListener(new ValueEventListener() {
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
