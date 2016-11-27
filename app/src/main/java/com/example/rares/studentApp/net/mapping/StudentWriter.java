package com.studentApp.net.mapping;

import android.util.JsonWriter;

import com.studentApp.content.Student;

import java.io.IOException;

import static com.studentApp.net.mapping.Api.Student.*;


public class StudentWriter implements ResourceWriter<Student, JsonWriter>{
  @Override
  public void write(Student student, JsonWriter writer) throws IOException {
    writer.beginObject();
    {
      if (student.getId() != null) {
        writer.name(_ID).value(student.getId());
      }
      writer.name(TEXT).value(student.getText());
      writer.name(STATUS).value(student.getStatus().name());
      if (student.getUpdated() > 0) {
        writer.name(UPDATED).value(student.getUpdated());
      }
      if (student.getUserId() != null) {
        writer.name(USER_ID).value(student.getUserId());
      }
      if (student.getVersion() > 0) {
        writer.name(VERSION).value(student.getVersion());
      }
    }
    writer.endObject();
  }
}
