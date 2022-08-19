package tuan.aprotrain.projectpetcare.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Booking implements Serializable {
    private String bookingId;
    private String bookingStartDate;//them Start Date
    private String bookingEndDate;
    private String bookingAddress;
    private String notes;
    private float totalPrice;//them total price
    private String payment;
    private long petId;
    private ArrayList<Service> selectedService;

    public static String TABLE_NAME = "Bookings";



    public Booking(String bookingId, String bookingStartDate,
                   String bookingEndDate, String bookingAddress,
                   String notes, float totalPrice, String payment, long petId,ArrayList<Service> selectedService) {
        this.bookingId = bookingId;
        this.bookingStartDate = bookingStartDate;
        this.bookingEndDate = bookingEndDate;
        this.bookingAddress = bookingAddress;
        this.notes = notes;
        this.totalPrice = totalPrice;
        this.payment = payment;
        this.petId = petId;
        this.selectedService = selectedService;
    }

//    public Booking(String bookingId,long petId, String bookingStartDate, String bookingEndDate, String payment, String notes,float totalPrice,ArrayList<Service> selectedService) {
//        this.bookingId = bookingId;
//        this.petId = petId;
//        this.bookingStartDate = bookingStartDate;
//        this.bookingEndDate = bookingEndDate;
//        this.payment = payment;
//        this.notes = notes;
//        this.totalPrice = totalPrice;
//        this.selectedService =selectedService;
//    }

    public Booking(long petId, String payment, String note) {
        this.petId = petId;
        this.payment = payment;
        this.notes = note;
    }

    public Booking() {

    }


    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(String bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
    }

    public String getBookingAddress() {
        return bookingAddress;
    }

    public void setBookingAddress(String bookingAddress) {
        this.bookingAddress = bookingAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public long getPetId() {
        return petId;
    }

    public void setPetId(long petId) {
        this.petId = petId;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static void setTableName(String tableName) {
        TABLE_NAME = tableName;
    }

    public String getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(String bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public ArrayList<Service> getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(ArrayList<Service> selectedService) {
        this.selectedService = selectedService;
    }


}
