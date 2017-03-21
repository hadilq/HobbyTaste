package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.AuthenticateDto;
import ir.asparsa.common.net.dto.AuthenticateRequestDto;
import ir.asparsa.common.net.path.UserServicePath;
import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.exception.EmptyUsernameException;
import ir.asparsa.hobbytaste.server.exception.HashCodeExpiredException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.security.model.AuthenticatedUser;
import ir.asparsa.hobbytaste.server.util.JwtTokenUtil;
import ir.asparsa.hobbytaste.server.util.RequestLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author hadi
 * @since 12/1/2016 AD
 */
@RestController
@RequestMapping(WebSecurityConfig.ENTRY_POINT_API + "/" + UserServicePath.SERVICE) class UserRestController {

    private final static Logger logger = LoggerFactory.getLogger(UserRestController.class);

    @Value("${authorization.hashCodeExpirationTime}")
    int hashCodeExpirationTime;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RequestLogUtil requestLogUtil;

    public UserRestController() {
    }

    @RequestMapping(value = UserServicePath.AUTHENTICATE, method = RequestMethod.POST)
    AuthenticateDto authorization(
            @RequestBody AuthenticateRequestDto authenticateRequestDto,
            HttpServletRequest request
    ) {
        logger.debug("Authentication gets called");
        AccountModel accountModel;
        Optional<AccountModel> account = accountRepository.findByHashCode(authenticateRequestDto.getHashCode());
        if (!account.isPresent()) {
            accountModel = new AccountModel(generateUsername(), authenticateRequestDto.getHashCode(), "USER");
            accountModel = accountRepository.save(accountModel);
            logger.debug("New username: " + accountModel.getUsername());
        } else if (System.currentTimeMillis() - account.get().getCreated() > hashCodeExpirationTime) {
            logger.debug("Authentication is expired");
            String locale = request.getParameter("locale");
            throw new HashCodeExpiredException("hash code is expired", Strings.HASH_CODE_EXPIRED,
                                      StringUtils.isEmpty(locale) ? Strings.DEFAULT_LOCALE : locale);
        } else {
            logger.debug("Authenticated before");
            accountModel = account.get();
        }

        requestLogUtil.asyncLog(request, accountModel);
        return new AuthenticateDto(jwtTokenUtil.generateToken(accountModel), accountModel.getUsername());
    }

    @RequestMapping(value = UserServicePath.USERNAME, method = RequestMethod.POST)
    AuthenticateDto changeUsername(
            @RequestParam("new") String username,
            @PathVariable("hashCode") Long hashCode,
            @RequestParam(value = "locale", defaultValue = Strings.DEFAULT_LOCALE) String locale,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        if (StringUtils.isEmpty(username)) {
            throw new EmptyUsernameException("Username is empty", Strings.USERNAME_IS_EMPTY, locale);
        }
        logger.debug("username: " + username);
        AccountModel account = user.getAccount();

        account.setUsername(username);
        account.setHashCode(hashCode);
        accountRepository.save(account);

        return new AuthenticateDto(jwtTokenUtil.generateToken(account), username);
    }

    private String generateUsername() {
        List<Integer> list = new ArrayList<>();
        for (char ch = '0'; ch <= '9'; ++ch)
            list.add((int) ch);
        for (char ch = 'a'; ch <= 'z'; ++ch)
            list.add((int) ch);
        for (char ch = 'A'; ch <= 'Z'; ++ch)
            list.add((int) ch);
        list.add(0x1F600);

        list.add(0x1F600);
        list.add(0x1F601);
        list.add(0x1F602);
        list.add(0x1F603);
        list.add(0x1F604);
        list.add(0x1F606);
        list.add(0x1F609);
        list.add(0x1F60A);
        list.add(0x1F60E);
        list.add(0x1F913);

        Random random = new Random();
        StringBuilder tmp = new StringBuilder();

        for (int idx = 0; idx < 8; ++idx)
            tmp.appendCodePoint(list.get(random.nextInt(list.size())));

        return tmp.toString();
    }


}
