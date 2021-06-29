package com.auth.service.service;

import com.auth.service.dto.UserClientDTO;
import com.auth.service.dto.UserDTO;
import com.auth.service.entity.Role;
import com.auth.service.entity.User;
import com.auth.service.base.request.LoginRequest;
import com.auth.service.base.request.SignupRequest;
import com.auth.service.base.response.JwtResponse;
import com.auth.service.base.response.MessageResponse;
import com.auth.service.entity.enumeration.ERole;
import com.auth.service.repository.RoleRepository;
import com.auth.service.repository.UserRepository;
import com.auth.service.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private JwtUtils jwtUtils;

  public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateJwtToken(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      List<String> roles = userDetails.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toList());

      return ResponseEntity.ok(new JwtResponse(
              jwt,
              userDetails.getId(),
              userDetails.getUsername(),
              userDetails.getEmail(),
              roles));
    } catch (Exception ex) {
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
//        .body(new MessageResponse("Error: User name is already taken!", 400));
              .body(new MessageResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

  }

  public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
    //Check if username already existed
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
//        .body(new MessageResponse("Error: User name is already taken!", 400));
              .body(new MessageResponse("Error: User name is already taken!", 400));
    }

    //Check if email already existed
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(new MessageResponse("Error: Email is already taken!", 400));

    }

    //Create new user's account
    User user = new User(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),
            signUpRequest.getFirstName(),
            signUpRequest.getLastName()
    );

    Set<String> stringRoles = signUpRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (stringRoles == null) {
      Role userRole = roleRepository
              .findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
      roles.add(userRole);
    } else {
      stringRoles.forEach(role -> {
        if ("admin".equals(role)) {
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                  .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
          roles.add(adminRole);
        } else {
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                  .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
          roles.add(userRole);
        }
      });
    }
    user.setRoles(roles);
    userRepository.save(user);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!", 200));
  }


//  Decode jwt to verify usernam

  public static Claims decodeJWT(String jwt) {
    //This line will throw an exception if it is not a signed JWS (as expected)
    Claims claims = Jwts.parser()
            .setSigningKey(DatatypeConverter.parseBase64Binary("SECRET"))
            .parseClaimsJws(jwt).getBody();
    return claims;
  }

  //  Get current user info
  public ResponseEntity<?> verifyJwtToken(String token) {
    Boolean validate = jwtUtils.validateJwtToken(token);
    return ResponseEntity.ok().body(validate);
  }

  public ResponseEntity<?> getUserInfoByJWT(String token) {
    Claims userClaim = decodeJWT(token);
    String currentUsername = userClaim.getSubject();
    User currentUser = userRepository.findByUsername(currentUsername).get();

//    UserDTO currentUserDTO = new UserDTO();
//    currentUserDTO.setUserName(currentUser.getUsername());
//    currentUserDTO.setEmail(currentUser.getEmail());
//    currentUserDTO.setFirstName(currentUser.getFirstName());
//    currentUserDTO.setLastName(currentUser.getLastName());

    UserClientDTO currentUserDTO = userRepository.findUserClientDTOByUsername(currentUsername);

    return ResponseEntity.ok().body(currentUserDTO);
  }
}
