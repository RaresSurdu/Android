package com.studentApp.net.mapping;

import com.studentApp.content.Student;

import org.json.JSONObject;

import static com.studentApp.net.mapping.Api.Student.*;


public class StudentJsonObjectReader implements ResourceReader<Student
        , JSONObject> {
  private static final String TAG = com.studentApp.net.mapping.StudentJsonObjectReader.class.getSimpleName();

  @Override
  public Student read(JSONObject obj) throws Exception {
    Student student = new Student();
    student.setId(obj.getString(_ID));
    student.setText(obj.getString(TEXT));
    student.setUpdated(obj.getLong(UPDATED));
    student.setStatus(Bill.Status.valueOf(obj.getString(STATUS)));
    student.setUserId(obj.getString(USER_ID));
    student.setVersion(obj.getInt(VERSION));
    return student;
  }
}
