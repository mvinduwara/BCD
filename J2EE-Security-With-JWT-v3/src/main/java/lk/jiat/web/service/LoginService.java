package lk.jiat.web.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lk.jiat.web.entity.User;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RequestScoped
public class LoginService {

    @PersistenceContext(unitName = "JIAT-Security")
    private EntityManager em;

    public Optional<User> findByEmail(String email) {
        try {
            return Optional.of(em.createNamedQuery("User.findByEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean validate(String email, String password) {
        return findByEmail(email)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    public Set<String> getRoles(String email) {
        return findByEmail(email)
                .map(User::getRoles).orElse(Collections.emptySet());
    }

}
