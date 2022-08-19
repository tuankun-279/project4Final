package tuan.aprotrain.projectpetcare.Fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tuan.aprotrain.projectpetcare.Fragment.booking_history.BookingHistoryFragment;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.activities.BookingActivity;
import tuan.aprotrain.projectpetcare.activities.LoginActivity;
import tuan.aprotrain.projectpetcare.databinding.FragmentHomeBinding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageButton;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;

    SharedPreferences prf;
    Button btnLogout, buttonLogout;
    FirebaseAuth mAuth;
    private ImageSlider imageSlider;
    ImageButton petHotel_Btn, petSpa_Btn, petHealth_Btn, petBurry_Btn;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    TextView textView4;

    public static BookingHistoryFragment newInstance() {
        return new BookingHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final DrawerLayout drawerLayout = root.findViewById(R.id.drawerLayout);
//        drawerLayout.findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });

        btnLogout = root.findViewById(R.id.btnLogout);

        petHotel_Btn = root.findViewById(R.id.petHotel_Btn);
        petSpa_Btn = root.findViewById(R.id.petSpa_Btn);
        petHealth_Btn = root.findViewById(R.id.petHealth_Btn);
        petBurry_Btn = root.findViewById(R.id.petBurry_Btn);

        //buttonLogout = root.findViewById(R.id.buttonLogout);

        petHotel_Btn.setOnClickListener(this);
        petSpa_Btn.setOnClickListener(this);
        petHealth_Btn.setOnClickListener(this);
        petBurry_Btn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
       // textView4 = root.findViewById(R.id.textView4);

        //code cua kien
        imageSlider = root.findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://img.freepik.com/free-vector/cute-pets-illustration_53876-112522.jpg?w=2000", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://kienthucbonphuong.com/images/202006/pet-la-gi/pet-la-gi.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://img.freepik.com/premium-photo/group-pets-posing-around-border-collie-dog-cat-ferret-rabbit-bird-fish-rodent_191971-22249.jpg?w=2000", ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

//        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAuth.signOut();
//            }
//        });
//        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
//        if (signInAccount !=null){
//            textView4.setText(signInAccount.getEmail());
//        }
//
//
//        buttonLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//            }
//        });
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {

        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.petHotel_Btn:
                intent= new Intent(getActivity(), BookingActivity.class);
                intent.putExtra("ID_BUTTON", "Homestay");
                startActivity(intent);
                break;
            case R.id.petSpa_Btn:
                intent= new Intent(getActivity(), BookingActivity.class);
                intent.putExtra("ID_BUTTON", "Spa");
                startActivity(intent);
                break;
            case R.id.petHealth_Btn:
                intent= new Intent(getActivity(), BookingActivity.class);
                intent.putExtra("ID_BUTTON", "Hospital");
                startActivity(intent);
                break;
            case R.id.petBurry_Btn:
                intent= new Intent(getActivity(), BookingActivity.class);
                intent.putExtra("ID_BUTTON", "Burial");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
