package main.com.kulikov.Repository.excpetion;

public class EntityUpdateException  extends RuntimeException{
  public EntityUpdateException(String message) {
    super(message);
  }
}
