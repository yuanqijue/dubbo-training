package com.xksh.common.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

/**
 * Created by aaron on 2017/9/28.
 */
public class JsonSerialization {

    ObjectMapper objectMapper = new ObjectMapper();

    public OutputStream serialize(Object obj) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            objectMapper.writeValue(out, obj);
        } catch (IOException var5) {
            var5.printStackTrace();
        }
        String json = "";

        try {
            json = new String(out.toByteArray(), "UTF-8");
            System.out.println(json);
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }
        return out;
    }

    public <T> T deserialize(Class<T> cls, InputStream input) {
        try {
            return objectMapper.readValue(input, cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        User user = new User("Aaron Wang", 28);
        JsonSerialization jsonSerialization = new JsonSerialization();
        jsonSerialization.serialize(user);
        String json = "{\"name\":\"Joseph Zhang\",\"age\":28}";
        ByteArrayInputStream input = new ByteArrayInputStream(json.getBytes());
        User user1 = jsonSerialization.deserialize(User.class, input);
        System.out.println(user1.toString());
    }
}
