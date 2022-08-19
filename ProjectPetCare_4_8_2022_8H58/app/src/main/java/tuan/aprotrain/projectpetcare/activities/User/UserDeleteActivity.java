package tuan.aprotrain.projectpetcare.activities.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.User;

public class UserDeleteActivity extends AppCompatActivity {
    Spinner spinnerUserName;
    Button btnUserDelete;
    DatabaseReference refUserDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_delete);
        btnUserDelete = findViewById(R.id.btnUserDelete);
        refUserDelete = FirebaseDatabase.getInstance().getReference("Users" );

        spinnerUserName = findViewById(R.id.spnUserDelete);
        ArrayAdapter<String> userNameAdapter = new ArrayAdapter<>(UserDeleteActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                getListUserName());
        userNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserName.setAdapter(userNameAdapter);
        spinnerUserName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnUserDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserEmail = spinnerUserName.getSelectedItem().toString();
                UserDelete(UserEmail);
            }
        });

    }

    private void UserDelete(String userEmail) {
        refUserDelete = FirebaseDatabase.getInstance().getReference("Users");
        refUserDelete.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(user ->{
                    if(user.getValue(User.class).getEmail().equals(userEmail)){
                        refUserDelete.child(String.valueOf(user.getValue(User.class).getUserId())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(UserDeleteActivity.this, "User has been deleted", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(UserDeleteActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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

    private List<String> getListUserName() {
        List<String> userNameList = new ArrayList<>();
        refUserDelete = FirebaseDatabase.getInstance().getReference("Users");
        userNameList.add("Choose Users");
        refUserDelete.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    //Pet pet = petSnapshot.getValue(Pet.class);
                    userNameList.add(userSnapshot.child("email").getValue(String.class));
                    //System.out.println("Service:"+serviceSnapshot.child("serviceName").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return userNameList;
    }
}