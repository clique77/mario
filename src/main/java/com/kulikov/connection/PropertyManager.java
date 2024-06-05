package com.kulikov.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The PropertyManager class manages application properties.
 */
public class PropertyManager {

  /** The properties loaded from the application.properties file. */
  private static final Properties PROPERTIES = new Properties();

  static {
    loadProperties();
  }

  /**
   * Retrieves the value associated with the specified key from the properties.
   *
   * @param key the key whose associated value is to be retrieved
   * @return the value associated with the specified key, or null if no value is present
   */
  public static String get(String key) {
    return PROPERTIES.getProperty(key);
  }

  /**
   * Loads the application properties from the application.properties file.
   */
  private static void loadProperties() {
    try (InputStream applicationProperties = PropertyManager.class.getClassLoader()
        .getResourceAsStream("com.kulikov/mariobros/main/resources/application.properties")) {
      PROPERTIES.load(applicationProperties);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Private constructor to prevent instantiation of the class. */
  private PropertyManager() {
  }
}
