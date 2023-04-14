package com.example.demomicroservice.config.web;

import com.obys.common.config.i18n.SmartLocaleResolver;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The type Web mvc config.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  /**
   * Locale resolver locale resolver.
   *
   * @return the locale resolver
   */
  @Bean
  public LocaleResolver localeResolver() {
    return new SmartLocaleResolver();
  }

  /**
   * Gets message source.
   *
   * @return the message source
   */
  @Bean(name = "messageSource")
  public MessageSource getMessageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames(
        "file:i18n/common/common",
        "file:i18n/messages/messages",
        "file:i18n/validator/validator",
        "file:i18n/field/field");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  /**
   * Validator local validator factory bean.
   *
   * @return the local validator factory bean
   */
  @Bean
  public LocalValidatorFactoryBean validator() {
    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(getMessageSource());
    return bean;
  }

  @Override
  public Validator getValidator() {
    return validator();
  }

  @Bean(name = "ModelMapper")
  public ModelMapper getModelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setMatchingStrategy(MatchingStrategies.STRICT);
    return modelMapper;
  }
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}

