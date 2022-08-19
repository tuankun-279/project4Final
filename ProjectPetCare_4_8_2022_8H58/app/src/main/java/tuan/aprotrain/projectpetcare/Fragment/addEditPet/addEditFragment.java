package tuan.aprotrain.projectpetcare.Fragment.addEditPet;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import tuan.aprotrain.projectpetcare.Adapter.DisplayPetImageAdapter;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.activities.LoginActivity;
import tuan.aprotrain.projectpetcare.activities.Pet.PetActivity;
import tuan.aprotrain.projectpetcare.activities.Pet.PetUpdateActivity;
import tuan.aprotrain.projectpetcare.databinding.ActivityPetUpdateBinding;
import tuan.aprotrain.projectpetcare.databinding.FragmentAddEditBinding;
import tuan.aprotrain.projectpetcare.databinding.FragmentBreedingBinding;
import tuan.aprotrain.projectpetcare.entity.Image;
import tuan.aprotrain.projectpetcare.entity.Pet;

public class addEditFragment extends Fragment {

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
    TextView idUser;
    Button btnSave;
    long petId ;
    DatabaseReference refPet;
    private Boolean isUpdating = false;
    Pet pets;

    ArrayList<String> listKey = new ArrayList<>();

    DatabaseReference refPetId;

