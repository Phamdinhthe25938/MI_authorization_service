package com.example.demomicroservice.service;

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
import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class AuthorService extends BaseService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthorService.class);
    @Resource
    IAppUserRepo iAppUserRepo;

    @Resource
    private RoleService roleService;
    @Resource
    private JWTService jwtService;

    private AuthenticationManager authenticationManager;

    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ObjectMapper objectMapper;

    public AuthorService(@NonNull @Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

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
       try {
           String token = getTokenFromRequestUserKafka(record.headers().headers(Constants.AuthService.AUTHORIZATION).toString());
           if (jwtService.validateToken(token)) {
               String password = randomPassword();
               String uuid = String.valueOf(UUID.randomUUID());
               RegistryEmployeeConsumer employeeConsumer = objectMapper.readValue(record.value(), RegistryEmployeeConsumer.class);
               AppUser appUser = new AppUser(uuid, employeeConsumer.getAccount(),
                       password, employeeConsumer.getEmail(), employeeConsumer.getTelephone());
               AppUser appUserSave = iAppUserRepo.save(appUser);
               roleService.saveRoleUser(appUserSave.getId(), RoleEnum.ROLE_EMPLOYEE.getCode());
           }
       }catch (Exception e) {
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