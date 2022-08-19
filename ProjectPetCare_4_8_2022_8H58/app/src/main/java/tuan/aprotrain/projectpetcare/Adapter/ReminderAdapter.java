package tuan.aprotrain.projectpetcare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.activities.AddReminderActivity;
import tuan.aprotrain.projectpetcare.entity.Reminder;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.Viewholder>{
    private Context context;
    private ArrayList<Reminder> reminders;

    // Constructor
    public ReminderAdapter(Context context, ArrayList<Reminder> reminders) {
        this.context = context;
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fragment_reminder, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderAdapter.Viewholder holder, int position) {
        holder.tv_reminder_name.setText(reminders.get(position).getName());
        Calendar calendarGetTime = reminders.get(position).getCalendar();
        int hour = calendarGetTime.get(Calendar.HOUR_OF_DAY);
        int minute  = calendarGetTime.get((Calendar.MINUTE));
        holder.tv_med_time.setText(hour+ " : " + minute);
//        holder.tv_med_time.setText(reminders.get(position).getCalendar().HOUR_OF_DAY + " : " + reminders.get(position).getCalendar().MINUTE);
        holder.iv_icon.setImageResource(R.drawable.icon_blister);

        if (reminders.get(position).getDayOfWeek().get(1) == true) {
            holder.tv_monday.setText("Mo");
            holder.tv_monday.setTextColor(Color.parseColor("#c217a5"));
        } else {
            holder.tv_monday.setText("Mo");
            holder.tv_monday.setTextColor(Color.parseColor("#000000"));
        }

        if (reminders.get(position).getDayOfWeek().get(2) == true) {
            holder.tv_tuesday.setText("Tu");
            holder.tv_tuesday.setTextColor(Color.parseColor("#c217a5"));
        } else {
            holder.tv_tuesday.setText("Tu");
            holder.tv_tuesday.setTextColor(Color.parseColor("#000000"));
        }

        if (reminders.get(position).getDayOfWeek().get(3) == true) {
            holder.tv_wednesday.setText("We");
            holder.tv_wednesday.setTextColor(Color.parseColor("#c217a5"));
        } else {
            holder.tv_wednesday.setText("We");
            holder.tv_wednesday.setTextColor(Color.parseColor("#000000"));
        }
        if (reminders.get(position).getDayOfWeek().get(4) == true) {
            holder.tv_thursday.setText("Th");
            holder.tv_thursday.setTextColor(Color.parseColor("#c217a5"));
        } else {
            holder.tv_thursday.setText("Th");
            holder.tv_thursday.setTextColor(Color.parseColor("#000000"));
        }
        if (reminders.get(position).getDayOfWeek().get(5) == true) {
            holder.tv_friday.setText("Fr");
            holder.tv_friday.setTextColor(Color.parseColor("#c217a5"));
        } else {
            holder.tv_friday.setText("Fr");
            holder.tv_friday.setTextColor(Color.parseColor("#000000"));
        }
        if (reminders.get(position).getDayOfWeek().get(6) == true) {
            holder.tv_saturday.setText("Sa");
            holder.tv_saturday.setTextColor(Color.parseColor("#c217a5"));
        } else {
            holder.tv_saturday.setText("Sa");
            holder.tv_saturday.setTextColor(Color.parseColor("#000000"));
        }
        if (reminders.get(position).getDayOfWeek().get(0) == true) {
            holder.tv_sunday.setText("Su");
            holder.tv_sunday.setTextColor(Color.parseColor("#c217a5"));
        } else {
            holder.tv_sunday.setText("Su");
            holder.tv_sunday.setTextColor(Color.parseColor("#000000"));
        }
        if(reminders.get(position).isTurnedOn() == true){
            holder.iv_take_med.setImageResource(R.drawable.on_icon);
        }else{
            holder.iv_take_med.setImageResource(R.drawable.off_icon);
        }
//       holder.tv_med_time.setText(reminders.get(position).getCalendar().HOUR_OF_DAY + " : " + reminders.get(position).getCalendar().MINUTE);
        holder.iv_icon.setImageResource(R.drawable.icon_blister);
        holder.iv_ignore_med.setImageResource(R.drawable.x_icon);
        holder.iv_edit_med.setImageResource(R.drawable.edit);
//        holder.iv_take_med.setImageResource(R.drawable.on_icon);


//            }
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return reminders == null ? 0 : reminders.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView iv_icon, iv_ignore_med, iv_edit_med, iv_take_med;
        private TextView tv_med_time, tv_reminder_name, tv_sunday, tv_monday, tv_tuesday, tv_wednesday, tv_thursday, tv_friday, tv_saturday;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            iv_icon = itemView.findViewById(R.id.iv_icon);
            iv_ignore_med = itemView.findViewById(R.id.iv_ignore_med);
            iv_edit_med = itemView.findViewById(R.id.iv_edit_med);
            iv_take_med = itemView.findViewById(R.id.iv_take_med);
            tv_med_time = itemView.findViewById(R.id.tv_med_time);
            tv_reminder_name = itemView.findViewById(R.id.tv_reminder_name);

            tv_sunday = itemView.findViewById(R.id.tv_sunday);
            tv_monday = itemView.findViewById(R.id.tv_monday);
            tv_tuesday = itemView.findViewById(R.id.tv_tuesday);
            tv_wednesday = itemView.findViewById(R.id.tv_wednesday);
            tv_thursday = itemView.findViewById(R.id.tv_thursday);
            tv_friday = itemView.findViewById(R.id.tv_friday);
            tv_saturday = itemView.findViewById(R.id.tv_saturday);

            iv_ignore_med.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("DATA", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    reminders.remove(position);
                    String json = gson.toJson(reminders);
                    editor.putString("reminder_data", json);
                    editor.apply();
                    notifyDataSetChanged();
                }
            });

            iv_take_med.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentTag = iv_take_med.getTag() == null ? 0 :
                            (int)iv_take_med.getTag();
                    currentTag++;
                    iv_take_med.setTag(currentTag);
                    if(currentTag % 2 == 0){
                        iv_take_med.setImageResource(R.drawable.on_icon);
                    }else{
                        iv_take_med.setImageResource(R.drawable.off_icon);
                    }

                }
            });

            iv_edit_med.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent myIntent = new Intent(itemView.getContext(), AddReminderActivity.class);
                    myIntent.putExtra("keyPositionEdit",position);
                    itemView.getContext().startActivity(myIntent);
                }
            });
        }
    }
}
