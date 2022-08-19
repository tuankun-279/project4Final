package tuan.aprotrain.projectpetcare.Fragment.breeding;

import static java.util.stream.Collectors.toList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.RequiresApi;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import tuan.aprotrain.projectpetcare.Adapter.PetAdapter;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.activities.BookingActivity;
import tuan.aprotrain.projectpetcare.activities.LoginActivity;
import tuan.aprotrain.projectpetcare.entity.Image;
import tuan.aprotrain.projectpetcare.entity.Pet;

public class BreedingFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private Context context;

    private DatabaseReference reference;

    private View view;


    LinearLayout firstLinearLayout;
    LinearLayout secondLinearLayout;
    LinearLayout showoHideButtonFilter;

    private ArrayList<Pet> petListSelected = new ArrayList<>();
    private ArrayList<String> spinnersSpeciesList = new ArrayList<>();


    private String title;

    //new Search
    private PetAdapter petAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private MenuItem menuItem;
    private SearchView searchView;
    private Spinner spinnerPetColor, spinnerSpecies, spinnerGender,spinnerKind;
    //private Spinner spinnerUserPetName;
    private List<String> getPetColor, getSpecies, getKind, getGender;
    private List<Pet> userPets;
    private ArrayList<Image> petImages;
    private List<Pet> petList;


    public static BreedingFragment newInstance() {
        return new BreedingFragment();
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_breeding, container, false);

        firstLinearLayout = view.findViewById(R.id.firstLinearLayout);
        secondLinearLayout = view.findViewById(R.id.secondLinearLayout);
        firstLinearLayout.setVisibility(View.GONE);
        secondLinearLayout.setVisibility(View.GONE);

        searchView = view.findViewById(R.id.petListSearchView);
        spinnerPetColor = view.findViewById(R.id.sp_color_piker);
        spinnerSpecies = view.findViewById(R.id.sp_species);
        spinnerGender = view.findViewById(R.id.sp_gender);
        spinnerKind = view.findViewById(R.id.sp_kind);
        //spinnerUserPetName = view.findViewById(R.id.sp_userPetName);

        reference = FirebaseDatabase.getInstance().getReference();

        petImages = new ArrayList<>();
        petList = new ArrayList<>();
        getPetColor = new ArrayList<>();
        getGender = new ArrayList<>();
        getSpecies = new ArrayList<>();
        getKind = new ArrayList<>();
        userPets = new ArrayList<>();


        recyclerView = view.findViewById(R.id.recycleViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getListPetsFromRealTimeDataBase();

        petAdapter = new PetAdapter(petList, petImages);
        recyclerView.setAdapter(petAdapter);


        ArrayAdapter<String> petColorAdapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getPetColor);
        petColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPetColor.setAdapter(petColorAdapter);
        spinnerPetColor.setOnItemSelectedListener(this);

        ArrayAdapter<String> petSpeciesAdapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getSpecies);
        petSpeciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecies.setAdapter(petSpeciesAdapter);
        spinnerSpecies.setOnItemSelectedListener(this);


        ArrayAdapter<String> petKindAdapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getKind);
        petKindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKind.setAdapter(petKindAdapter);
        spinnerKind.setOnItemSelectedListener(this);

        ArrayAdapter<String> petGenderAdapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getGender);
        petGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(petGenderAdapter);
        spinnerGender.setOnItemSelectedListener(this);
        initSearchWidgets();


        //button for hide and show after clicking search icon
        TextView showHide = view.findViewById(R.id.tv_show_hide);
        showHide.setOnClickListener(new View.OnClickListener() {
            int count = 0;
            @Override
            public void onClick(View view) {
                count ++;
                if (count%2 == 0){
                    firstLinearLayout.setVisibility(View.GONE);
                    secondLinearLayout.setVisibility(View.GONE);
                }else {
                    firstLinearLayout.setVisibility(View.VISIBLE);
                    secondLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        return view;
    }


    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private void initSearchWidgets() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                search(newText);

                return true;
            }
        });

        spinnerPetColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getItemAtPosition(position).equals("Color")) {
                    search(parent.getItemAtPosition(position).toString());
                }else {
                    search("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getItemAtPosition(position).equals("Gender")) {
                    search(parent.getItemAtPosition(position).toString());
                }else {
                    search("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getItemAtPosition(position).equals("Kind")) {
                    search(parent.getItemAtPosition(position).toString());
                }else {
                    search("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerSpecies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getItemAtPosition(position).equals("Species")) {
                    search(parent.getItemAtPosition(position).toString());
                }else {
                    search("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    private void search(String query) {

//        for (Pet pet : petList) {
//            if (pet.getKind().toLowerCase().contains(query.toLowerCase()) || pet.getSpecies().toLowerCase().contains(query.toLowerCase())) {
//
//                filteredList.add(pet);
//                petAdapter.setFilteredList(filteredList);
//            }
//        }
//        petAdapter.notifyDataSetChanged();
        List<Pet> filteredList = petList.stream()
                .filter(pet ->
                                (pet.getSpecies().toLowerCase().contains(query))
                                || (pet.getGender().toLowerCase().equals(query))
                                || (pet.getKind().toLowerCase().contains(query))
                                || (pet.getColor().toLowerCase().contains(query)))
                .collect(toList());
        petAdapter.setFilteredList(filteredList);
        petAdapter.notifyDataSetChanged();

    }

    private void getListPetsFromRealTimeDataBase() {
        ArrayList<String> addColor = new ArrayList<>();
        ArrayList<String> addGender = new ArrayList<>();
        ArrayList<String> addKind = new ArrayList<>();
        ArrayList<String> addSpecies = new ArrayList<>();
        ArrayList<Pet> addUserPet = new ArrayList<>();
        getPetColor.add(0, "Color");
        getGender.add(0,"Gender");
        getKind.add(0,"Kind");
        getSpecies.add(0,"Species");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference.child("Pets").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    petList.clear();
                    snapshot.getChildren().forEach(petSnapshot -> {
                        if (!petSnapshot.getValue(Pet.class).getUserId().equals(user.getUid())) {
                            Pet pet = petSnapshot.getValue(Pet.class);
                            petList.add(pet);
                            //add petColor
                            addColor.add(pet.getColor().toLowerCase());
                            for (String color : addColor) {
                                if (!getPetColor.contains(color)) {
                                    getPetColor.add(color);
                                }
                            }
                            addGender.add(pet.getGender().toLowerCase());
                            for (String gender : addGender) {
                                if (!getGender.contains(gender)) {
                                    getGender.add(gender);
                                }
                            }
                            addKind.add(pet.getKind().toLowerCase());
                            for (String kind : addKind) {
                                if (!getKind.contains(kind)) {
                                    getKind.add(kind);
                                }
                            }
                            addSpecies.add(pet.getSpecies().toLowerCase());
                            for (String species : addSpecies) {
                                if (!getSpecies.contains(species)) {
                                    getSpecies.add(species);
                                }
                            }

                            reference.child("Image").orderByChild("petId").equalTo(petSnapshot.getValue(Pet.class).getPetId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot imageSnapshot) {
                                    if (imageSnapshot.exists()) {
                                        try {
                                            imageSnapshot.getChildren().forEach(image -> {
                                                petImages.add(image.getValue(Image.class));
                                                int i = 0;
                                            });
                                            petAdapter.notifyDataSetChanged();
                                        } catch (NumberFormatException e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            addUserPet.add(petSnapshot.getValue(Pet.class));
                            for (Pet pet : addUserPet) {
                                if (!userPets.contains(pet)) {
                                    userPets.add(pet);
                                }
                            }
                        }
                    });

                    petAdapter.notifyDataSetChanged();
                    openSmartFilterDialog(Gravity.CENTER);
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openSmartFilterDialog(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.breeding_dialog_choose_pet);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(false);

        Spinner spinnerUserPetName = dialog.findViewById(R.id.sp_userPetName);


        List<String> listOfUserPet = new ArrayList<>();
        listOfUserPet.add(0, "Choose your pet");
        for (Pet pet : userPets) {
            listOfUserPet.add(pet.getPetName());
        }
        ArrayAdapter<String> userPetNameAdapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listOfUserPet);
        userPetNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserPetName.setAdapter(userPetNameAdapter);
        spinnerUserPetName.setOnItemSelectedListener(this);

        Button btnCancel = dialog.findViewById(R.id.btn_cancel_smart_search);
        Button btnStartSearch = dialog.findViewById(R.id.btn_start_search);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnStartSearch.setOnClickListener(new View.OnClickListener() {
            @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //check logic

                if (!spinnerUserPetName.getSelectedItem().toString().trim().equals("Choose your pet")) {
                    for (Pet pet : userPets) {
                        if (pet.getPetName().equals(spinnerUserPetName.getSelectedItem().toString().trim())) {
                            if (pet.getGender().equals("Male")) {
                                search("Female");
                            } else {
                                search("Male");
                            }
                            search(pet.getSpecies());
                            search(pet.getKind());

                            ArrayAdapter myAdap = (ArrayAdapter) spinnerPetColor.getAdapter(); //cast to an ArrayAdapter

                            int spinnerPosition = myAdap.getPosition(pet.getColor());

//set the default according to value
                            spinnerPetColor.setSelection(spinnerPosition);
                            dialog.dismiss();
                        }
                    }
                }
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