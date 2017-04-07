package ir.asparsa.hobbytaste.server.database.model;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.asparsa.common.database.model.Account;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = Account.TABLE_NAME)
public class AccountModel implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @JsonIgnore
    @Column(name = Account.Columns.PASSWORD)
    private String password;

    @Column(name = Account.Columns.USERNAME)
    private String username;

    @Column(name = Account.Columns.HASH)
    private String hash;

    @Column(name = Account.Columns.CREATED)
    private Long created;

    @Column(name = Account.Columns.ROLE)
    private String role;

    AccountModel() { // jpa only
    }

    public AccountModel(
            String username,
            String hash,
            String role
    ) {
        this.username = username;
        this.hash = hash;
        this.created = System.currentTimeMillis();
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getHash() {
        return hash;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCreated() {
        return created;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if (!(otherObj instanceof AccountModel)) {
            return false;
        }
        final AccountModel other = (AccountModel) otherObj;
        return ((getUsername() == null && other.getUsername() == null) ||
                (getUsername() != null && getUsername().equals(other.getUsername()))) &&
               ((getHash() == null && other.getHash() == null) ||
                (getHash() != null && getHash().equals(other.getHash()))) &&
               (getId() == getId());
    }

    @Override
    public int hashCode() {
        return (getUsername() == null ? 0 : getUsername().hashCode()) ^ (int) ((getId() ^ getId() >> 31));
    }

    @Override public String toString() {
        return "AccountModel{" +
               "id=" + id +
               ", password='" + password + '\'' +
               ", username='" + username + '\'' +
               ", hash='" + hash + '\'' +
               ", created=" + created +
               ", role='" + role + '\'' +
               '}';
    }
}
