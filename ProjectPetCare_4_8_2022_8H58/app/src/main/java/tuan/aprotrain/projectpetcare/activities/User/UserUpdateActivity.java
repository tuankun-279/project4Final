package tuan.aprotrain.projectpetcare.activities.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.activities.LoginActivity;
import tuan.aprotrain.projectpetcare.databinding.ActivityUserUpdateBinding;

public class UserUpdateActivity extends AppCompatActivity {
    ActivityUserUpdateBinding userUpdateBinding;
    DatabaseReference userUpdateReference;
    FirebaseAuth mAuth;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(UserUpdateActivity.this, LoginActivity.class ));
        } else {

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userUpdateBinding = ActivityUserUpdateBinding.inflate(getLayoutInflater());
        setContentView(userUpdateBinding.getRoot());


        userUpdateBinding.btnUserUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserName = userUpdateBinding.userNameUpdate.getText().toString();
                String UserAddress = userUpdateBinding.userAddressUpdate.getText().toString();
                updateUserData(UserName, UserAddress);
            }
        });

    }

    private void updateUserData(String userName, String userAddress) {
        HashMap User = new HashMap();
        User.put("userName", userName);
        User.put("Address", userAddress);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        userUpdateReference = FirebaseDatabase.getInstance().getReference("Users");
        userUpdateReference.child(user.getUid()).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    userUpdateBinding.userNameUpdate.setText("");
                    userUpdateBinding.userAddressUpdate.setText("");

                    Toast.makeText(UserUpdateActivity.this, "Data update complete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}