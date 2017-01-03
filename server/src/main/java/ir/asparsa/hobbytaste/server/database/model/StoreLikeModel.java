package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.StoreLike;

import javax.persistence.*;

/**
 * Created by hadi on 12/30/2016 AD.
 */
@Entity
@Table(name = StoreLike.TABLE_NAME)
public class StoreLikeModel {

    @Id
    @GeneratedValue
    @Column(name = StoreLike.Columns.ID)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = StoreLike.Columns.STORE)
    private StoreModel store;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = StoreLike.Columns.ACCOUNT)
    private AccountModel account;

    public StoreLikeModel() {
    }

    public StoreLikeModel(
            StoreModel store,
            AccountModel account
    ) {
        this.store = store;
        this.account = account;
    }

    public AccountModel getAccount() {
        return account;
    }

    public StoreModel getStore() {
        return store;
    }
}
