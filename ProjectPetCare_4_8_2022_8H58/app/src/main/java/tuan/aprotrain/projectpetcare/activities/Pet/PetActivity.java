package tuan.aprotrain.projectpetcare.activities.Pet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.activities.LoginActivity;
import tuan.aprotrain.projectpetcare.entity.Pet;


public class PetActivity extends AppCompatActivity {

    EditText editTextPetName,
            editTextPetKind,
            editTextPetSpecies,
            editTextPetGender,
            editTextPetBirth,
            editTextPetHeight,
            editTextPetWeight,
            editTextPetColor,
            editTextPetIntact,
            editTextPetNote;
    Button btnPetAdd;
    long petId ;
    DatabaseReference refPet;
    private Boolean isUpdating = false;
    Pet pets;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);


        editTextPetName = findViewById(R.id.petName);
        editTextPetKind = findViewById(R.id.petKind);
        editTextPetSpecies = findViewById(R.id.petSpecies);
        editTextPetGender = findViewById(R.id.petGender);
        editTextPetBirth = findViewById(R.id.petBirth);
        editTextPetHeight = findViewById(R.id.petHeight);
        editTextPetWeight = findViewById(R.id.petWeight);
        editTextPetColor = findViewById(R.id.petColor);
        editTextPetIntact = findViewById(R.id.petIntact);
        editTextPetNote = findViewById(R.id.petNote);
        btnPetAdd = findViewById(R.id.btnPetAdd);



        btnPetAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUpdating = true;
                if (TextUtils.isEmpty(editTextPetName.getText().toString())|| TextUtils.isEmpty(editTextPetKind.getText().toString()) ||
                        TextUtils.isEmpty(editTextPetSpecies.getText().toString()) || TextUtils.isEmpty(editTextPetGender.getText().toString())
                        ||TextUtils.isEmpty(editTextPetBirth.getText().toString()) || TextUtils.isEmpty(editTextPetHeight.getText().toString())
                        ||TextUtils.isEmpty(editTextPetWeight.getText().toString()) || TextUtils.isEmpty(editTextPetColor.getText().toString())
                        ||TextUtils.isEmpty(editTextPetIntact.getText().toString())) {
                    Toast.makeText(PetActivity.this, "All field are required", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String PetName = editTextPetName.getText().toString();
                    String PetKind = editTextPetKind.getText().toString();
                    String PetSpecies = editTextPetSpecies.getText().toString();
                    String PetGender = editTextPetGender.getText().toString();
                    String PetBirth = editTextPetBirth.getText().toString();
                    Float PetHeight = Float.parseFloat(String.valueOf(editTextPetHeight.getText().toString()));
                    Float PetWeight = Float.parseFloat(String.valueOf(editTextPetBirth.getText().toString()));
                    String PetColor = editTextPetColor.getText().toString();
                    String PetIntact = editTextPetIntact.getText().toString();
                    String PetNote = editTextPetNote.getText().toString();

                    PetAdds(PetName, PetKind, PetSpecies, PetGender, PetBirth, PetHeight, PetWeight, PetColor, PetIntact, PetNote);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(PetActivity.this, LoginActivity.class ));
        } else {

        }
    }
    public void PetAdds(String PetName,String PetKind,String PetSpecies,String PetGender,String PetBirth,Float PetHeight,Float PetWeight,String PetColor,String PetIntact,String PetNote){
        refPet = FirebaseDatabase.getInstance().getReference().child("Pets");
        pets = new Pet();

        refPet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!isUpdating) {
                    return;
                }
                petId = System.currentTimeMillis();
                pets.setPetId(petId);
                pets.setPetName(PetName);
                pets.setKind(PetKind);
                pets.setSpecies(PetSpecies);
                pets.setGender(PetGender);
                pets.setBirthDate(PetBirth);
                pets.setPetHeight(PetHeight);
                pets.setPetWeight(PetWeight);
                pets.setColor(PetColor);
                pets.setIntact(PetIntact);
                pets.setNotes(PetNote);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                pets.setUserId(user.getUid());

                refPet.child(String.valueOf(petId)).setValue(pets);
                isUpdating = false;
                Toast.makeText(PetActivity.this, "Data Inserted Succesfully", Toast.LENGTH_LONG).show();
                //finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isUpdating = false;
            }
        });
    }
}