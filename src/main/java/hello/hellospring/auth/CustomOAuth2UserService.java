package hello.hellospring.auth;

import hello.hellospring.entity.Authentication;
import hello.hellospring.entity.Password;
import hello.hellospring.entity.SocialLogin;
import hello.hellospring.entity.User;
import hello.hellospring.repository.AuthRepository;
import hello.hellospring.repository.PassRepository;
import hello.hellospring.repository.SocialRepository;
import hello.hellospring.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PassRepository passRepository;
    private final AuthRepository authRepository;
    private final SocialRepository socialRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public CustomOAuth2UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository, PassRepository passRepository, AuthRepository authRepository, SocialRepository socialRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.passRepository = passRepository;
        this.authRepository = authRepository;
        this.socialRepository = socialRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;

        String provider = userRequest.getClientRegistration().getRegistrationId();
        if(provider.equals("naver")){
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        }
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"_"+providerId;  			// ???????????? ????????? ?????? ????????? ???????????????

        String uuid = UUID.randomUUID().toString().substring(0, 6);
        String password = bCryptPasswordEncoder.encode("????????????"+uuid);  // ???????????? ????????? ?????? ????????? ???????????????

        String birthday = oAuth2User.getAttribute("birthday");
        String cellphone = oAuth2User.getAttribute("mobile");
        Authentication.Role role = Authentication.Role.ROLE_USER;

        
        // ?????? ?????? ????????????
        User byUsername = userRepository.findByUsername(username);
        Password pass = null;
        Authentication auth = null;
        SocialLogin social = null;
        //DB??? ?????? ??????????????? ??????????????????
        if(byUsername == null){
            LocalDateTime now =  LocalDateTime.now();
            pass = new Password(password, now);
            auth = new Authentication(role, cellphone, birthday, now);
            social = new SocialLogin(0, providerId, "www", now);
            byUsername = new User(username, 1, auth, pass, social);
            passRepository.save(pass);
            authRepository.save(auth);
            socialRepository.save(social);
            userRepository.save(byUsername);
        }

        return new PrincipalDetails(byUsername, auth, pass, social, oAuth2UserInfo);
    }
}