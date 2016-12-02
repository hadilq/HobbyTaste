package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.database.repository.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@RestController
@RequestMapping("/stores") class StoresRestController {

    private final static Logger logger = LoggerFactory.getLogger(StoresRestController.class);

    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;

    @Autowired StoresRestController(
            AccountRepository accountRepository,
            StoreRepository storeRepository) {
        this.accountRepository = accountRepository;
        this.storeRepository = storeRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<StoreModel> readStores() {
        return storeRepository.findAll();
    }
}