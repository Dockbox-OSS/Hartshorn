package test.org.dockbox.hartshorn.jpa;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

@Entity(name = "queryUsers")
@NamedQuery(name = "UserWithNamedQuery.findWaldo", query = "SELECT u FROM queryUsers u WHERE u.name = 'Waldo'")
public class UserWithNamedQuery {

    @Id
    @GeneratedValue
    private long id;
    private String name;

    public UserWithNamedQuery() {
    }

    public UserWithNamedQuery(final String name) {
        this.name = name;
    }

    public long id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public UserWithNamedQuery name(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final UserWithNamedQuery that)) return false;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
