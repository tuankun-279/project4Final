package tuan.aprotrain.projectpetcare.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Booking;
import tuan.aprotrain.projectpetcare.entity.Category;
import tuan.aprotrain.projectpetcare.entity.Pet;

public class BookingHistoryAdapter extends ArrayAdapter<Booking> {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();



    public BookingHistoryAdapter(Context context, List<Booking> bookingArrayList) {
        super(context, R.layout.booking_histories_item, R.id.tv_bookingId, bookingArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Booking booking = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.booking_histories_item, parent, false);
        }
        ImageView imageQRBooking = convertView.findViewById(R.id.imageQRBooking);
        TextView tv_category = convertView.findViewById(R.id.tv_category);
        TextView tv_bookingId = convertView.findViewById(R.id.tv_bookingId);
        TextView tv_interval = convertView.findViewById(R.id.tv_duration);
        TextView tv_bookingPrice = convertView.findViewById(R.id.tv_bookingPrice);
        TextView tv_serviceCount = convertView.findViewById(R.id.tv_serviceCount);

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(booking.getBookingId(), BarcodeFormat.QR_CODE, 80, 80);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            imageQRBooking.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Pets").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(pets -> {
                    if (pets.getValue(Pet.class).getPetId() == booking.getPetId()) {
                        String petName = pets.getValue(Pet.class).getPetName();
                        reference.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getChildren().forEach(categories -> {
                                    if (categories.getValue(Category.class).getCategoryId() == booking.getSelectedService().get(0).getCategoryId()) {
                                        String serviceName = categories.getValue(Category.class).getCategoryName();
                                        tv_category.setText(serviceName + " for " + petName);
                                    }
                                });
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

        tv_bookingId.setText(booking.getBookingId());
        tv_interval.setText(booking.getBookingStartDate() + " \n" + booking.getBookingEndDate());
        tv_bookingPrice.setText(booking.getTotalPrice() + "$");
        tv_serviceCount.setText(booking.getSelectedService().size() + "services");
        return super.getView(position, convertView, parent);
    }


}
