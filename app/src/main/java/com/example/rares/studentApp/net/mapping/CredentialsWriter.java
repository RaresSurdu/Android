package com.studentApp.net.mapping;

import android.util.JsonWriter;

import com.studentApp.content.User;

import java.io.IOException;

import static com.studentApp.net.mapping.Api.Auth.PASSWORD;
import static com.studentApp.net.mapping.Api.Auth.USERNAME;


public class CredentialsWriter implements ResourceWriter<User, JsonWriter> {
  @Override
  public void write(User user, JsonWriter writer) throws IOException {
    writer.beginObject();
    {
      writer.name(USERNAME).value(user.getUsername());
      writer.name(PASSWORD).value(user.getPassword());
    }
    writer.endObject();
  }
}