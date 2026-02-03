package pl.regizz.saasapi.domain.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private Instant createdAt = Instant.now();

    public User(String email) {
        this.email = email;
    }

    protected User() {}

    public long getId() {
        return id;
    }
}
