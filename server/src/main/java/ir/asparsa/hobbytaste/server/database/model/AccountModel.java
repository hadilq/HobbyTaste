package ir.asparsa.hobbytaste.server.database.model;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "accounts")
public class AccountModel implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "role")
    private String role;

    AccountModel() { // jpa only
    }

    public AccountModel(String token) {
        this.username = token;
    }

    public AccountModel(Long id) {
        this.id = id;
    }

    public AccountModel(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if (!(otherObj instanceof AccountModel)) {
            return false;
        }
        final AccountModel other = (AccountModel) otherObj;
        return new EqualsBuilder()
                .append(getUsername(), other.getUsername())
                .append(getId(), other.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getUsername()).append(getId()).toHashCode();
    }

    @Override public String toString() {
        return "AccountModel{" +
               "id=" + id +
               ", password='" + password + '\'' +
               ", username='" + username + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}
