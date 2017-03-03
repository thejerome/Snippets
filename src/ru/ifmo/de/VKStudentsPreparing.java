package ru.ifmo.de;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VKStudentsPreparing {

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        List<Student> students = new ArrayList<>();
        String studentInlined;
        while (!(studentInlined = br.readLine()).equals("fin")) {
            students.add(new Student(studentInlined));
        }
        PrintWriter pw = new PrintWriter(new FileWriter("vkstudents.sql"));


        //Substitute "s|https://([\w\d\.-]*annualreviews.org)|https://$1.de.ifmo.ru|i"
        students.stream().sequential().map(
                s -> "insert into tmp_students_vk values ('" + s.ln + "', '" + s.fn + "', '" + s.mn + "', '" + s.log + "', '" + s.pw + "');"

        ).forEach(pw::println);

        pw.flush();
        pw.close();
    }

    private static class Student {
        private final String ln;
        private final String fn;
        private final String mn;
        private final String log;
        private final String pw;

        public Student(String studentInlined) {
            String[] split = studentInlined.split(";");
            ln = split[0];
            fn = split[1];
            mn = split[2];
            log = split[3];
            pw = split[4];

        }
    }
}
