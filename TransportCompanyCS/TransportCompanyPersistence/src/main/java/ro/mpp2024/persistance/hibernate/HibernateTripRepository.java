package ro.mpp2024.persistance.hibernate;

<<<<<<< HEAD
=======
import org.hibernate.Session;
>>>>>>> origin/main
import ro.mpp2024.model.Trip;
import ro.mpp2024.persistance.TripRepository;

import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.List;
=======
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
>>>>>>> origin/main

public class HibernateTripRepository implements TripRepository {
    @Override
    public List<Trip> getTripsByDestinationAndDepartureTime(String destination, LocalDateTime departureTime) {
<<<<<<< HEAD
        return List.of();
=======
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Trip where destination=:destination and  date(departureTime)=:departureTime", Trip.class)
                    .setParameter("destination", destination)
                    .setParameter("departureTime", departureTime)
                    .getResultList();
        }
>>>>>>> origin/main
    }

    @Override
    public void add(Trip elem) {
<<<<<<< HEAD

=======
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(elem));
>>>>>>> origin/main
    }

    @Override
    public void delete(Trip elem) {
<<<<<<< HEAD

=======
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
>>>>>>> origin/main
    }

    @Override
    public void update(Trip elem, Long id) {
<<<<<<< HEAD

=======
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (!Objects.isNull(session.find(Trip.class, id))) {
                System.out.println("In update, am gasit trip-ul cu id-ul " + id);
                session.merge(elem);
                session.flush();
            }
        });
>>>>>>> origin/main
    }

    @Override
    public Trip findById(Long id) {
<<<<<<< HEAD
        return null;
=======
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from Trip where id=:id", Trip.class)
                    .setParameter("id", id)
                    .getSingleResultOrNull();
        }
>>>>>>> origin/main
    }

    @Override
    public Iterable<Trip> findAll() {
<<<<<<< HEAD
        return null;
=======
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return new HashSet<>(session.createSelectionQuery("from Trip", Trip.class).getResultList());
        }
>>>>>>> origin/main
    }
}
