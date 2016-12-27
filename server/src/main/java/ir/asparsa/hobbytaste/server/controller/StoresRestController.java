package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.database.model.CommentColumns;
import ir.asparsa.common.net.dto.PageDto;
import ir.asparsa.common.net.dto.ResponseDto;
import ir.asparsa.common.net.dto.StoreCommentDto;
import ir.asparsa.common.net.dto.StoreDto;
import ir.asparsa.hobbytaste.server.database.model.CommentModel;
import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.database.repository.StoreCommentRepository;
import ir.asparsa.hobbytaste.server.database.repository.StoreRepository;
import ir.asparsa.hobbytaste.server.exception.StoreNotFoundException;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@RestController
@RequestMapping(WebSecurityConfig.ENTRY_POINT_API + "/stores") class StoresRestController {

    private final static Logger logger = LoggerFactory.getLogger(StoresRestController.class);

    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final StoreCommentRepository storeCommentRepository;

    @Autowired StoresRestController(
            AccountRepository accountRepository,
            StoreRepository storeRepository,
            StoreCommentRepository storeCommentRepository
    ) {
        this.accountRepository = accountRepository;
        this.storeRepository = storeRepository;
        this.storeCommentRepository = storeCommentRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<StoreDto> readStores() {
        logger.info("read stores request");

        Collection<StoreDto> collection = new LinkedList<>();
        for (StoreModel storeModel : storeRepository.findAll()) {
            collection.add(storeModel.convertToDto());
        }
        return collection;
    }

    @RequestMapping(value = "/{storeId}/comments", method = RequestMethod.POST)
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

    @RequestMapping(value = "/{storeId}/comments", method = RequestMethod.PUT)
    ResponseDto saveStoreComments(
            @PathVariable("storeId") Long id,
            @RequestBody StoreCommentDto comment
    ) {
        logger.info("save stores request: storeId: " + id + "comment: " + comment);

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
}