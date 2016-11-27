package com.studentApp.net.mapping;

import android.util.JsonReader;
import android.util.Log;

import com.studentApp.content.Student;

import java.io.IOException;

import static com.studentApp.net.mapping.Api.Student.*;


public class StudentReader implements ResourceReader<Student, JsonReader> {
  private static final String TAG = com.studentApp.net.mapping.StudentReader.class.getSimpleName();

  @Override
  public Student read(JsonReader reader) throws IOException {
    Student student = new Student();
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      if (name.equals(_ID)) {
        student.setId(reader.nextString());
      } else if (name.equals(TEXT)) {
        student.setText(reader.nextString());
      } else if (name.equals(STATUS)) {
        student.setStatus(com.studentApp.content.Student.Status.valueOf(reader.nextString()));
      } else if (name.equals(UPDATED)) {
        student.setUpdated(reader.nextLong());
      } else if (name.equals(USER_ID)) {
        student.setUserId(reader.nextString());
      } else if (name.equals(VERSION)) {
        student.setVersion(reader.nextInt());
      } else {
        reader.skipValue();
        Log.w(TAG, String.format("Student property '%s' ignored", name));
      }
    }
    reader.endObject();
    return student;
  }
}
