package com.example.demomicroservice.service;

import com.example.demomicroservice.config.web.ApplicationContextProvider;
import com.example.demomicroservice.model.dto.communicate_kafka.employee.RegistryEmployeeConsumer;
import com.example.demomicroservice.model.dto.request.manager_personal.AccountManagerPersonalRegistryRequest;
import com.example.demomicroservice.model.dto.request.user.AccountLoginRequest;
import com.example.demomicroservice.model.dto.request.user.AccountUserRegistryRequest;
import com.example.demomicroservice.model.dto.response.user.AccountLoginResponse;
import com.example.demomicroservice.model.dto.response.user.AccountRegistryResponse;
import com.example.demomicroservice.model.entity.AppUser;
import com.example.demomicroservice.repository.IAppUserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.the.common.constant.Constants;
import com.the.common.constant.redis.RedisKey;
import com.the.common.enums.RoleEnum;
import com.the.common.exception.ErrorV1Exception;
import com.the.common.model.payload.response.BaseResponse;
import com.the.common.service.BaseService;
import com.the.common.system_message.SystemMessageCode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("AuthorService")
public class AuthorService extends BaseService {

  private final static Logger LOGGER = LoggerFactory.getLogger(AuthorService.class);
  @Resource
  private AppUserService appUserService;
  @Resource
  @Qualifier("IAppUserRepo")
  IAppUserRepo iAppUserRepo;
  @Resource
  @Qualifier("RoleService")
  private RoleService roleService;
  @Resource
  @Qualifier("JwtService")
  private JWTService jwtService;
  @Resource
  private AuthenticationManager authenticationManager;
  @Resource
  @Qualifier("ModelMapper")
  private ModelMapper modelMapper;
  @Resource(name = "RedisTemplate")
  private RedisTemplate<String, Object> redisTemplate;


