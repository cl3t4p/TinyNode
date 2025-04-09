package com.cl3t4p.TinyNode.db.mapper;


import com.cl3t4p.TinyNode.annotation.SQLDInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SQLMapper {


    public static <A> void serializeSQL(PreparedStatement statement, A object, List<String> fields) throws SQLException {
        try {
            int cursor = 0;

            for (String field_name : fields) {
                Field field = object.getClass().getDeclaredField(field_name);

                cursor++;
                field.setAccessible(true);

                //TODO test byte[] to blob
                statement.setObject(cursor,field.get(object));
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }



    public static <A> A deserializeSQL(ResultSet result, Class<A> clazz) throws SQLException {
        A object;

        try {
            object = clazz.getDeclaredConstructor().newInstance();
            for (Field field : object.getClass().getDeclaredFields()) {
                String name = field.getName();
                boolean ignore = false;

                for (Annotation annotation : field.getAnnotations())
                    if (annotation instanceof SQLDInfo info) {
                        name = info.sql_name().isEmpty() ? name : info.sql_name();
                        ignore = info.ignore();
                        break;
                    }
                if (ignore)
                    continue;

                field.setAccessible(true);

                //TODO test blob to byte[]
                field.set(object, result.getObject(name));
            }
        }catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e){
            throw new RuntimeException(e);
        }

        return object;
    }

}
