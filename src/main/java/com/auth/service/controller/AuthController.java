package com.auth.service.controller;

import javax.validation.Valid;

import com.auth.service.base.request.LoginRequest;
import com.auth.service.base.request.SignupRequest;
import com.auth.service.base.response.MessageResponse;
import com.auth.service.exception.BusinessException;
import com.auth.service.exception.ErrorCode;
import com.auth.service.repository.RoleRepository;
import com.auth.service.repository.UserRepository;
import com.auth.service.security.jwt.JwtUtils;
import com.auth.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  UserService userService;

  //Login
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return userService.authenticateUser(loginRequest);
  }

  //Register
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    try {
      return userService.registerUser(signUpRequest);
    } catch (Exception ex) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(ex.getMessage(), 400));
    }
  }

  // Verify jwt token
  @GetMapping("/verify/{token}")
  public ResponseEntity<?> verifyJwtToken(@PathVariable String token) {
    try {
      return userService.verifyJwtToken(token);
    } catch (Exception ex) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(ex.getMessage(), 400));
    }
  }

  @GetMapping("/user/{token}")
  public ResponseEntity<?> getUserInfoByJWT(@PathVariable String token){
    try{
      return userService.getUserInfoByJWT(token);
    }catch (Exception ex){
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(ex.getMessage(), 400));
    }
  }
}
