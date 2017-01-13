package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.database.model.Comment;
import ir.asparsa.common.net.dto.PageDto;
import ir.asparsa.common.net.dto.StoreCommentDto;
import ir.asparsa.common.net.dto.StoreDto;
import ir.asparsa.common.net.path.StoreServicePath;
import ir.asparsa.hobbytaste.server.database.model.*;
import ir.asparsa.hobbytaste.server.database.repository.CommentLikeRepository;
import ir.asparsa.hobbytaste.server.database.repository.StoreCommentRepository;
import ir.asparsa.hobbytaste.server.database.repository.StoreLikeRepository;
import ir.asparsa.hobbytaste.server.database.repository.StoreRepository;
import ir.asparsa.hobbytaste.server.exception.CommentNotFoundException;
import ir.asparsa.hobbytaste.server.exception.InternalServerErrorException;
import ir.asparsa.hobbytaste.server.exception.StoreNotFoundException;
import ir.asparsa.hobbytaste.server.security.JwtTokenUtil;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@RestController
@RequestMapping(WebSecurityConfig.ENTRY_POINT_API + "/" + StoreServicePath.SERVICE) class StoresRestController {

    private final static Logger logger = LoggerFactory.getLogger(StoresRestController.class);

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    StoreRepository storeRepository;
    @Autowired
    StoreLikeRepository storeLikeRepository;
    @Autowired
    StoreCommentRepository storeCommentRepository;
    @Autowired
    CommentLikeRepository commentLikeRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;


