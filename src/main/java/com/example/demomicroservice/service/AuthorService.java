package com.example.demomicroservice.service;

import com.example.demomicroservice.config.web.ApplicationContextProvider;
import com.example.demomicroservice.model.dto.communicate_kafka.employee.RegistryEmployeeConsumer;
import com.example.demomicroservice.model.dto.request.user.AccountLoginRequest;
import com.example.demomicroservice.model.dto.request.user.AccountRegistryRequest;
import com.example.demomicroservice.model.dto.response.user.AccountLoginResponse;
import com.example.demomicroservice.model.dto.response.user.AccountRegistryResponse;
import com.example.demomicroservice.model.entity.AppUser;
import com.example.demomicroservice.repository.IAppUserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obys.common.constant.Constants;
import com.obys.common.enums.RoleEnum;
import com.obys.common.exception.ErrorV1Exception;
import com.obys.common.kafka.Topic;
import com.obys.common.model.payload.response.BaseResponse;
import com.obys.common.service.BaseService;
import com.obys.common.system_message.SystemMessageCode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("AuthorService")
public class AuthorService extends BaseService {

  private final static Logger LOGGER = LoggerFactory.getLogger(AuthorService.class);
  @Resource
  private KafkaTemplate<String, String> kafkaTemplate;
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
  @Resource
  private ObjectMapper objectMapper;

  @Transactional(rollbackFor = Exception.class)
  public BaseResponse<?> login(AccountLoginRequest request, BindingResult result) {
    try {
      hasError(result);
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      User user = (User) authentication.getPrincipal();
      return responseV1(SystemMessageCode.AuthService.CODE_LOGIN_SUCCESS,
          SystemMessageCode.AuthService.MESSAGE_LOGIN_SUCCESS
          , AccountLoginResponse.builder().token(jwtService.createToken(user)).build());
    } catch (Exception e) {
      return responseV1(
          SystemMessageCode.AuthService.CODE_LOGIN_ERROR,
          SystemMessageCode.AuthService.MESSAGE_LOGIN_ERROR,
          AccountLoginResponse.builder().token(null).build()
      );
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public BaseResponse<?> registryUser(AccountRegistryRequest request, BindingResult result) {
    hasError(result);
    validateRegistry(request);
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

  @KafkaListener(topics = Topic.TOPIC_REGISTRY_EMPLOYEE)
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
              password, employeeConsumer.getEmail(), employeeConsumer.getTelephone());
          AppUser appUserSave = iAppUserRepo.save(appUser);
          roleService.saveRoleUser(appUserSave.getId(), RoleEnum.ROLE_EMPLOYEE.getCode());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Registry employee ------> fail :" + e.getMessage());
    }
  }

  public void validateRegistry(AccountRegistryRequest request) {
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
