package tuan.aprotrain.projectpetcare.Adapter;

import android.content.Context;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DisplayPetImageAdapter extends RecyclerView.Adapter<DisplayPetImageAdapter.ViewHolder> {
    private static final String tag =  "RecyclerView";
    private Context mContext;
    private ArrayList<Image> images;
    private ArrayList<Image> selectedItems = new ArrayList<>();

    boolean isSelectAll=false;
    boolean isSelectMode = false;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;

    public DisplayPetImageAdapter(Context mContext, ArrayList<Image> images) {
        this.mContext = mContext;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_display_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(images.get(position).getFileName());
        //Glide with
        Glide.with(mContext).load(images.get(position).getUrl()).into(holder.imageView);
// set text on text view

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedItems.clear();
                // check condition
                if (!isSelectMode)
                {
                    // when action mode is not enable
                    // initialize action mode
                    ActionMode.Callback callback=new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            // initialize menu inflater
                            MenuInflater menuInflater= mode.getMenuInflater();
                            // inflate menu
                            menuInflater.inflate(R.menu.image_pet_menu,menu);
                            // return true
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            MenuItem uploadItem = menu.findItem(R.id.itemUpload);
                            uploadItem.setVisible(false);
                            // when action mode is prepare
                            // set isEnable true
                            isSelectMode=true;
                            // create method
                            ClickItem(holder);
                            // set observer on getText method
//                            mainViewModel.getText().observe((LifecycleOwner) activity
//                                    , new Observer<String>() {
//                                        @Override
//                                        public void onChanged(String s) {
//                                            // when text change
//                                            // set text on action mode title
//                                            mode.setTitle(String.format("%s Selected",s));
//                                        }
//                                    });
                            // return true
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            mStorage = FirebaseStorage.getInstance();
                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Image");
                            // when click on action mode item
                            // get item  id
                            int id=item.getItemId();
                            // use switch condition
                            switch(id)
                            {
                                case R.id.menu_delete: //lỗi
                                    // when click on delete
                                    // use for loop
                                    if(images.size()==0)
                                    {
                                        // when array list is empty
                                        // visible text view
                                    }else{
                                        for(int i =0; i < selectedItems.size();i++){
                                            StorageReference sReference = mStorage.getReferenceFromUrl(selectedItems.get(i).getUrl());
                                            String idDb = String.valueOf(selectedItems.get(i).getImageId());

                                            //begin remove in realtime db
                                            DatabaseReference itemImage = FirebaseDatabase.getInstance().getReference("Image").child(idDb);
                                            itemImage.removeValue();
                                            //end remove in realtime db

                                            //begin remove in storage
                                            sReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Uh-oh, an error occurred!
                                                }
                                            });
                                            //end remove in storage

//                                            images.remove(finalI);
//                                            int finalI = Integer.parseInt(idDb);
//
                                        }
//
                                    }

                                    // check condition

                                    // finish action mode
                                    mode.finish();
                                    break;

                                case R.id.menu_select_all:
                                    // when click on select all
                                    // check condition
                                    if(selectedItems.size()==images.size())
                                    {
                                        // when all item selected
                                        // set isselectall false
                                        isSelectAll=false;
                                        // create select array list
                                        selectedItems.clear();
                                    }
                                    else
                                    {
                                        // when  all item unselected
                                        // set isSelectALL true
                                        isSelectAll=true;
                                        // clear select array list
                                        selectedItems.clear();
                                        // add value in select array list
                                        selectedItems.addAll(images);
                                    }
                                    // set text on view model
//                                    mainViewModel.setText(String .valueOf(selectList.size()));
                                    // notify adapter
                                    notifyDataSetChanged();
                                    break;
                            }
                            // return true
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            // when action mode is destroy
                            // set isEnable false
                            isSelectMode=false;
                            // set isSelectAll false
                            isSelectAll=false;
                            // clear select array list
                            selectedItems.clear();
                            // notify adapter
                            notifyDataSetChanged();
                        }
                    };
                    // start action mode
                    ((AppCompatActivity) v.getContext()).startActionMode(callback);
                }
                else
                {
                    // when action mode is already enable
                    // call method
                    ClickItem(holder);
                }
                // return true
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check condition
                if(isSelectMode)
                {
                    // when action mode is enable
                    // call method
                    ClickItem(holder);
                }
                else
                {
                    // when action mode is not enable
                    // display toast
//                    Toast.makeText(,"You Clicked"+arrayList.get(holder.getAdapterPosition()),
//                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        // check condition
        if(isSelectAll)
        {
            // when value selected
            // visible all check boc image
            holder.check_box.setVisibility(View.VISIBLE);
        }
        else
        {
            // when all value unselected
            // hide all check box image
            holder.check_box.setVisibility(View.GONE);
        }
    }
    private void ClickItem(ViewHolder holder) {

        // get selected item value
        Image s=images.get(holder.getAdapterPosition());
        // check condition
        if(holder.check_box.getVisibility()==View.GONE)
        {
            // when item not selected
            // visible check box image
            holder.check_box.setVisibility(View.VISIBLE);
            // add value in select array list
            selectedItems.add(s);
        }
        else
        {
            // when item selected
            // hide check box image
            holder.check_box.setVisibility(View.GONE);
            // remove value from select arrayList
            selectedItems.remove(s);

        }
    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Widgets
        ImageView imageView, check_box;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textViewImg);
            check_box = itemView.findViewById(R.id.check_box);
        }
    }
}
