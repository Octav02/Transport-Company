package ro.mpp2024;


import org.hibernate.Session;
import org.springframework.stereotype.Component;
import ro.mpp2024.model.Trip;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
public class HibernateTripRepository implements TripRepository {
    @Override
    public List<Trip> getTripsByDestinationAndDepartureTime(String destination, LocalDateTime departureTime) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Trip where destination=:destination and  date(departureTime)=:departureTime", Trip.class)
                    .setParameter("destination", destination)
                    .setParameter("departureTime", departureTime)
                    .getResultList();
        }
    }

    @Override
    public void add(Trip elem) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(elem));
        System.out.println(elem.getId());
    }

    @Override
    public void delete(Trip elem) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Trip trip = session.createQuery("from Trip where id=:id", Trip.class)
                    .setParameter("id", elem.getId())
                    .uniqueResult();
            System.out.println("In delete, am gasit trip-ul cu id-ul " + elem.getId());
            if (trip != null) {
                session.remove(trip);
                session.flush();
            }
        });
    }

    @Override
    public void update(Trip elem, Long id) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (!Objects.isNull(session.find(Trip.class, id))) {
                System.out.println("In update, am gasit trip-ul cu id-ul " + id);
                elem.setId(id);
                session.merge(elem);
                session.flush();
            }
        });
    }

    @Override
    public Trip findById(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from Trip where id=:id", Trip.class)
                    .setParameter("id", id)
                    .getSingleResultOrNull();
        }
    }

    @Override
    public Iterable<Trip> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return new HashSet<>(session.createSelectionQuery("from Trip", Trip.class).getResultList());
        }
    }
}
