package billing.demo.rds;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import java.util.List; 
import java.util.Iterator; 

public class Reader implements RequestHandler<Request, String> {

    @Override
    public String handleRequest(Request request, Context context) {
        Transaction tx = null;
        int agrCount = 0;
        String metric = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            List usersList = session.createQuery("FROM Users").list();
            for (Iterator iterator = usersList.iterator(); iterator.hasNext();){
                Users users = (Users)iterator.next();
                if (metric == null) {
                    metric = users.getMetric();
                }
                if((users.getUserId()).equals(request.userId)){
                    agrCount += users.getCount();
                }
            }
            tx.commit();
        }

        return String.format("{\"userId\" : \"%s\", \"metric\" : \"%s\", \"count\" : %d}", request.userId, metric, agrCount);
    }
}
