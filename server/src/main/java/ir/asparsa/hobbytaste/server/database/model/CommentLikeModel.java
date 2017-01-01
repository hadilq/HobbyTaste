package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.CommentLikeColumns;

import javax.persistence.*;

/**
 * Created by hadi on 12/30/2016 AD.
 */
public class CommentLikeModel {

    @Id
    @GeneratedValue
    @Column(name = CommentLikeColumns.ID)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = CommentLikeColumns.COMMENT)
    private CommentModel comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = CommentLikeColumns.ACCOUNT)
    private AccountModel account;

    public CommentLikeModel() {
    }

    public CommentLikeModel(
            CommentModel comment,
            AccountModel account
    ) {
        this.comment = comment;
        this.account = account;
    }

    public AccountModel getAccount() {
        return account;
    }

    public CommentModel getComment() {
        return comment;
    }
}
