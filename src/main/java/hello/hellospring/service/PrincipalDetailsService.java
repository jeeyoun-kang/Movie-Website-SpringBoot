package hello.hellospring.service;

import hello.hellospring.auth.PrincipalDetails;
import hello.hellospring.dto.AuthDto;
import hello.hellospring.dto.PassDto;
import hello.hellospring.dto.RequestDto;
import hello.hellospring.dto.UserDto;
import hello.hellospring.entity.Authentication;
import hello.hellospring.entity.Password;
import hello.hellospring.entity.SocialLogin;
import hello.hellospring.entity.User;
import hello.hellospring.repository.AuthRepository;
import hello.hellospring.repository.PassRepository;
import hello.hellospring.repository.SocialRepository;
import hello.hellospring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static hello.hellospring.entity.Authentication.Role.ROLE_USER;

@Service
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private AuthRepository authRepository;
    private PassRepository passRepository;
    private SocialRepository socialRepository;

    public PrincipalDetailsService(UserRepository userRepository, AuthRepository authRepository, PassRepository passRepository, SocialRepository socialRepository) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
        this.passRepository = passRepository;
        this.socialRepository = socialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User byUsername = userRepository.findByUsername(username);
        Authentication authByUser = authRepository.findAuthenticationByUser(byUsername);
        Password passByUser = passRepository.findPasswordByUser(byUsername);
        SocialLogin socialByUser = socialRepository.findSocialLoginByUser(byUsername);
        if(byUsername != null){
            return new PrincipalDetails(byUsername, authByUser, passByUser, socialByUser);
        }
        return null;
    }

    @Transactional
    public String create(RequestDto request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        LocalDateTime now =  LocalDateTime.now();
        boolean existMember = userRepository.existsUserByUsername(request.getUsername());

        if(existMember) return null;

        Password pass = new Password(encoder.encode(request.getPassword()), now);
        passRepository.save(pass);
        Authentication auth = new Authentication(ROLE_USER, request.getCellphone(), request.getBirthday(), now);
        authRepository.save(auth);
        User user = new User(request.getUsername(), 0, auth, pass, null);
        userRepository.save(user);


        return user.getUsername();
    }

    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }
//    @Transactional
//    public Long update(Long id, UserDto userDto) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new
//                        IllegalArgumentException("?????? ????????? ???????????? ????????????."));
//
//        user.update(userDto.getUserName(), userDto.getLoginType(), userDto.getAuth(), userDto.getPassword(), userDto.getSocialLogin());
//        return id;
//    }
//    @Transactional
//    public String updatePass(String userName, String password, UserDto userDto) {
//        User user = loadUserByUsername(userName);
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        userDto.setPassword(encoder.encode(password));
//
//        user.update(userDto.getUsername(), userDto.getLoginType(), userDto.getAuth(), userDto.getPassword());
//        return userName;
//    }
}