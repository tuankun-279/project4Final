package tuan.aprotrain.projectpetcare.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import tuan.aprotrain.projectpetcare.OTP.SendOTPActivity;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.User;


public class RegisterActivity extends AppCompatActivity {

    private TextView alreadyHaveAccount, btnRegister;
    private TextInputEditText inputEmail, inputPass, inputRepass;
    TextInputLayout inputLayoutEmail, inputLayoutPass, inputLayoutRePass;

    private User user;
    DatabaseReference referenceUsers;
    //long userId = 0;
    private Boolean isUpdating = false;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        alreadyHaveAccount = findViewById(R.id.textLogin);

        inputEmail = findViewById(R.id.user_email);
        inputPass = findViewById(R.id.pass);
        inputRepass = findViewById(R.id.re_password);

        inputLayoutEmail = findViewById(R.id.texInputLayoutEmail);
        inputLayoutPass = findViewById(R.id.texInputLayoutPass);
        inputLayoutRePass = findViewById(R.id.texInputLayoutPass2);

        btnRegister = findViewById(R.id.loginBtn);
        ImageView phoneBtn = findViewById(R.id.phoneBtn);

        referenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputLayoutEmail.setError("Invalid email form !");
                }
                if (email.length() == 0) {
                    inputLayoutEmail.setError("Required");
                }
                if (email.length() > 0 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputLayoutEmail.setError(null);
                }
            }
        });

        inputPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pass = s.toString().trim();
                if (pass.length() < 6) {
                    inputLayoutPass.setError("Password length must be 6 characters or more");

                }
                if (pass.length() == 0) {
                    inputLayoutPass.setError("Required");
                }
                if (pass.length() >= 6) {
                    inputLayoutPass.setError(null);
                }
            }
        });

        inputRepass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable s) {
                String pass = inputPass.getText().toString().trim();
                String confirm = s.toString().trim();
                if (!confirm.equals(pass)) {
                    inputLayoutRePass.setError("Password confirmation does not match");
                }
                if (confirm.equals(pass)) {
                    inputLayoutRePass.setError(null);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUpdating = true;
                String uEmail = inputEmail.getText().toString().trim();
                String uPass = inputPass.getText().toString().trim();
                String confirm = inputRepass.getText().toString().trim();

                if (validateInput(uEmail, uPass, confirm) == false)
                    Toast.makeText(RegisterActivity.this, "Wrong Validation", Toast.LENGTH_LONG).show();
                else {
                    register(uEmail, uPass);
                }
            }
        });

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, SendOTPActivity.class));
            }
        });
    }

    private boolean validateInput(String email, String password, String confirmpass) {//
        int t = 0;
        if (email.isEmpty()) {
            t++;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            t++;
        }
        if (password.isEmpty()) {
            t++;
        }
        if (confirmpass.isEmpty()) {
            t++;
        }
        if (password.toString().trim().length() < 6) {
            t++;
        }
        if (!password.equals(confirmpass)) {
            t++;
        }
        if (t > 0) {
            isUpdating = false;
            return false;
        } else {
            return true;
        }
    }


    public void register(String email, String pass) {


        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),"", "", email.toLowerCase(), "user");
                            referenceUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                            Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Register error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}