  public BaseResponse<?> validateToken(HttpServletRequest request) {
    String token = jwtService.getTokenFromRequest(request);
    if (jwtService.validateToken(token)) {
      String userName = jwtService.getSubjectFromToken(token);
      if (userName != null) {
        try {
          UserDetails userDetails = appUserService.loadUserByUsername(userName);

          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              userDetails, token, userDetails.getAuthorities());

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);

          return responseV1(
              SystemMessageCode.AuthService.CODE_TOKEN_SUCCESS,
              SystemMessageCode.AuthService.MESSAGE_TOKEN_SUCCESS,
              AccountLoginResponse.builder().token(null).build()
          );

        } catch (Exception e) {
          return responseV1(
              SystemMessageCode.AuthService.CODE_TOKEN_FAIL,
              SystemMessageCode.AuthService.MESSAGE_TOKEN_FAIL,
              AccountLoginResponse.builder().token(null).build()
          );
        }
      }
    }
    return responseV1(
        SystemMessageCode.AuthService.CODE_TOKEN_FAIL,
        SystemMessageCode.AuthService.MESSAGE_TOKEN_FAIL,
        AccountLoginResponse.builder().token(null).build()
    );
  }

  @Transactional(rollbackFor = Exception.class)
  public BaseResponse<?> login(AccountLoginRequest request, BindingResult result) {
    try {
      hasError(result);
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      User user = (User) authentication.getPrincipal();
      String token = jwtService.createToken(user);
      redisTemplate.opsForValue().set(String.format(RedisKey.KEY_TOKEN_ACCOUNT, request.getUserName()), token);
      iAppUserRepo.updateToken(token, request.getUserName());
      return responseV1(SystemMessageCode.AuthService.CODE_LOGIN_SUCCESS,
          SystemMessageCode.AuthService.MESSAGE_LOGIN_SUCCESS
          , AccountLoginResponse.builder().token(token).build());
    } catch (Exception e) {
      return responseV1(
          SystemMessageCode.AuthService.CODE_LOGIN_ERROR,
          SystemMessageCode.AuthService.MESSAGE_LOGIN_ERROR,
          AccountLoginResponse.builder().token(null).build()
      );
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public BaseResponse<?> registryUser(AccountUserRegistryRequest request, BindingResult result) {
    hasError(result);
    validateUserRegistry(request);
    AppUser appUser = modelMapper.map(request, AppUser.class);
    String uuid = String.valueOf(UUID.randomUUID());
    appUser.setUuid(uuid);
    AppUser appUserSave = iAppUserRepo.save(appUser);
    roleService.saveRoleUser(appUserSave.getId(), RoleEnum.ROLE_USER.getCode());
    AccountRegistryResponse response = modelMapper.map(appUserSave, AccountRegistryResponse.class);
    response.setRoles(Stream.of(RoleEnum.ROLE_USER.getValue()).collect(Collectors.toList()));
    return responseV1(SystemMessageCode.AuthService.CODE_REGISTRY_SUCCESS,
        SystemMessageCode.AuthService.MESSAGE_REGISTRY_SUCCESS,
        response);
  }

  @Transactional(rollbackFor = Exception.class)
  public BaseResponse<?> registryManagerPersonal(AccountManagerPersonalRegistryRequest request, BindingResult result) {
    hasError(result);
    validateManagerPersonalRegistry(request);
    AppUser appUser = modelMapper.map(request, AppUser.class);
    String uuid = String.valueOf(UUID.randomUUID());
    appUser.setUuid(uuid);
    appUser.setToken("12345");
    AppUser appUserSave = iAppUserRepo.save(appUser);
    roleService.saveRoleUser(appUserSave.getId(), RoleEnum.ROLE_MANAGER_PERSONAL.getCode());
    roleService.saveRoleUser(appUserSave.getId(), RoleEnum.ROLE_EMPLOYEE.getCode());
    roleService.saveRoleUser(appUserSave.getId(), RoleEnum.ROLE_USER.getCode());
    AccountRegistryResponse response = modelMapper.map(appUserSave, AccountRegistryResponse.class);
    response.setRoles(Stream.of(RoleEnum.ROLE_MANAGER_PERSONAL.getValue(), RoleEnum.ROLE_EMPLOYEE.getValue(), RoleEnum.ROLE_USER.getValue()).collect(Collectors.toList()));
    return responseV1(SystemMessageCode.AuthService.CODE_REGISTRY_SUCCESS,
        SystemMessageCode.AuthService.MESSAGE_REGISTRY_SUCCESS,
        response);
  }

  //  @KafkaListener(topics = Topic.TOPIC_REGISTRY_EMPLOYEE)
  private void registryEmployee(ConsumerRecord<String, String> record) {
    ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
    String uuid;
    try {
      Header header = record.headers().lastHeader(Constants.AuthService.AUTHORIZATION);
      uuid = new String(record.headers().lastHeader(Constants.AuthService.UUID).value());
      if (header != null) {
        String token = new String(header.value());
        LOGGER.info("Token -------> : " + token);
        JWTService jwtService = applicationContext.getBean("JwtService", JWTService.class);
        IAppUserRepo iAppUserRepo = applicationContext.getBean("IAppUserRepo", IAppUserRepo.class);
        RoleService roleService = applicationContext.getBean("RoleService", RoleService.class);
        if (jwtService.validateToken(token)) {
          ObjectMapper objectMapperKafka = new ObjectMapper();
          String password = randomPassword();
          RegistryEmployeeConsumer employeeConsumer = objectMapperKafka.readValue(record.value(), RegistryEmployeeConsumer.class);
          AppUser appUser = new AppUser(uuid, employeeConsumer.getAccount(),
              password, employeeConsumer.getEmail(), employeeConsumer.getTelephone(), "");
          AppUser appUserSave = iAppUserRepo.save(appUser);
          roleService.saveRoleUser(appUserSave.getId(), RoleEnum.ROLE_EMPLOYEE.getCode());
          roleService.saveRoleUser(appUserSave.getId(), RoleEnum.ROLE_USER.getCode());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Registry employee ------> fail :" + e.getMessage());
    }
  }

  public void validateUserRegistry(AccountUserRegistryRequest request) {
    checkUserNameExist(request.getUserName());
    checkEmailExist(request.getGmail());
    checkPhoneExist(request.getPhoneNumber());
  }

  public void validateManagerPersonalRegistry(AccountManagerPersonalRegistryRequest request) {
    checkUserNameExist(request.getUserName());
    checkEmailExist(request.getGmail());
    checkPhoneExist(request.getPhoneNumber());
  }


  private void checkUserNameExist(String userName) {
    if (iAppUserRepo.findByUserName(userName) != null) {
      throw new ErrorV1Exception(messageV1Exception(
          SystemMessageCode.AuthService.CODE_REGISTRY_USER_NAME_EXIST,
          SystemMessageCode.AuthService.MESSAGE_REGISTRY_USER_NAME_EXIST
      ));
    }
  }

  private void checkEmailExist(String email) {
    if (iAppUserRepo.findAppUsersByGmail(email) != null) {
      throw new ErrorV1Exception(messageV1Exception(
          SystemMessageCode.AuthService.CODE_REGISTRY_GMAIL_EXIST,
          SystemMessageCode.AuthService.MESSAGE_REGISTRY_GMAIL_EXIST
      ));
    }
  }

  private void checkPhoneExist(String phone) {
    if (iAppUserRepo.findAppUsersByGmail(phone) != null) {
      throw new ErrorV1Exception(messageV1Exception(
          SystemMessageCode.AuthService.CODE_REGISTRY_PHONE_EXIST,
          SystemMessageCode.AuthService.MESSAGE_REGISTRY_PHONE_EXIST
      ));
    }
  }

  public List<AppUser> getAll() {
    return (List<AppUser>) iAppUserRepo.findAll();
  }
}