    StoresRestController() {
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<StoreDto> readStores(HttpServletRequest request) {
        logger.info("read stores request");

        AccountModel account = jwtTokenUtil.parseToken(request.getHeader(tokenHeader));
        if (account == null) {
            // This request must be authorized before, so this never should happened
            logger.error("Account is null, header " + request.getHeader(tokenHeader));
            throw new InternalServerErrorException();
        }
        List<StoreLikeModel> storeLikes = storeLikeRepository.findByAccount(account);

        Collection<StoreDto> collection = new LinkedList<>();
        for (StoreModel storeModel : storeRepository.findAll()) {
            boolean like = false;
            for (StoreLikeModel storeLike : storeLikes) {
                if (storeLike.getStore().equals(storeModel)) {
                    like = true;
                    break;
                }
            }
            collection.add(storeModel.convertToDto(like));
        }
        return collection;
    }

    @RequestMapping(value = StoreServicePath.VIEWED, method = RequestMethod.PUT)
    StoreDto storeViewed(
            @PathVariable("storeId") Long id,
            HttpServletRequest request
    ) {
        logger.info("read stores request");

        Optional<StoreModel> storeModel = storeRepository.findById(id);
        if (!storeModel.isPresent()) {
            throw new StoreNotFoundException();
        }
        AccountModel account = jwtTokenUtil.parseToken(request.getHeader(tokenHeader));
        if (account == null) {
            // This request must be authorized before, so this never should happened
            logger.error("Account is null, header " + request.getHeader(tokenHeader));
            throw new InternalServerErrorException();
        }
        Optional<StoreLikeModel> storeLike = storeLikeRepository.findByAccountAndStore(account, storeModel.get());

        storeModel.get().increaseViewed();
        Observable.create((Observable.OnSubscribe<Void>) subscriber -> storeRepository.save(storeModel.get()))
                  .subscribeOn(Schedulers.newThread())
                  .subscribe();

        return storeModel.get().convertToDto(storeLike.isPresent());
    }

    @RequestMapping(value = StoreServicePath.COMMENTS, method = RequestMethod.POST)
    PageDto<StoreCommentDto> readStoreCommentsList(
            @PathVariable("storeId") Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        logger.info("read store comments request: storeId: " + id + ", page: " + page + ", size: " + size);

        AccountModel account = jwtTokenUtil.parseToken(request.getHeader(tokenHeader));
        if (account == null) {
            // This request must be authorized before, so this never should happened
            logger.error("Account is null, header " + request.getHeader(tokenHeader));
            throw new InternalServerErrorException();
        }

        Optional<StoreModel> storeModel = storeRepository.findById(id);
        if (!storeModel.isPresent()) {
            throw new StoreNotFoundException();
        }

        Pageable pageable = new PageRequest(page, size, new Sort(
                new Sort.Order(Sort.Direction.DESC, Comment.Columns.CREATED)
        ));
        Page<CommentModel> comments = storeCommentRepository.findByStore(storeModel.get(), pageable);

        List<CommentLikeModel> commentLikes = commentLikeRepository.findByAccountAndStore(account, storeModel.get());
        List<StoreCommentDto> list = new ArrayList<>();
        for (CommentModel comment : comments) {
            boolean like = false;
            for (CommentLikeModel commentLike : commentLikes) {
                if (commentLike.getComment().equals(comment)) {
                    like = true;
                    break;
                }
            }
            list.add(comment.convertToDto(like));
            logger.info("Loaded comment: " + comment);
        }

        return new PageDto<>(comments.getTotalElements(), list);
    }

    @RequestMapping(value = StoreServicePath.COMMENTS, method = RequestMethod.PUT)
    StoreCommentDto saveStoreComments(
            @PathVariable("storeId") Long id,
            @RequestBody StoreCommentDto comment,
            HttpServletRequest request
    ) {
        logger.info("save stores comment request: storeId: " + id + ", comment: " + comment);

        Optional<StoreModel> storeModel = storeRepository.findById(id);
        if (!storeModel.isPresent()) {
            throw new StoreNotFoundException();
        }
        Optional<CommentModel> existComment = storeCommentRepository
                .findByHashCodeAndStore(comment.getHashCode(), storeModel.get());

        CommentModel entity;
        if (!existComment.isPresent()) {
            AccountModel account = jwtTokenUtil.parseToken(request.getHeader(tokenHeader));
            if (account == null) {
                // This request must be authorized before, so this never should happened
                logger.error("Account is null, header " + request.getHeader(tokenHeader));
                throw new InternalServerErrorException();
            }

            entity = CommentModel.newInstance(comment, storeModel.get(), account);
            logger.info("Saved comment: " + entity);
            entity = storeCommentRepository.save(entity);
        } else {
            entity = existComment.get();
        }
        return entity.convertToDto(false);
    }

    @RequestMapping(value = StoreServicePath.LIKE, method = RequestMethod.PUT)
    StoreDto saveStoreLike(
            @PathVariable("storeId") Long id,
            @PathVariable("like") Boolean like,
            HttpServletRequest request
    ) {
        logger.info("save store like request: storeId: " + id + ", liked: " + like);

        Optional<StoreModel> storeModel = storeRepository.findById(id);
        if (!storeModel.isPresent()) {
            throw new StoreNotFoundException();
        }

        AccountModel account = jwtTokenUtil.parseToken(request.getHeader(tokenHeader));
        if (account == null) {
            // This request must be authorized before, so this never should happened
            logger.error("Account is null, header " + request.getHeader(tokenHeader));
            throw new InternalServerErrorException();
        }

        List<StoreLikeModel> storeLikes = storeLikeRepository.findByAccount(account);
        StoreLikeModel storeLiked = null;
        for (StoreLikeModel storeLike : storeLikes) {
            if (storeLike.getStore().equals(storeModel.get())) {
                storeLiked = storeLike;
                break;
            }
        }

        if (like && storeLiked == null) {
            storeLikeRepository.save(new StoreLikeModel(storeModel.get(), account));
        } else if (!like && storeLiked != null) {
            storeLikeRepository.delete(storeLiked);
        }

        if (like) {
            storeModel.get().increaseRate();
        } else {
            storeModel.get().decreaseRate();
        }

        Observable.create((Observable.OnSubscribe<Void>) subscriber -> storeRepository.save(storeModel.get()))
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(new Observer<Void>() {
                      @Override public void onCompleted() {
                      }

                      @Override public void onError(Throwable e) {
                      }

                      @Override public void onNext(Void aVoid) {
                      }
                  });

        return storeModel.get().convertToDto(like);
    }

    @RequestMapping(value = StoreServicePath.LIKE_COMMENT, method = RequestMethod.PUT)
    StoreCommentDto saveCommentLike(
            @PathVariable("storeId") Long id,
            @PathVariable("commentHashCode") Long hashCode,
            @PathVariable("like") Boolean like,
            HttpServletRequest request
    ) {
        logger.info(
                "save comment like request: storeId: " + id + ", comment hash code: " + hashCode + ", liked: " + like);

        Optional<StoreModel> storeModel = storeRepository.findById(id);
        if (!storeModel.isPresent()) {
            throw new StoreNotFoundException();
        }

        Optional<CommentModel> commentModel = storeCommentRepository.findByHashCodeAndStore(hashCode, storeModel.get());
        if (!commentModel.isPresent()) {
            throw new CommentNotFoundException();
        }

        AccountModel account = jwtTokenUtil.parseToken(request.getHeader(tokenHeader));
        if (account == null) {
            // This request must be authorized before, so this never should happened
            logger.error("Account is null, header " + request.getHeader(tokenHeader));
            throw new InternalServerErrorException();
        }

        List<CommentLikeModel> commentLikes = commentLikeRepository.findByAccountAndStore(account, storeModel.get());
        CommentLikeModel commentLiked = null;
        for (CommentLikeModel commentLike : commentLikes) {
            if (commentLike.getComment().equals(commentModel.get())) {
                commentLiked = commentLike;
                break;
            }
        }

        if (like && commentLiked == null) {
            commentLikeRepository.save(new CommentLikeModel(commentModel.get(), account));
        } else if (!like && commentLiked != null) {
            commentLikeRepository.delete(commentLiked);
        }

        if (like) {
            commentModel.get().increaseRate();
        } else {
            commentModel.get().decreaseRate();
        }
        Observable.create((Observable.OnSubscribe<Void>) subscriber -> storeCommentRepository.save(commentModel.get()))
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(new Observer<Void>() {
                      @Override public void onCompleted() {
                      }

                      @Override public void onError(Throwable e) {
                      }

                      @Override public void onNext(Void aVoid) {
                      }
                  });

        return commentModel.get().convertToDto(like);
    }
}