    //    private ShareViewModel shareViewModel;
    private FragmentAddEditBinding binding;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    public static addEditFragment newInstance() {
        return new addEditFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

//        drawerLayout = (DrawerLayout)  root.findViewById(R.id.drawerLayoutAddEditPet);
//        toggle = new ActionBarDrawerToggle
//                (
//                        getActivity(),
//                        drawerLayout,
//                        R.string.navigation_drawer_open,
//                        R.string.navigation_drawer_close
//                )
//        {
//        };
//        drawerLayout.setDrawerListener(toggle);
//        toggle.syncState();
//        drawerLayout.setDrawerListener(toggle);

        editTextPetName = root.findViewById(R.id.petName);
        editTextPetKind = root.findViewById(R.id.petKind);
        editTextPetSpecies = root.findViewById(R.id.petSpecies);
        editTextPetGender = root.findViewById(R.id.petGender);
        editTextPetBirth = root.findViewById(R.id.petBirth);
        editTextPetHeight = root.findViewById(R.id.petHeight);
        editTextPetWeight = root.findViewById(R.id.petWeight);
        editTextPetColor = root.findViewById(R.id.petColor);
        editTextPetIntact = root.findViewById(R.id.petIntact);
        editTextPetNote = root.findViewById(R.id.petNote);
        idUser = root.findViewById(R.id.idUser);

        btnSave = root.findViewById(R.id.btnSave);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Long idPet = bundle.getLong("idPet");

//            refPetId = FirebaseDatabase.getInstance().getReference("Pets").child(String.valueOf(idPet));

            refPet = FirebaseDatabase.getInstance().getReference();
            Query query = refPet.child("Pets").orderByChild("petId").equalTo(idPet);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("petId").getValue() != null) {
                            Pet pet = new Pet();
                            editTextPetName.setText(snapshot.child("petName").getValue().toString());
                            editTextPetKind.setText(snapshot.child("kind").getValue().toString());
                            editTextPetSpecies.setText(snapshot.child("species").getValue().toString());
                            editTextPetGender.setText(snapshot.child("gender").getValue().toString());
                            editTextPetBirth.setText(snapshot.child("birthDate").getValue().toString());
                            editTextPetHeight.setText(snapshot.child("petHeight").getValue().toString());
                            editTextPetWeight.setText(snapshot.child("petWeight").getValue().toString());
                            editTextPetColor.setText(snapshot.child("color").getValue().toString());
                            editTextPetIntact.setText(snapshot.child("intact").getValue().toString());
                            editTextPetNote.setText(snapshot.child("notes").getValue().toString());
                            idUser.setText(snapshot.child("userId").getValue().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//            refPetId.addListenerForSingleValueEvent(new ValueEventListener() {
//                @RequiresApi(api = Build.VERSION_CODES.N)
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    snapshot.getChildren().forEach(pet -> {
//                        if (pet.getValue(Pet.class).getPetId() == idPet) {
//
//                            editTextPetName.setText(pet.getValue(Pet.class).getPetName());
//                            editTextPetKind.setText(pet.getValue(Pet.class).getKind());
//                            editTextPetSpecies.setText(pet.getValue(Pet.class).getSpecies());
//                            editTextPetGender.setText(pet.getValue(Pet.class).getGender());
//                            editTextPetBirth.setText(pet.getValue(Pet.class).getBirthDate());
//                            editTextPetHeight.setText((int) pet.getValue(Pet.class).getPetHeight());
//                            editTextPetWeight.setText((int) pet.getValue(Pet.class).getPetWeight());
//                            editTextPetColor.setText(pet.getValue(Pet.class).getColor());
//                            editTextPetIntact.setText(pet.getValue(Pet.class).getIntact());
//                            editTextPetNote.setText(pet.getValue(Pet.class).getNotes());
//                            idUser.setText(pet.getValue(Pet.class).getUserId());
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
        }
//            refPetId = FirebaseDatabase.getInstance().getReference("Pets");
//
//            refPetId.addListenerForSingleValueEvent(new ValueEventListener() {
//                @RequiresApi(api = Build.VERSION_CODES.N)
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    snapshot.getChildren().forEach(pet -> {
//
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });






        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUpdating = true;
                String PetName = editTextPetName.getText().toString();
                String PetKind = editTextPetKind.getText().toString();
                String PetSpecies = editTextPetSpecies.getText().toString();
                String PetGender = editTextPetGender.getText().toString();
                String PetBirth = editTextPetBirth.getText().toString();
                Float PetHeight = Float.parseFloat(String.valueOf(editTextPetHeight.getText().toString()));
                Float PetWeight = Float.parseFloat(String.valueOf(editTextPetWeight.getText().toString()));
                String PetColor = editTextPetColor.getText().toString();
                String PetIntact = editTextPetIntact.getText().toString();
                String PetNote = editTextPetNote.getText().toString();

                PetAddsEdits(PetName, PetKind, PetSpecies, PetGender, PetBirth, PetHeight, PetWeight, PetColor, PetIntact, PetNote);

            }
        });
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        drawerLayout.openDrawer(Gravity.LEFT);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(getActivity(), LoginActivity.class ));
        } else {

        }
    }
    public void PetAddsEdits(String PetName,String PetKind,String PetSpecies,String PetGender,String PetBirth,Float PetHeight,Float PetWeight,String PetColor,String PetIntact,String PetNote){
        refPet = FirebaseDatabase.getInstance().getReference().child("Pets");
        pets = new Pet();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Long idPet = bundle.getLong("idPet");

            HashMap Pet = new HashMap();
            Pet.put("petName", PetName);
            Pet.put("kind", PetKind);
            Pet.put("species", PetSpecies);
            Pet.put("gender", PetGender);
            Pet.put("birthDate", PetBirth);
            Pet.put("petHeight", PetHeight);
            Pet.put("petWeight", PetWeight);
            Pet.put("color", PetColor);
            Pet.put("intact", PetIntact);
            Pet.put("notes", PetNote);

            refPet = FirebaseDatabase.getInstance().getReference("Pets");
            refPet.child(String.valueOf(idPet)).updateChildren(Pet).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        editTextPetName.setText("");
                        editTextPetKind.setText("");
                        editTextPetSpecies.setText("");
                        editTextPetGender.setText("");
                        editTextPetBirth.setText("");
                        editTextPetHeight.setText("");
                        editTextPetWeight.setText("");
                        editTextPetColor.setText("");
                        editTextPetIntact.setText("");
                        editTextPetNote.setText("");
                        idUser.setText("");

                        Toast.makeText(getContext(), "Update data complete", Toast.LENGTH_SHORT).show();
                        NavOptions navOptionsHome = new NavOptions.Builder()
                                .setPopUpTo(R.id.action_home, true)
                                .build();
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_home, null, navOptionsHome);
                    }
                }
            });

        }else{
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
                    Toast.makeText(getActivity(), "Data Inserted Succesfully", Toast.LENGTH_LONG).show();
                    NavOptions navOptionsHome = new NavOptions.Builder()
                            .setPopUpTo(R.id.action_home, true)
                            .build();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_home, null, navOptionsHome);
                    //finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    isUpdating = false;
                }
            });
        }
    }
}
