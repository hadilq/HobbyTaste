package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.security.JwtTokenUtil;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * @author hadi
 * @since 12/1/2016 AD
 */
@RestController
@RequestMapping(WebSecurityConfig.ENTRY_POINT_API + "/authenticate") class AuthenticateRestController {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticateRestController.class);

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public AuthenticateRestController() {
    }

    @RequestMapping(method = RequestMethod.POST)
    Map<String, String> authorization() {
        AccountModel account = new AccountModel("UNKNOWN", "USER");
        account = accountRepository.save(account);

        return Collections.singletonMap("token", jwtTokenUtil.generateToken(account));
    }
}