package com.studentmanagement.communicator;

import android.os.Bundle;

public interface Communicator {

    public void addStudent(Bundle bundleData);
    public void updateStudent(Bundle bundleData);
    public void updateStudentList(Bundle bundleData);

}
