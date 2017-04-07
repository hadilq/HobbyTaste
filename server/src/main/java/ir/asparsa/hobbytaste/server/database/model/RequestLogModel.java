package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.RequestLog;

import javax.persistence.*;

/**
 * @author hadi
 * @since 3/10/2017 AD.
 */
@Entity
@Table(name = RequestLog.TABLE_NAME)
public class RequestLogModel {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = RequestLog.Columns.DATETIME)
    private long datetime;
    @Column(name = RequestLog.Columns.ADDRESS)
    private String address;
    @Column(name = RequestLog.Columns.URI)
    private String uri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = RequestLog.Columns.ACCOUNT)
    private AccountModel account;

    RequestLogModel() { // jpa only
    }

    public RequestLogModel(
            long datetime,
            String address,
            String uri,
            AccountModel account
    ) {
        this.datetime = datetime;
        this.address = address;
        this.uri = uri;
        this.account = account;
    }

    public long getDatetime() {
        return datetime;
    }

    public String getAddress() {
        return address;
    }

    public String getUri() {
        return uri;
    }

    public AccountModel getAccount() {
        return account;
    }
}
