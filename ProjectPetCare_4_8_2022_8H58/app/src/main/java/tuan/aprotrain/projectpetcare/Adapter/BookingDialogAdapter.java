package tuan.aprotrain.projectpetcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Service;

public class BookingDialogAdapter extends ArrayAdapter<Service> {
    Context context;
    ArrayList<Service> serviceArrayList;
    LayoutInflater inflater;

    public BookingDialogAdapter(Context context, ArrayList<Service> serviceArrayList){
        super(context,R.layout.booking_services_item,R.id.idDialog,serviceArrayList);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Service service = (Service) getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.booking_services_item,parent,false);
        }

        TextView  selectedServiceName = convertView.findViewById(R.id.selectedServiceName);
        TextView  selectedServicePrice = convertView.findViewById(R.id.selectedServicePrice);
        TextView  selectedServiceTime = convertView.findViewById(R.id.selectedServiceTime);
        TextView  idDialog = convertView.findViewById(R.id.idDialog);

        idDialog.setText("hello");
        selectedServiceName.setText(service.getServiceName());
        selectedServicePrice.setText(service.getServicePrice()+"$");
        selectedServiceTime.setText(service.getServiceTime()+"mins");

        return super.getView(position, convertView, parent);
    }
}
