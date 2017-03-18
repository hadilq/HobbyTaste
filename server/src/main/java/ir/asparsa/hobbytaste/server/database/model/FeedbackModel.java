package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.Feedback;

import javax.persistence.*;

/**
 * @author hadi
 * @since 3/10/2017 AD.
 */
@Entity
@Table(name = Feedback.TABLE_NAME)
public class FeedbackModel {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = Feedback.Columns.DATETIME)
    private long datetime;
    @Column(name = Feedback.Columns.BODY)
    private String body;
    @Column(name = Feedback.Columns.THROWABLE)
    private String throwable;
    @Column(name = Feedback.Columns.MESSAGE)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Feedback.Columns.ACCOUNT)
    private AccountModel account;

    FeedbackModel() { // jpa only
    }

    public FeedbackModel(
            String body,
            String throwable,
            String message,
            AccountModel account
    ) {
        this.body = body;
        this.throwable = throwable;
        this.message = message;
        this.account = account;
    }
}
