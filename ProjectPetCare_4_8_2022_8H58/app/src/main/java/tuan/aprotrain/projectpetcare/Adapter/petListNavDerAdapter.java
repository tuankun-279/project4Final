package tuan.aprotrain.projectpetcare.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import tuan.aprotrain.projectpetcare.Fragment.addEditPet.addEditFragment;
import tuan.aprotrain.projectpetcare.activities.MainActivity;
import tuan.aprotrain.projectpetcare.entity.Image;
import tuan.aprotrain.projectpetcare.entity.Pet;

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
import java.util.List;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Image;
import tuan.aprotrain.projectpetcare.entity.Pet;

public class petListNavDerAdapter extends RecyclerView.Adapter<petListNavDerAdapter.ViewHolder> {

    ArrayList<Pet> pets;
    ArrayList<Pet> petSelected = new ArrayList<>();

    private ArrayList<Image> images = new ArrayList<>();
    ;
    Context context;
    private long selectedPet;

    DatabaseReference refPetDelete;

    public petListNavDerAdapter(ArrayList<Pet> pets, ArrayList<Image> images, Context context) {
        this.pets = pets;
        this.images = images;
        this.context = context;
    }

    public petListNavDerAdapter() {
    }

    public Long getSelectedPet() {
        int i = 0;
        return selectedPet;
    }

    @NonNull
    @Override
    public petListNavDerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_item_nav, parent, false);
        return new petListNavDerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        holder.name.setText((CharSequence) pets.get(position)); // set position value vào recycler view.
        holder.name.setText(pets.get(position).getPetName());
        holder.kind.setText("Kind: "+pets.get(position).getKind());

        if (images == null) {
            images = new ArrayList<>();
        }else{
            for (Image image : images) {
                if (image.getPetId() == pets.get(position).getPetId()) {
                    //Glide with
                    Glide.with(context).load(image.getUrl()).apply(new RequestOptions().override(70, 70)).into(holder.imagePet);
                    break;
                }
            }
        }


        //int i  = position;
        holder.itemView.setOnLongClickListener((View v) -> {
            selectedPet = pets.get(position).getPetId();
            ViewHolder vh = new ViewHolder(v);
            v.setOnCreateContextMenuListener(vh);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return pets == null ? 0 : pets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView name, kind;
        ImageView imagePet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.first_name);
            kind = itemView.findViewById(R.id.owner_pet_kind);
            imagePet = (ImageView) itemView.findViewById(R.id.iv_pet_id);
            if (context instanceof MainActivity) {
                ((MainActivity) context).registerForContextMenu(itemView);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Pet Options");
            menu.add(getAdapterPosition(), 101, 0, "Edit it");
            menu.add(getAdapterPosition(), 102, 1, "Remove it");
            menu.add(getAdapterPosition(), 103, 2, "Pet Images");
        }


    }

    public long EditSelectedPet() {
        return selectedPet;
    }

    public void RemoveSelectedPet() {
        DeletePet(selectedPet);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.pets.removeIf(item -> item.equals(selectedPet));
        }
        notifyDataSetChanged();
    }

    List<String> listForeignPetIds = new ArrayList<>();
    List<String> listImageIds = new ArrayList<>();

    private void DeletePet(long selectedPet) {

        refPetDelete = FirebaseDatabase.getInstance().getReference("Image");
        refPetDelete.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(image ->{
                    if(image.getValue(Image.class).getPetId() == selectedPet){
                        refPetDelete.child(String.valueOf(image.getValue(Pet.class).getPetId())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    refPetDelete = FirebaseDatabase.getInstance().getReference("Pets");

                                    refPetDelete.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            snapshot.getChildren().forEach(pet -> {
                                                if (pet.getValue(Image.class).getPetId() == selectedPet) {
                                                    refPetDelete.child(String.valueOf(pet.getValue(Pet.class).getPetId())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {

                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    });
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }else{
                                    refPetDelete = FirebaseDatabase.getInstance().getReference("Pets");

                                    refPetDelete.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            snapshot.getChildren().forEach(pet -> {
                                                if (pet.getValue(Image.class).getPetId() == selectedPet) {
                                                    refPetDelete.child(String.valueOf(pet.getValue(Pet.class).getPetId())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {

                                                        public void onComplete(@NonNull Task<Void> task) {

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

    private void displayPetId() {
//        selectedPet = pets.get(position).getPetId();
    }
}
