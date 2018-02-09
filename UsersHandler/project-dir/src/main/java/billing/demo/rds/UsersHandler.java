package billing.demo.rds;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UsersHandler implements RequestHandler<Request, String> {

    @Override
    public String handleRequest(Request request, Context context) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Users users = new Users();
            users.setUserId(request.userId);
            users.setMetric(request.metric);
	    users.setCount(request.count);
            session.save(users);
            session.getTransaction().commit();
        }

        return String.format("Added %s %s %d.", request.userId, request.metric, request.count);
    }
}
