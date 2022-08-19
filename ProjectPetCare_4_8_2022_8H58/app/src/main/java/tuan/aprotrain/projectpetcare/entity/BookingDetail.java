package tuan.aprotrain.projectpetcare.entity;

public class BookingDetail {
    private String bookingId;
    private long serviceId;

    public BookingDetail(){}

    public BookingDetail(String bookingId, long serviceId) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }
}
