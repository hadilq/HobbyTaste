package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.CommentLike;

import javax.persistence.*;

/**
 * Created by hadi on 12/30/2016 AD.
 */
@Entity
@Table(name = CommentLike.TABLE_NAME)
public class CommentLikeModel {

    @Id
    @GeneratedValue
    @Column(name = CommentLike.Columns.ID)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = CommentLike.Columns.COMMENT)
    private CommentModel comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = CommentLike.Columns.ACCOUNT)
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
