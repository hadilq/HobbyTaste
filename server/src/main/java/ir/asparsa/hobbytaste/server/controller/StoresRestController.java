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
            list.add(new StoreCommentDto(comment.getId(), comment.getRate(), comment.getDescription(),
                                         comment.getCreated(), comment.getStore().getId()));
        }

        return new PageDto<>(comments.getTotalElements(), list);
    }

    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    List<StoreCommentDto> readStoreComments(@RequestParam("ids") List<Long> ids) {
        List<StoreCommentDto> list = new ArrayList<>();

        for (Long id : ids) {
            Optional<CommentModel> comment = storeCommentRepository.findById(id);
            if (comment.isPresent()) {
                list.add(new StoreCommentDto(comment.get().getId(), comment.get().getRate(),
                                             comment.get().getDescription(), comment.get().getCreated(),
                                             comment.get().getStore().getId()));
            }
        }
        return list;
    }

    @RequestMapping(value = "/{storeId}/comments", method = RequestMethod.PUT)
    ResponseDto saveStoreComments(
            @PathVariable("storeId") Long id,
            @RequestBody StoreCommentDto comment
    ) {

        Optional<StoreModel> storeModel = storeRepository.findById(id);
        if (!storeModel.isPresent()) {
            throw new StoreNotFoundException();
        }
        Optional<CommentModel> existComment = storeCommentRepository
                .findByHashCodeAndStore(comment.getHashCode(), storeModel.get());

        if (!existComment.isPresent()) {
            storeCommentRepository
                    .save(new CommentModel(comment.getDescription(), comment.getHashCode(), storeModel.get()));
        }
        return new ResponseDto();
    }
}