package tuan.aprotrain.projectpetcare.entity;

public class User {

    private String userId;
    private String userName;
    private String email;
    private String phoneNo;
    private String userUrl;
    //private String password;
    private String userToken;
    private String userRole;
    public static String TABLE_NAME = "Users";
    public User(){}

    public User(String userId, String email){
        this.userId = userId;
        this.email = email;
        //this.password = password;
    }

    public User(String userId, String userName, String email, String phoneNo,
                 String userToken, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.userToken = userToken;
        this.userRole = userRole;
    }

    public User(String userId,String userName, String userUrl, String email, String userRole) {
        this.userUrl = userUrl;
        this.userName = userName;
        this.userId = userId;
        this.email = email;
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
