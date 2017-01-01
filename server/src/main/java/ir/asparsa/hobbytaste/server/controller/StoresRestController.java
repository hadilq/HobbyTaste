package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.database.model.CommentColumns;
import ir.asparsa.common.net.dto.PageDto;
import ir.asparsa.common.net.dto.ResponseDto;
import ir.asparsa.common.net.dto.StoreCommentDto;
import ir.asparsa.common.net.dto.StoreDto;
import ir.asparsa.common.net.path.StoreServicePath;
import ir.asparsa.hobbytaste.server.database.model.*;
import ir.asparsa.hobbytaste.server.database.repository.*;
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
import rx.schedulers.Schedulers;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    AccountRepository accountRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    StoreLikeRepository storeRateRepository;
    @Autowired
    StoreCommentRepository storeCommentRepository;
    @Autowired
    CommentLikeRepository commentLikeRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;


    @Autowired StoresRestController() {
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
        List<StoreLikeModel> storeLikes = storeRateRepository.findByAccount(account);

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
        Optional<StoreLikeModel> storeLike = storeRateRepository.findByAccountAndStore(account, storeModel.get());

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
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        logger.info("read store comments request: storeId: " + id + ", page: " + page + ", size: " + size);

        Optional<StoreModel> storeModel = storeRepository.findById(id);
        if (!storeModel.isPresent()) {
            throw new StoreNotFoundException();
        }

        Pageable pageable = new PageRequest(page, size, new Sort(
                new Sort.Order(Sort.Direction.DESC, CommentColumns.CREATED)
        ));
        Page<CommentModel> comments = storeCommentRepository.findByStore(storeModel.get(), pageable);

        List<StoreCommentDto> list = new ArrayList<>();

        for (CommentModel comment : comments) {
            list.add(CommentModel.convertToDto(comment));
            logger.info("Loaded comment: " + comment);
        }

        return new PageDto<>(comments.getTotalElements(), list);
    }

    @RequestMapping(value = StoreServicePath.COMMENTS, method = RequestMethod.PUT)
    ResponseDto saveStoreComments(
            @PathVariable("storeId") Long id,
            @RequestBody StoreCommentDto comment
    ) {
        logger.info("save stores comment request: storeId: " + id + ", comment: " + comment);

        Optional<StoreModel> storeModel = storeRepository.findById(id);
        if (!storeModel.isPresent()) {
            throw new StoreNotFoundException();
        }
        Optional<CommentModel> existComment = storeCommentRepository
                .findByHashCodeAndStore(comment.getHashCode(), storeModel.get());

        if (!existComment.isPresent()) {
            CommentModel entity = CommentModel.newInstance(comment, storeModel.get());
            logger.info("Saved comment: " + entity);
            storeCommentRepository.save(entity);
        }
        return new ResponseDto(ResponseDto.STATUS.SUCCEED);
    }

    @RequestMapping(value = StoreServicePath.LIKE, method = RequestMethod.PUT)
    ResponseDto saveStoreLike(
            @PathVariable("storeId") Long id,
            HttpServletRequest request
    ) {
        logger.info("save store like request: storeId: " + id + ", liked");

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
        List<StoreLikeModel> storeLikes = storeRateRepository.findByAccount(account);
        StoreLikeModel storeLiked = null;
        for (StoreLikeModel storeLike : storeLikes) {
            if (storeLike.getStore().equals(storeModel.get())) {
                storeLiked = storeLike;
                break;
            }
        }

        if (storeLiked == null) {
            storeRateRepository.save(new StoreLikeModel(storeModel.get(), account));
        }

        storeModel.get().increaseRate();
        Observable.create((Observable.OnSubscribe<Void>) subscriber -> storeRepository.save(storeModel.get()))
                  .subscribeOn(Schedulers.newThread())
                  .subscribe();

        return new ResponseDto(ResponseDto.STATUS.SUCCEED);
    }

    @RequestMapping(value = StoreServicePath.UNLIKE, method = RequestMethod.PUT)
    ResponseDto saveStoreUnlike(
            @PathVariable("storeId") Long id,
            HttpServletRequest request
    ) {
        logger.info("save store like request: storeId: " + id + ", unliked");

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
        List<StoreLikeModel> storeLikes = storeRateRepository.findByAccount(account);
        StoreLikeModel storeLiked = null;
        for (StoreLikeModel storeLike : storeLikes) {
            if (storeLike.getStore().equals(storeModel.get())) {
                storeLiked = storeLike;
                break;
            }
        }

        if (storeLiked != null) {
            storeRateRepository.delete(storeLiked);
        }

        storeModel.get().decreaseRate();
        Observable.create((Observable.OnSubscribe<Void>) subscriber -> storeRepository.save(storeModel.get()))
                  .subscribeOn(Schedulers.newThread())
                  .subscribe();

        return new ResponseDto(ResponseDto.STATUS.SUCCEED);
    }

    @RequestMapping(value = StoreServicePath.LIKE_COMMENT, method = RequestMethod.PUT)
    ResponseDto saveCommentLike(
            @PathVariable("storeId") Long id,
            @PathVariable("commentHashCode") Long hashCode,
            HttpServletRequest request
    ) {
        logger.info("save comment like request: storeId: " + id + ", comment hash code: " + hashCode + ", liked");

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

        List<CommentLikeModel> commentLikes = commentLikeRepository.findByAccount(account);
        CommentLikeModel commentLiked = null;
        for (CommentLikeModel commentLike : commentLikes) {
            if (commentLike.getComment().equals(commentModel.get())) {
                commentLiked = commentLike;
                break;
            }
        }

        if (commentLiked == null) {
            commentLikeRepository.save(new CommentLikeModel(commentModel.get(), account));
        }

        commentModel.get().increaseRate();
        Observable.create((Observable.OnSubscribe<Void>) subscriber -> storeCommentRepository.save(commentModel.get()))
                  .subscribeOn(Schedulers.newThread())
                  .subscribe();

        return new ResponseDto(ResponseDto.STATUS.SUCCEED);
    }

    @RequestMapping(value = StoreServicePath.UNLIKE_COMMENT, method = RequestMethod.PUT)
    ResponseDto saveCommentUnlike(
            @PathVariable("storeId") Long id,
            @PathVariable("commentHashCode") Long hashCode,
            HttpServletRequest request
    ) {
        logger.info("save comment like request: storeId: " + id + ", comment hash code: " + hashCode + ", unliked");

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

        List<CommentLikeModel> commentLikes = commentLikeRepository.findByAccount(account);
        CommentLikeModel commentLiked = null;
        for (CommentLikeModel commentLike : commentLikes) {
            if (commentLike.getComment().equals(commentModel.get())) {
                commentLiked = commentLike;
                break;
            }
        }

        if (commentLiked != null) {
            commentLikeRepository.delete(commentLiked);
        }

        commentModel.get().decreaseRate();
        Observable.create((Observable.OnSubscribe<Void>) subscriber -> storeCommentRepository.save(commentModel.get()))
                  .subscribeOn(Schedulers.newThread())
                  .subscribe();

        return new ResponseDto(ResponseDto.STATUS.SUCCEED);
    }
}