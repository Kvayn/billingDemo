package billing.demo.rds;
public class Request {
    String userId;


    public Request() {
    }

    public Request(String userId) {
        this.userId = userId;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    

    @Override
    public String toString() {
        return "Request{" + "userId=" + userId + '}';
    }
}

