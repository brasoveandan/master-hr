package bdir.masterhr.repository;

import bdir.masterhr.domain.Holiday;
import bdir.masterhr.domain.validators.HolidayValidator;
import bdir.masterhr.domain.validators.Validator;
import bdir.masterhr.utils.HibernateSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class HolidayRepository implements CrudRepository<String, Holiday> {
    SessionFactory sessionFactory;
    HolidayValidator holidayValidator;

    public HolidayRepository() {
        this.sessionFactory = HibernateSession.getSessionFactory();
        this.holidayValidator = new HolidayValidator();
    }

    @Override
    public Holiday save(Holiday entity) throws Validator.ValidationException {
        if (entity == null)
            throw new IllegalArgumentException();
        try {
            holidayValidator.validate(entity);
        } catch (Validator.ValidationException exception) {
            throw new Validator.ValidationException(exception.getMessage());
        }
        if (findOne(entity.getIdHoliday()) != null)
            return entity;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
            return null;
        }
    }

    @Override
    public Holiday delete(String id) {
        if (id == null)
            throw new IllegalArgumentException();
        Holiday holiday = findOne(id);
        if (holiday == null)
            return null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(holiday);
            session.getTransaction().commit();
            return holiday;
        }
    }

    @Override
    public Holiday update(Holiday entity) throws Validator.ValidationException {
        if (entity == null)
            throw new IllegalArgumentException();
        if (findOne(entity.getIdHoliday()) == null)
            return entity;
        try {
            holidayValidator.validate(entity);
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                session.update(entity);
                session.getTransaction().commit();
                return null;
            }
        } catch (Validator.ValidationException exception) {
            throw new Validator.ValidationException(exception.getMessage());
        }
    }

    @Override
    public Holiday findOne(String id) {
        if (id == null)
            throw new IllegalArgumentException();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            List<Holiday> result = session.createQuery("select a from Holiday a where idHoliday=:idHoliday")
                    .setParameter("idHoliday", id)
                    .list();
            session.getTransaction().commit();

            if (result.isEmpty())
                return null;
            else return result.get(0);
        }
    }

    @Override
    public List<Holiday> findAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            List<Holiday> result = session.createQuery("select a from Holiday a").list();
            session.getTransaction().commit();
            return result;
        }
    }
}
