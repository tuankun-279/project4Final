package tuan.aprotrain.projectpetcare.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import tuan.aprotrain.projectpetcare.Adapter.ReminderAdapter;
import tuan.aprotrain.projectpetcare.AlarmReceiver;
import tuan.aprotrain.projectpetcare.AlarmService;
import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.Reminder;

public class ReminderActivity extends AppCompatActivity {
    private static final int NOTIFICATION_ID = 1;
    Bundle extras;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;

    RecyclerView id_reminder_recycleview;
    ReminderAdapter reminderAdapter;
    ImageButton btn_add_activity;
    // Arraylist for storing data
    private ArrayList<Reminder> reminders;
    private ArrayList<Calendar> calendars;
//    private ArrayList<CalendarClass> calendarClassList;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    // Define ActionBar object
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        toolbar = findViewById(R.id.toolbar_uploading_pet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(new Intent(this, AlarmService.class));
        } else {
            this.startService(new Intent(this, AlarmService.class));
        }
        startService(new Intent(getApplicationContext(), AlarmService.class));
        createNotificationChannel();

        id_reminder_recycleview = findViewById(R.id.id_reminder_recycleview);
        btn_add_activity = findViewById(R.id.btn_add_activity);
        btn_add_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ReminderActivity.this, AddReminderActivity.class);
                ReminderActivity.this.startActivity(myIntent);
            }
        });

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("DATA", 0);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("reminder_data", null);
        Type type = new TypeToken<ArrayList<Reminder>>() {
        }.getType();
        reminders = gson.fromJson(json, type);
        if (reminders == null) {
            reminders = new ArrayList<>();
        } else {
            // we are initializing our adapter class and passing our arraylist to it.
            reminderAdapter = new ReminderAdapter(this, reminders);

        }

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        id_reminder_recycleview.setLayoutManager(linearLayoutManager);
        id_reminder_recycleview.setAdapter(reminderAdapter);

        if (extras == null) {
            extras = getIntent().getExtras();
            setAlarm();
        }

        if (extras != null) {

            String value = extras.getString("key");
            if (value != null) {
                setAlarm();
            }
            //The key argument here must match that used in the other activity
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Check";
            String description = "Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }


    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                setAlarm();
            }
        }, delay);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }

    private void setAlarm() {
        calendars = new ArrayList<>();

        //creating Calendar instance
        String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar calendarCurrent = Calendar.getInstance();

        int year = calendarCurrent.get(Calendar.YEAR);
        int month = calendarCurrent.get(Calendar.MONTH);
        int day = calendarCurrent.get(Calendar.DAY_OF_MONTH);

        String daysNameCurrent = days[calendarCurrent.get(Calendar.DAY_OF_WEEK) - 1];

        for (Reminder reminderCalendarItem : reminders) {
            if (reminderCalendarItem.getCalendar() != calendarCurrent) {
                reminderCalendarItem.getCalendar().set(year, month, day);
            }
        }

        for (Reminder reminderItem : reminders) {

            String daysName = days[reminderItem.getCalendar().get(Calendar.DAY_OF_WEEK) - 1];
            if (reminderItem.getDayOfWeek().get(0) == false
                    && reminderItem.getDayOfWeek().get(1) == false
                    && reminderItem.getDayOfWeek().get(2) == false
                    && reminderItem.getDayOfWeek().get(3) == false
                    && reminderItem.getDayOfWeek().get(4) == false
                    && reminderItem.getDayOfWeek().get(5) == false
                    && reminderItem.getDayOfWeek().get(6) == false
                    && reminderItem.isTurnedOn() == true) {
                if (reminderItem.getCalendar().after(calendarCurrent)) {
                    reminderItem.getCalendar().set(year, month, day);
                    calendars.add(reminderItem.getCalendar());

                }
            } else if (daysNameCurrent == "Friday") {
                if (reminderItem.getDayOfWeek().get(5) == true && reminderItem.isTurnedOn() == true) {
                    if (reminderItem.getCalendar().after(calendarCurrent)) {
                        reminderItem.getCalendar().set(year, month, day);
                        calendars.add(reminderItem.getCalendar());

                    }
                }
            } else if (daysNameCurrent == "Saturday") {
                if (reminderItem.getDayOfWeek().get(6) == true && reminderItem.isTurnedOn() == true) {
                    if (reminderItem.getCalendar().after(calendarCurrent)) {
                        reminderItem.getCalendar().set(year, month, day);
                        calendars.add(reminderItem.getCalendar());

                    }
                }
            } else if (daysNameCurrent == "Sunday") {
                if (reminderItem.getDayOfWeek().get(0) == true && reminderItem.isTurnedOn() == true) {
                    if (reminderItem.getCalendar().after(calendarCurrent)) {
                        reminderItem.getCalendar().set(year, month, day);
                        calendars.add(reminderItem.getCalendar());

                    }
                }
            } else if (daysNameCurrent == "Monday") {
                if (reminderItem.getDayOfWeek().get(1) == true && reminderItem.isTurnedOn() == true) {
                    if (reminderItem.getCalendar().after(calendarCurrent)) {
                        reminderItem.getCalendar().set(year, month, day);
                        calendars.add(reminderItem.getCalendar());

                    }
                }
            } else if (daysNameCurrent == "Tuesday") {
                if (reminderItem.getDayOfWeek().get(2) == true && reminderItem.isTurnedOn() == true) {
                    if (reminderItem.getCalendar().after(calendarCurrent)) {
                        reminderItem.getCalendar().set(year, month, day);
                        calendars.add(reminderItem.getCalendar());

                    }
                }
            } else if (daysNameCurrent == "Wednesday") {
                if (reminderItem.getDayOfWeek().get(3) == true && reminderItem.isTurnedOn() == true) {
                    if (reminderItem.getCalendar().after(calendarCurrent)) {
                        reminderItem.getCalendar().set(year, month, day);
                        calendars.add(reminderItem.getCalendar());

                    }
                }
            } else if (daysNameCurrent == "Thursday") {
                if (reminderItem.getDayOfWeek().get(4) == true && reminderItem.isTurnedOn() == true) {
                    if (reminderItem.getCalendar().after(calendarCurrent)) {
                        reminderItem.getCalendar().set(year, month, day);
                        calendars.add(reminderItem.getCalendar());


                    }
                }
            }
        }
        if (calendars.size() > 0) {
            ArrayList<String> remindersName = new ArrayList<>();
            Collections.sort(calendars);

            for (Reminder reminder : reminders) {
                if (calendars.get(0).equals(reminder.getCalendar())) {
                    remindersName.add(reminder.getName());
                }
            }



            // Intent
            Intent intent = new Intent(this, AlarmReceiver.class);

            intent.putExtra("message", remindersName.get(0));

            String time = calendars.get(0).get(Calendar.HOUR_OF_DAY) + " : " + calendars.get(0).get(Calendar.MINUTE);
            intent.putExtra("title", time);

            Calendar currentTime = Calendar.getInstance();

            int hourCalendar = calendars.get(0).get(Calendar.HOUR_OF_DAY);
            int hourCurrentTime = currentTime.get(Calendar.HOUR_OF_DAY);

            int minuteCalendar = calendars.get(0).get(Calendar.MINUTE);
            int minuteCurrentTime = currentTime.get(Calendar.MINUTE);

            int secondCalendar = calendars.get(0).get(Calendar.SECOND);
            int secondCurrentTime = currentTime.get(Calendar.SECOND);

            String nameReminder = remindersName.get(0);

//            if ((hourCalendar == hourCurrentTime)) {
//                if (minuteCalendar == minuteCurrentTime) {
//                    if (secondCalendar == secondCurrentTime) {
//                        openRequestDialog(nameReminder, time, this);
//                        notification(nameReminder, time);
//                        Log.d("TAG", "notification");
//                    }
//
//                }
//            }
            boolean isNotificationEnabled = false;
            if ((hourCalendar == hourCurrentTime)) {
                if (minuteCalendar == minuteCurrentTime) {
                    isNotificationEnabled = true;
                }
            }
            if(isNotificationEnabled == true){
                openRequestDialog(nameReminder, time, this);
                notification(nameReminder, time);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "notification");
            }
        }
    }

    private void notification(String time, String title) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, AlarmReceiver.CHANNEL_ID)
                    .setContentTitle("It's: " + title)
                    .setContentText(time)
                    .setSmallIcon(R.drawable.on_icon)
                    .setLargeIcon(bitmap)
                    .setColor(getResources().getColor(R.color.green))
                    .build();
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(getNotificationId(), notification);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if(notificationManager != null){
//            notificationManager.notify(getNotificationId(), notification);
//        }
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    private void openRequestDialog(String nameReminder, String time, Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_dialog_on_time);

        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);


//        String time,String nameReminder
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.layout_dialog_on_time);
//
//        Window window = dialog.getWindow();
//        if(window == null){
//            return;
//        }
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        WindowManager.LayoutParams windowAttributes = window.getAttributes();
//        window.setAttributes(windowAttributes);

        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvDescription = dialog.findViewById(R.id.tv_description);
        Button btnSend = dialog.findViewById(R.id.btn_send);

        tvTitle.setText("It's: " + time);
        tvDescription.setText(nameReminder);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}

