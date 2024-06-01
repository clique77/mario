package main.com.kulikov.Repository.excpetion;

public class EntityNotFoundException extends RuntimeException{
  public EntityNotFoundException(String message) {
    super(message);
  }
}