package tuan.aprotrain.projectpetcare.entity;

public class Service {

    private long serviceId;
    private String serviceName;
    private long categoryId;
    private Float servicePrice;//sua thanh servicePrice
    private long serviceTime;//Them serviceTime
    public static String TABLE_NAME = "Services";//them table name

    public Service(){}

    public Service(long serviceId, String serviceName, Long categoryId, Float servicePrice, long serviceTime) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.categoryId = categoryId;
        this.servicePrice = servicePrice;
        this.serviceTime = serviceTime;
    }
    //factory methods
//    public static Service fromHashMap(HashMap<String, Object> hashMap) {
//        return new Service(
//                (Long)hashMap.get("serviceId"),
//                (String)hashMap.get("serviceName"),
//                String.valueOf(hashMap.get("categoryId")),
//                (Float)hashMap.get("servicePrice"),
//                (Long)hashMap.get("serviceTime")
//                );
//    }
    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public Float getServicePrice() {return servicePrice;}

    public void setServicePrice(Float servicePrice) {this.servicePrice = servicePrice;}

    public long getServiceTime() {return serviceTime;}

    public void setServiceTime(long serviceTime) {this.serviceTime = serviceTime;}
}
