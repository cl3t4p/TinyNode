package com.cl3t4p.TinyNode.db.mapper;

import com.cl3t4p.TinyNode.annotation.SQLDInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** SQLMapper - A utility class for serializing and deserializing objects to and from SQL. */
public class SQLMapper {

  /**
   * Serializes an object into a SQL PreparedStatement.
   *
   * @param statement The PreparedStatement to serialize the object into.
   * @param object The object to serialize.
   * @param fields The list of field names to serialize.
   * @param <A> The type of the object.
   * @throws SQLException If an SQL error occurs during serialization.
   */
  public static <A> void serializeSQL(PreparedStatement statement, A object, List<String> fields)
      throws SQLException {
    try {
      int cursor = 0;

      for (String field_name : fields) {
        Field field = object.getClass().getDeclaredField(field_name);

        cursor++;
        field.setAccessible(true);

        statement.setObject(cursor, field.get(object));
      }

    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Deserializes a SQL ResultSet into an object of the specified class.
   *
   * @param result The ResultSet to deserialize.
   * @param clazz The class of the object to deserialize into.
   * @param <A> The type of the object.
   * @return The deserialized object or null if there is no object
   * @throws SQLException If an SQL error occurs during deserialization.
   */
  public static <A> A deserializeSQL(ResultSet result, Class<A> clazz) throws SQLException {
    if (!result.isBeforeFirst()) {
      return null;
    }
    A object;

    try {
      object = clazz.getDeclaredConstructor().newInstance();
      for (Field field : object.getClass().getDeclaredFields()) {
        String name = field.getName();
        boolean ignore = true;

        for (Annotation annotation : field.getAnnotations())
          if (annotation instanceof SQLDInfo info) {
            name = info.sql_name().isEmpty() ? name : info.sql_name();
            ignore = info.ignore();
            break;
          } else {
            break;
          }
        if (ignore) continue;

        field.setAccessible(true);

        field.set(object, result.getObject(name));
      }
    } catch (NoSuchMethodException
        | InvocationTargetException
        | InstantiationException
        | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    return object;
  }
}
