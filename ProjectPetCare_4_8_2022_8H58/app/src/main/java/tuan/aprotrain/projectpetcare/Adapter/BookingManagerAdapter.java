package tuan.aprotrain.projectpetcare.Adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Booking;

public class BookingManagerAdapter extends ArrayAdapter {
    private Activity Context;
    List<Booking> usersList;

    public BookingManagerAdapter(Activity Context, List<Booking> usersList){
        super(Context, R.layout.list_service, usersList);
        this.Context=Context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater=Context.getLayoutInflater();
        View listItemView=inflater.inflate(R.layout.list_booking,null,true);

        TextView id = listItemView.findViewById(R.id.bookingId);
        TextView address = listItemView.findViewById(R.id.bookingAddress);
        TextView price = listItemView.findViewById(R.id.totalPrice);
        TextView payment = listItemView.findViewById(R.id.payment);
        TextView petId = listItemView.findViewById(R.id.petId);
        //ImageView img=listItemView.findViewById(R.id.img1);

        Booking booking = usersList.get(position);
        id.setText(booking.getBookingId());
        address.setText(booking.getBookingAddress());
        price.setText(booking.getTotalPrice()+"$");
        payment.setText(booking.getPayment());
        petId.setText(String.valueOf(booking.getPetId()));

        return listItemView;
    }
}
