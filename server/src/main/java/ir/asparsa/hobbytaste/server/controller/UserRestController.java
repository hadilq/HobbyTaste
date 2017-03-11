package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.AuthenticateDto;
import ir.asparsa.common.net.path.UserServicePath;
import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.exception.EmptyUsernameException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.security.model.AuthenticatedUser;
import ir.asparsa.hobbytaste.server.util.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * @author hadi
 * @since 12/1/2016 AD
 */
@RestController
@RequestMapping(WebSecurityConfig.ENTRY_POINT_API + "/" + UserServicePath.SERVICE) class UserRestController {

    private final static Logger logger = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public UserRestController() {
    }

    @RequestMapping(value = UserServicePath.AUTHENTICATE, method = RequestMethod.POST)
    AuthenticateDto authorization() {
        String username = generateUsername();
        logger.info("username: " + username);
        AccountModel account = new AccountModel(username, "USER");
        account = accountRepository.save(account);

        return new AuthenticateDto(jwtTokenUtil.generateToken(account), username);
    }

    @RequestMapping(value = UserServicePath.USERNAME, method = RequestMethod.POST)
    AuthenticateDto changeUsername(
            @RequestParam("new") String username,
            @RequestParam(value = "locale", defaultValue = Strings.DEFAULT_LOCALE) String locale,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        if (StringUtils.isEmpty(username)) {
            throw new EmptyUsernameException("Username is empty", Strings.USERNAME_IS_EMPTY, locale);
        }
        logger.info("username: " + username);

        AccountModel account = user.getAccount();

        account.setUsername(username);
        accountRepository.save(account);

        return new AuthenticateDto(jwtTokenUtil.generateToken(account), username);
    }

    private String generateUsername() {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        for (char ch = 'A'; ch <= 'Z'; ++ch)
            tmp.append(ch);
//        tmp.appendCodePoint(0x1F600);
//        tmp.appendCodePoint(0x1F601);
//        tmp.appendCodePoint(0x1F602);
//        tmp.appendCodePoint(0x1F603);
//        tmp.appendCodePoint(0x1F604);
//        tmp.appendCodePoint(0x1F605);
//        tmp.appendCodePoint(0x1F606);
//        tmp.appendCodePoint(0x1F609);
//        tmp.appendCodePoint(0x1F923);
//        tmp.appendCodePoint(0x1F60A);
        char[] symbols = tmp.toString().toCharArray();

        Random random = new Random();
        char[] buf = new char[8];

        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }


}
