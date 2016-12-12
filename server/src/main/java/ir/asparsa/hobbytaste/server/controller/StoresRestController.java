package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.StoreDetailsDto;
import ir.asparsa.common.net.dto.StoreLightDto;
import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.database.repository.StoreBannerRepository;
import ir.asparsa.hobbytaste.server.database.repository.StoreRepository;
import ir.asparsa.hobbytaste.server.exception.StoreNotFoundException;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@RestController
@RequestMapping(WebSecurityConfig.ENTRY_POINT_API + "/stores") class StoresRestController {

    private final static Logger logger = LoggerFactory.getLogger(StoresRestController.class);

    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final StoreBannerRepository storeBannerRepository;

    @Autowired StoresRestController(
            AccountRepository accountRepository,
            StoreRepository storeRepository,
            StoreBannerRepository storeBannerRepository) {
        this.accountRepository = accountRepository;
        this.storeRepository = storeRepository;
        this.storeBannerRepository = storeBannerRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<StoreLightDto> readStores() {
        Collection<StoreLightDto> collection = new LinkedList<>();
        for (StoreModel storeModel : storeRepository.findAll()) {
            collection.add(storeModel.lightConvert());
        }
        return collection;
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    StoreDetailsDto readStoreDetails(@RequestParam("id") Long id) {
        Optional<StoreModel> storeModel = storeRepository.findById(id);
        if (storeModel.isPresent()) {
            return storeModel.get().detailsConvert();
        }
        throw new StoreNotFoundException();
    }
}