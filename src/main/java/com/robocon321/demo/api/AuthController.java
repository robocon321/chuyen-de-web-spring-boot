package com.robocon321.demo.api;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.robocon321.demo.domain.CustomUserDetails;
import com.robocon321.demo.domain.EmailDetails;
import com.robocon321.demo.domain.ResponseObject;
import com.robocon321.demo.dto.user.UserAccountDTO;
import com.robocon321.demo.dto.user.UserDTO;
import com.robocon321.demo.dto.user.UserSocialDTO;
import com.robocon321.demo.entity.user.User;
import com.robocon321.demo.entity.user.UserAccount;
import com.robocon321.demo.repository.UserAccountRepository;
import com.robocon321.demo.service.common.EmailService;
import com.robocon321.demo.service.user.AuthService;
import com.robocon321.demo.token.JwtTokenProvider;
import com.robocon321.demo.util.Util;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserAccountRepository userAccountRepository;
	
	@Autowired
	private EmailService emailService;

	@PostMapping("/loginAccount")
	public ResponseEntity loginAccount(@Valid @RequestBody UserAccountDTO userAccountDTO, BindingResult result) {
		ResponseObject response = new ResponseObject<>();
		if (result.hasErrors()) {
			String message = "";
			for (ObjectError error : result.getAllErrors()) {
				message += error.getDefaultMessage() + ". ";
			}
			response.setMessage(message.trim());
			response.setSuccess(false);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} else {
			try {
				Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(userAccountDTO.getUsername(),
								userAccountDTO.getPassword()));
				SecurityContextHolder.getContext().setAuthentication(authentication);

				User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
				UserDTO userDTO = new UserDTO();
				BeanUtils.copyProperties(user, userDTO);
				String jwt = tokenProvider.generateToken(userDTO);

				response.setData(jwt);
				response.setMessage("Successful!");
				response.setSuccess(true);
				return ResponseEntity.status(HttpStatus.OK).body(response);

			} catch (Exception e) {
				e.printStackTrace();
				response.setMessage(e.getMessage());
				response.setSuccess(false);				
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
			}

		}

	}

	@PostMapping("/loginSocial")
	public ResponseEntity loginSocial(@Valid @RequestBody UserSocialDTO userSocialDTO, BindingResult result) {
		ResponseObject response = new ResponseObject<>();
		if (result.hasErrors()) {
			String message = "";
			for (ObjectError error : result.getAllErrors()) {
				message += error.getDefaultMessage() + ". ";
			}
			response.setMessage(message.trim());
			response.setSuccess(false);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} else {
			try {
				UserDTO userDTO = authService.loginSocial(userSocialDTO);
				String jwt = tokenProvider.generateToken(userDTO);
				response.setData(jwt);
				response.setMessage("Successful!");
				response.setSuccess(true);
				return ResponseEntity.status(HttpStatus.OK).body(response);

			} catch (Exception e) {
				response.setMessage(e.getMessage());
				response.setSuccess(false);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
			}

		}
	}
	
	@PostMapping("/loadUser")
	public ResponseEntity loadUser() {
		ResponseObject response = new ResponseObject<>();
		try {
			if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
				response.setSuccess(false);
				response.setMessage("Not login or not exists your user");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			} else {
				UserDTO obj = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				response.setData(obj);
				response.setSuccess(true);
				response.setMessage("Successfull!");
				return ResponseEntity.ok(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Load user fail!");
			response.setSuccess(false);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		}
	}

	@PostMapping("/registerByAccount")
	public ResponseEntity registerByAccount(@Valid @RequestBody UserAccountDTO userAccountDTO, BindingResult result) {
		ResponseObject response = new ResponseObject<>();

		if (result.hasErrors()) {
			String message = "";
			for (ObjectError error : result.getAllErrors()) {
				message += error.getDefaultMessage() + ". ";
			}
			response.setMessage(message.trim());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

		} else {
			try {
				UserDTO userDTO = authService.addNewAccountUser(userAccountDTO);
				String jwt = tokenProvider.generateToken(userDTO);
				response.setData(jwt);
				response.setSuccess(true);
				return ResponseEntity.ok().body(response);
			} catch (RuntimeException e) {
				e.printStackTrace();
				response.setMessage(e.getMessage());
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
			}
		}
	}

	@PostMapping("/registerBySocial")
	public ResponseEntity registerBySocial(@Valid @RequestBody UserSocialDTO userSocialDTO, BindingResult result) {
		ResponseObject response = new ResponseObject<>();
		if (result.hasErrors()) {
			String message = "";
			for (ObjectError error : result.getAllErrors()) {
				message += error.getDefaultMessage() + ". ";
			}
			response.setMessage(message.trim());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} else {
			try {
				UserDTO userDTO = authService.addNewSocialUser(userSocialDTO);
				String jwt = tokenProvider.generateToken(userDTO);
				response.setData(jwt);
				response.setSuccess(true);
				return ResponseEntity.ok(response);
			} catch (Exception e) {
				e.printStackTrace();
				response.setMessage(e.getMessage());
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
			}
		}
		
	}

	@PostMapping("/reset")
	public ResponseEntity resetPass(@RequestBody UserAccountDTO userAccountDTO) { 
		String username = userAccountDTO.getUsername();
		ResponseObject response = new ResponseObject<>();
		
		if(username == null || username.length() == 0) {
			response.setMessage("Your username is null");
			response.setSuccess(false);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		UserAccount userAccount = userAccountRepository.findByUsername(username);

		if(userAccount == null) {
			response.setMessage("Your account isn't found");
			response.setSuccess(false);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		User user = userAccount.getUser();
		
		if(user.getEmail() == null || user.getEmail().length() == 0) {
			response.setMessage("Your email does not found");
			response.setSuccess(false);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		String newPass = Util.generatePassayPassword();
		
		userAccount.setPassword(newPass);
		userAccountRepository.save(userAccount);
		
		EmailDetails emailDetails = new EmailDetails(user.getEmail(), "Cập nhật mật khẩu cho tài khoản: " + userAccount.getUsername(), "Mật khẩu mới của bạn là: " + newPass);
	
		if(emailService.sendSimpleMail(emailDetails)) {
			response.setMessage("Successful! We just send new password to " +user.getEmail());
			response.setSuccess(true);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.setMessage("Can not send mail");
			response.setSuccess(false);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);			
		}
	}	
}
