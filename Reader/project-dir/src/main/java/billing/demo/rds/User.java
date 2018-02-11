package billing.demo.rds;

public class User{
    private String userId;
    private String metric;
    private int count;


    public User() {
    }

    public User(String userId, String metric, int count) {
        this.userId = userId;
        this.metric = metric;
	    this.count = count;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Users{" + 
                "userId=" + userId + 
                ", metric=" + metric + 
                ", count=" + count + 
                '}';
    }
}
