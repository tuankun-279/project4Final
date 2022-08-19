package tuan.aprotrain.projectpetcare.Fragment.booking_history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import tuan.aprotrain.projectpetcare.Adapter.BookingHistoryAdapter;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.databinding.FragmentBookingHistoryBinding;
import tuan.aprotrain.projectpetcare.entity.Booking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tuan.aprotrain.projectpetcare.Adapter.BookingHistoryAdapter;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Booking;
import tuan.aprotrain.projectpetcare.entity.Pet;

public class BookingHistoryFragment extends Fragment {
    ListView listViewBooking;
    List<Booking> bookingList = new ArrayList<Booking>();
    private BookingHistoryAdapter bookingHistoryAdapter;

    private FragmentBookingHistoryBinding binding;

    public static BookingHistoryFragment newInstance() {
        return new BookingHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewBooking = root.findViewById(R.id.listViewBooking);
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);

        getData();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });


        bookingHistoryAdapter =
                new BookingHistoryAdapter(getActivity(), bookingList);
        int y = 0;
//        getBooking().forEach(booking -> {
//            System.out.println("Booking: " + getBooking());
//        });
        listViewBooking.setAdapter(bookingHistoryAdapter);
        listViewBooking.setClickable(true);
        return root;
    }
    public void getData(){
        //bookingList.clear();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Pets").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot petSnapshot) {
                petSnapshot.getChildren().forEach(pets -> {

                    Pet pet = pets.getValue(Pet.class);
                    if (pet.getUserId().equals(user.getUid())) {
                        long petId = pet.getPetId();
                        reference.child("Bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot bookingSnapshot) {
                                try {
                                    bookingList.clear();
                                    bookingSnapshot.getChildren().forEach(bookings -> {
                                        if (bookings.getValue(Booking.class).getPetId() == petId) {
                                            bookingList.add(bookings.getValue(Booking.class));
                                        }
                                    });
                                    bookingHistoryAdapter.notifyDataSetChanged();
                                } catch (NumberFormatException e) {
                                    System.out.println("error: " + e.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

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
