package tuan.aprotrain.projectpetcare.activities.Category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Category;

public class CategoryActivity extends AppCompatActivity {
    EditText editTextCategoryName;
    long categoryID;
    Button btnCateSave;
    DatabaseReference refCategory;
    private Boolean isUpdating = false;
    Category categories;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        editTextCategoryName = findViewById(R.id.categoryName);
        btnCateSave = findViewById(R.id.btnCateAdd);

        btnCateSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUpdating = true;

                String CategoryName = editTextCategoryName.getText().toString().trim();
                ServiceAdd(CategoryName);

            }
        });
    }
    public void ServiceAdd(String categoryName){
        refCategory = FirebaseDatabase.getInstance().getReference().child("Categories");
        categories = new Category();

        refCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!isUpdating) {
                    return;
                }
                categoryID = System.currentTimeMillis();
                categories.setCategoryId(categoryID);
                categories.setCategoryName(categoryName);

                refCategory.child(String.valueOf(categoryID)).setValue(categories);
                isUpdating = false;
                Toast.makeText(CategoryActivity.this, "Data Inserted Succesfully", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isUpdating = false;
            }
        });
    }
}