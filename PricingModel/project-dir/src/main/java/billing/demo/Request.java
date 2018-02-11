package billing.demo;

import java.util.Date;

public class Request {
    String userId;
    Date from;
    Date to;


    public Request() {
    }

    public Request(String userId, Date from, Date to) {
        this.userId = userId;
        this.from = from;
        this.to = to;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }
    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Request{" + 
                "userId=" + userId + 
                ", from=" + from + 
                ", to=" + to +
                '}';
    }
}

