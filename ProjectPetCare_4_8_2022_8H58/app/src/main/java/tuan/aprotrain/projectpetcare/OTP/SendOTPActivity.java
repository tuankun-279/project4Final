package tuan.aprotrain.projectpetcare.OTP;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.User;

public class SendOTPActivity extends AppCompatActivity {

    public static final Pattern PhonePattern = Pattern.compile("/(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})\\b/");
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_o_t_p);

        final EditText inputPhone = findViewById(R.id.inputPhone);
        Button btnGetOtp = findViewById(R.id.btnGetOtp);

        final ProgressBar processBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        btnGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputPhone.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SendOTPActivity.this, "Enter Your Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.PHONE.matcher(inputPhone.getText().toString()).matches()) {
                    Toast.makeText(SendOTPActivity.this, "Enter Your Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");


                processBar.setVisibility(View.VISIBLE);
                btnGetOtp.setVisibility(View.INVISIBLE);

                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber("+84" + inputPhone.getText().toString())       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(SendOTPActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            processBar.setVisibility(View.GONE);
                                            btnGetOtp.setVisibility(View.INVISIBLE);
                                            Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                                            intent.putExtra("mobile", inputPhone.getText().toString());

                                            intent.putExtra("verificationId", verificationId);

                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            processBar.setVisibility(View.GONE);
                                            btnGetOtp.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            processBar.setVisibility(View.GONE);
                                            btnGetOtp.setVisibility(View.VISIBLE);
                                            Toast.makeText(SendOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            System.out.println("Error: " + e.getMessage());
                                        }
                                    })          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
//                }

            }
        });
    }
}