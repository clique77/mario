package com.kulikov;


import com.kulikov.Repository.UserRepository;
import com.kulikov.Service.AuthenticationService;
import com.kulikov.Service.UserService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration class for the Spring application context.
 */
@Configuration
@ComponentScan("main.com.kulikov")
public class ApplicationConfig {

  @Autowired
  private UserRepository userRepository;

  /**
   * Configures and provides the validator bean for validation purposes.
   *
   * @return the configured validator bean
   */
  @Bean
  public Validator validator() {
    ValidatorFactory factory = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(new ParameterMessageInterpolator())
        .buildValidatorFactory();
    return factory.getValidator();
  }

  /**
   * Configures and provides the UserService bean as the primary bean for dependency injection.
   *
   * @param userRepository the UserRepository bean
   * @param validator      the Validator bean
   * @return the configured UserService bean
   */
  @Bean
  public UserService userService(UserRepository userRepository, Validator validator) {
    return new UserService(userRepository, validator);
  }

  @Bean
  public AuthenticationService authenticationService(UserRepository userRepository) {
    return new AuthenticationService(userRepository);
  }
}