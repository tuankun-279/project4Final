package tuan.aprotrain.projectpetcare.entity;

import java.util.ArrayList;
import java.util.Calendar;

public class Reminder {
    String name;
    private ArrayList<Boolean> dayOfWeek;
    Calendar calendar;
    boolean isTurnedOn;

    public Reminder() {
    }


    public Reminder(String name, ArrayList<Boolean> dayOfWeek, Calendar calendar, boolean isTurnedOn) {
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.calendar = calendar;
        this.isTurnedOn = isTurnedOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Boolean> getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(ArrayList<Boolean> dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public boolean isTurnedOn() {
        return isTurnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        isTurnedOn = turnedOn;
    }
}
