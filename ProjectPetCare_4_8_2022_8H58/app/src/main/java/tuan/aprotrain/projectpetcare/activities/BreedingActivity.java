package tuan.aprotrain.projectpetcare.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import tuan.aprotrain.projectpetcare.Adapter.PetAdapter;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.databinding.ActivityMainBinding;
import tuan.aprotrain.projectpetcare.entity.Pet;

public class BreedingActivity extends AppCompatActivity {
    //private ActivityMainBinding binding;

    private RecyclerView rv_pet_id;
    private PetAdapter petAdapter;
    private Spinner spinnerGender;
    private Spinner spinnerSpecies;

    LinearLayout ckAdditionalSearchCheckBox;
    LinearLayout ckAdditionalSearchSpinner;
    LinearLayout showoHideButtonFilter;

    private DatabaseReference reference;

    private ArrayList<Pet> petList;
    private ArrayList<Pet> petListSelected = new ArrayList<>();
    private ArrayList<String> spinnersSpeciesList = new ArrayList<>();

    // Define ActionBar object
    Toolbar toolbar;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breeding);

        toolbar = findViewById(R.id.toolbar_breed);
        setSupportActionBar(toolbar);

        title = String.valueOf(getTitle());

        ckAdditionalSearchCheckBox = findViewById(R.id.ck_additional_search_checkBox);
        ckAdditionalSearchSpinner = findViewById(R.id.ck_additional_search_spinner);
        showoHideButtonFilter = findViewById(R.id.show_or_hide_additional_search);

        ckAdditionalSearchCheckBox.setVisibility(View.GONE);
        ckAdditionalSearchSpinner.setVisibility(View.GONE);
        showoHideButtonFilter.setVisibility(View.GONE);

        rv_pet_id = findViewById(R.id.rv_pet_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_pet_id.setLayoutManager(linearLayoutManager);

        reference = FirebaseDatabase.getInstance().getReference();

        petList = new ArrayList<>();
//        petAdapter = new PetAdapter(petList);
        rv_pet_id.setAdapter(petAdapter);

        getListPetsFromRealTimeDataBase();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void getListPetsFromRealTimeDataBase() {

        //Real time database

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference databaseReference = database.getReference("Pet");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference.child("Pets").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(petSnapshot -> {
                    if (!petSnapshot.getValue(Pet.class).getUserId().equals(user.getUid())) {
                        Pet pet = petSnapshot.getValue(Pet.class);
                        petList.add(pet);
                    }
                });
                petAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //setting action bar
//        actionBar = getSupportActionBar();
//        ColorDrawable colorDrawable
//                = new ColorDrawable(Color.parseColor("#0F9D58"));
//        // Set BackgroundDrawable action bar
//        actionBar.setBackgroundDrawable(colorDrawable);

        getMenuInflater().inflate(R.menu.search_breeding_pet, menu);

        MenuItem menuItem = menu.findItem(R.id.search_id);

        SearchView searchView = (SearchView) menuItem.getActionView();

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchView.setQueryHint("Type here to search");

                //button for hide and show after clicking search icon
                Button btnHideAndShowAdditionalSearch = findViewById(R.id.btn_hide_show);
                btnHideAndShowAdditionalSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int xx = 0;
                        int currentTag = btnHideAndShowAdditionalSearch.getTag() == null ? 0 :
                                (int) btnHideAndShowAdditionalSearch.getTag();
                        currentTag++;
                        btnHideAndShowAdditionalSearch.setTag(currentTag);
                        if (currentTag % 2 == 0) {
                            btnHideAndShowAdditionalSearch.setText("Show");
                            ckAdditionalSearchCheckBox.setVisibility(View.VISIBLE);
                            ckAdditionalSearchSpinner.setVisibility(View.VISIBLE);
                        } else {
                            btnHideAndShowAdditionalSearch.setText("Hide");
                            ckAdditionalSearchCheckBox.setVisibility(View.GONE);
                            ckAdditionalSearchSpinner.setVisibility(View.GONE);
                        }
                    }
                });

                //display after clicking search icon
                ckAdditionalSearchCheckBox.setVisibility(View.VISIBLE);
                ckAdditionalSearchSpinner.setVisibility(View.VISIBLE);
                showoHideButtonFilter.setVisibility(View.VISIBLE);

                //begin display spinner species
                for (Pet petItem : petList) {
                    spinnersSpeciesList.add(petItem.getSpecies());
                }
                Set<String> speciesWithoutDuplicate = new LinkedHashSet<String>(spinnersSpeciesList);

                speciesWithoutDuplicate.remove("Species");
                speciesWithoutDuplicate.remove("All");

                spinnersSpeciesList.clear();

                spinnersSpeciesList.add("Species");
                spinnersSpeciesList.add("All");

                for (String i : speciesWithoutDuplicate) {
                    spinnersSpeciesList.add(i);
                }
                //end display spinner species

                //begin spinnerSpecies spinner
                spinnerSpecies = (Spinner) findViewById(R.id.speciesFilter);
                ArrayAdapter<String> myadapterSpecies = new ArrayAdapter<String>(BreedingActivity.this,
                        android.R.layout.simple_list_item_1, spinnersSpeciesList);
                myadapterSpecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSpecies.setAdapter(myadapterSpecies);
                // search by species
                spinnerSpecies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = parent.getItemAtPosition(position).toString();

                        if (selectedItem.equals("All")) {
                            petListSelected.clear();
//                            petAdapter = new PetAdapter(petList);
                            rv_pet_id.setAdapter(petAdapter);
                        } else {
                            petListSelected.clear();
                            for (Pet petItemOnlySpecies : petList) {
                                if (selectedItem.equals(petItemOnlySpecies.getSpecies())) {
                                    petListSelected.add(petItemOnlySpecies);
                                }
                            }
//                            petAdapter = new PetAdapter(petListSelected);
                            rv_pet_id.setAdapter(petAdapter);
                        }
                        //end spinnerSpecies spinner
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //begin gender spinner
                spinnerGender = (Spinner) findViewById(R.id.id_gender_spinner);
                ArrayAdapter<String> myadapter = new ArrayAdapter<String>(BreedingActivity.this,
                        android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender));
                myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGender.setAdapter(myadapter);

                //search by gender
                spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = parent.getItemAtPosition(position).toString();

                        if (selectedItem.equals("Male")) {
                            petListSelected.clear();
                            for (Pet petItem : petList) {
                                if (petItem.getGender().equals("male")) {
                                    petListSelected.add(petItem);
                                }
                            }

//                            petAdapter = new PetAdapter(petListSelected);
                            rv_pet_id.setAdapter(petAdapter);
                        } else if (selectedItem.equals("Female")) {
                            petListSelected.clear();

                            for (Pet petItem : petList) {
                                if (petItem.getGender().equals("female")) {
                                    petListSelected.add(petItem);
                                }
                            }

//                            petAdapter = new PetAdapter(petListSelected);
                            rv_pet_id.setAdapter(petAdapter);
                        } else {
                            petListSelected.clear();
//                            petAdapter = new PetAdapter(petList);
                            rv_pet_id.setAdapter(petAdapter);
                        }

                    }

                    //end  gender spinner
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //search by input text
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String searchText) {


                        CheckBox colorFilter = findViewById(R.id.colorFilter);
                        CheckBox breedFilter = findViewById(R.id.breedFilter);

                        //check 2: color and breed
                        if ((colorFilter.isChecked() == true && breedFilter.isChecked() == true)
                                || (colorFilter.isChecked() == false && breedFilter.isChecked() == false)) {
                            colorAbreedFilterList(searchText);
                        }
                        //check 1: color or breed
                        if (colorFilter.isChecked() == true) {
                            colorFilter(searchText);
                        } else if (breedFilter.isChecked() == true) {
                            breedFilter(searchText);
                        }
                        return true;
                    }

                });
                return true;
            }

            //begin click back icon to close addition search
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ckAdditionalSearchCheckBox.setVisibility(View.GONE);
                ckAdditionalSearchSpinner.setVisibility(View.GONE);
                showoHideButtonFilter.setVisibility(View.GONE);
                return BreedingActivity.super.onCreateOptionsMenu(menu);
            }
            //end click back icon to close search
        });

        return super.onCreateOptionsMenu(menu);
    }


    private void colorAbreedFilterList(String searchText) {
        List<Pet> filteredPet = new ArrayList<>();
        for (Pet item : petList) {

            if (item.getColor().toLowerCase().contains(searchText.toLowerCase())) {
                filteredPet.add(item);
            }
        }
        if (filteredPet.isEmpty()) {
//            Toast.makeText(this, "No Data Found", Toast.LENGTH_LONG).show();
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#D41717"));
            // Set BackgroundDrawable
            toolbar.setBackground(colorDrawable);
        } else {
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#0F9D58"));
            // Set BackgroundDrawable
            toolbar.setBackground(colorDrawable);

            petAdapter.setFilteredList(filteredPet);
        }
    }

    public void breedFilter(String searchText) {
        List<Pet> filteredPet = new ArrayList<>();
        for (Pet item : petList) {

            if (item.getKind().toLowerCase().contains(searchText.toLowerCase())) {
                filteredPet.add(item);
            }
        }
        if (filteredPet.isEmpty()) {
//            Toast.makeText(this, "No Data Found", Toast.LENGTH_LONG).show();
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#D41717"));
            // Set BackgroundDrawable
            toolbar.setBackground(colorDrawable);
        } else {
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#0F9D58"));
            // Set BackgroundDrawable
            toolbar.setBackground(colorDrawable);

            petAdapter.setFilteredList(filteredPet);
        }
    }

    // search by color
    public void colorFilter(String searchText) {

        List<Pet> filteredPet = new ArrayList<>();
        for (Pet item : petList) {

            if (item.getColor().toLowerCase().contains(searchText.toLowerCase())) {
                filteredPet.add(item);
            }
        }
        if (filteredPet.isEmpty()) {
//            Toast.makeText(this, "No Data Found", Toast.LENGTH_LONG).show();

            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#D41717"));
            // Set BackgroundDrawable
            toolbar.setBackground(colorDrawable);

        } else {
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#0F9D58"));
            // Set BackgroundDrawable
            toolbar.setBackground(colorDrawable);

            petAdapter.setFilteredList(filteredPet);
        }
    }
    //Intent contextBreeding = new Intent(getBaseContext(), BreedingActivity.this);

}
