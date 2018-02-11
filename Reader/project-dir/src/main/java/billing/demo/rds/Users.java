package billing.demo.rds;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Entity
public class Users implements java.io.Serializable {
    private String userId;
    private String metric;
    private int count;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    public Users() {
    }

    public Users(String userId, String metric, int count, Date dateTime) {
        this.userId = userId;
        this.metric = metric;
        this.count = count;
        this.dateTime = dateTime; 
    }
    @Id 
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
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Users{" + 
                "userId=" + userId + 
                ", metric=" + metric + 
                ", count=" + count + 
                ", dateTime=" + dateTime +
                '}';
    }
}
