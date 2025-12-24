package com.example.studentservice.util;

import com.example.studentservice.client.dto.UserCredential;
import com.example.studentservice.client.dto.UserRole;
import com.example.studentservice.domain.Student;
import com.example.studentservice.repository.StudentDao;

import org.apache.poi.xssf.model.SharedStrings;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SheetHandler extends DefaultHandler {
    private final SharedStrings sst;
    private final List<Student> students;
    private final List<UserCredential> users;

    private String lastContents;
    private boolean nextIsString;
    private int rowCount = 0;
    private int colCount = 0;
    private Student currentStudent;
    private UserCredential currentUser;
    private String duplicateEmail = null;

    // Cache to minimize database lookups
    private Set<String> emailCache = new HashSet<>();

    public SheetHandler(SharedStrings sst, List<Student> students, List<UserCredential> users, StudentDao studentDao) {
        this.sst = sst;
        this.students = students;
        this.users = users;

        // Prefetch all emails to avoid repeated DB calls
        studentDao.findAllEmails().forEach(emailCache::add);
    }

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        // Clear contents from previous element
        lastContents = "";

        // Handle row start
        if (name.equals("row")) {
            int rowNum = Integer.parseInt(attributes.getValue("r"));
            if (rowNum > 1) { // Skip header row
                rowCount++;
                colCount = 0;
                currentStudent = new Student();
                currentUser = new UserCredential();
                currentUser.setUserRole(UserRole.STUDENT);
            }
        }

        // Handle cell data type
        else if (name.equals("c")) {
            String cellType = attributes.getValue("t");
            nextIsString = cellType != null && cellType.equals("s");
        }
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
        // Process cell value at row > 1 (skip header)
        if (name.equals("v") && currentStudent != null) {
            String value;
            if (nextIsString) {
                int idx = Integer.parseInt(lastContents);
                value = sst.getItemAt(idx).getString(); // Changed from getItemAt to getEntryAt
            } else {
                value = lastContents;
            }

            // Process based on column number
            switch (colCount) {
                case 0: // Name
                    currentStudent.setName(value);
                    break;
                case 1: // Email
                    String email = value;
                    if (emailCache.contains(email)) {
                        duplicateEmail = email;
                    } else {
                        emailCache.add(email);
                        currentStudent.setEmail(email);
                        currentUser.setUsername(email);
                    }
                    break;
                case 2: // Password
                    currentUser.setPassword(value);
                    break;
            }
            colCount++;
        }
        // At end of row, add completed faculty and user to lists
        else if (name.equals("row") && currentStudent != null && currentStudent.getEmail() != null) {
            if (duplicateEmail == null) {
                students.add(currentStudent);
                users.add(currentUser);
            }
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        lastContents += new String(ch, start, length);
    }

    public int getRowCount() {
        return rowCount;
    }

    public String getDuplicateEmail() {
        return duplicateEmail;
    }
}