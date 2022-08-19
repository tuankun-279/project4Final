package tuan.aprotrain.projectpetcare.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.regex.Pattern;

import tuan.aprotrain.projectpetcare.OTP.SendOTPActivity;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Recycle;
import tuan.aprotrain.projectpetcare.entity.User;

public class LoginActivity extends AppCompatActivity {


    TextInputLayout inputLayoutEmail, inputLayoutPass;
    TextInputEditText uEmail, uPassword;
    private TextView forgot_pass, btnLogin;
    private TextView register;

    ProgressBar progressBar;
    private DatabaseReference reference;
    private User user;
    private Recycle recycle;
    private Boolean isUpdating = false;
    //SharedPreferences pref;
    private FirebaseAuth mAuth;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private final static int RC_SIGN_IN = 123;

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        uEmail = findViewById(R.id.user_email);
        uPassword = findViewById(R.id.pass);
        btnLogin = findViewById(R.id.loginBtn);
        register = findViewById(R.id.textRegister);
        forgot_pass = findViewById(R.id.forgot_pass);
        inputLayoutEmail = findViewById(R.id.texInputLayoutEmail);
        inputLayoutPass = findViewById(R.id.texInputLayoutPass);
        ImageView imageViewGoogle = findViewById(R.id.googleBtn);
        ImageView phoneBtn = findViewById(R.id.phoneBtn);

        reference = FirebaseDatabase.getInstance().getReference().child(User.TABLE_NAME);



        mAuth = FirebaseAuth.getInstance();

        uEmail.addTextChangedListener(new TextWatcher() {
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

        uPassword.addTextChangedListener(new TextWatcher() {
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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = uEmail.getText().toString().trim();
                String password = uPassword.getText().toString().trim();
                if (Verify(email, password)) {
                    login(email, password);
                }
            }
        });
        // google sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        gsc = GoogleSignIn.getClient(this, gso);
        imageViewGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenForgotPassword(Gravity.CENTER);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SendOTPActivity.class));
            }
        });
    }

    public boolean Verify(String email, String password) {
        int i = 0;
        if (TextUtils.isEmpty(email)) {
            i++;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            i++;
        }
        if (TextUtils.isEmpty(password)) {
            i++;
        }
        if (i > 0) {
            isUpdating = false;
            return false;
        } else {
            return true;
        }
    }

    //public boolean i = false;
    public void login(String email, String password) {


        //recycle = new Recycle();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Sign in success, update UI with the signed-in user's information
                            reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    switch (snapshot.child("userRole").getValue(String.class)) {
                                        case "user":
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            break;
                                        case "admin":
                                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                            break;
                                        default:
                                            break;
                                    }
//                                    if (snapshot.child("userRole").getValue(String.class).equals("user")) {
//
//                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            String token = task.getResult();
                                            //String token = task.getResult();
                                            System.out.println("Token: " + token);

                                            String android_id = Settings.Secure.getString(getContentResolver(),
                                                    Settings.Secure.ANDROID_ID);

                                            if (!reference.child(user.getUid()).child("token").getKey().equals(android_id)) {
                                                reference.child(user.getUid()).child("token")
                                                        .child(android_id).setValue(token);
                                            }
                                        }
                                    });
                        } else { // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private FirebaseAuth auth;


    private void OpenForgotPassword(int gravity) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_forgot_pass);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        auth = FirebaseAuth.getInstance();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(false);

        TextInputEditText edtforgot = dialog.findViewById(R.id.edt_email_reset);
        Button btnCancle = dialog.findViewById(R.id.btn_Cancle);
        Button btnReset = dialog.findViewById(R.id.btn_reset);

        TextInputLayout texInputLayoutEmailReset = dialog.findViewById(R.id.texInputLayoutEmailReset);



        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailReset = edtforgot.getText().toString().trim();
                if(emailReset.isEmpty()){
                    texInputLayoutEmailReset.setError("Email is required!");
                    texInputLayoutEmailReset.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(emailReset).matches()) {
                    texInputLayoutEmailReset.setError("Invalid email form !");
                    texInputLayoutEmailReset.requestFocus();
                    return;
                }
                //progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(emailReset).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "Try again! Something wrong happened", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
        dialog.show();
    }

    public void signInGoogle(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser userAuth = mAuth.getCurrentUser();
                            User user = new User(userAuth.getUid(),"", "",userAuth.getEmail(),"user");
                            reference.child(userAuth.getUid()).setValue(user);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}