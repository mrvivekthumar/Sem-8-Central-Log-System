package com.example.facultyservice.Controller;

import com.example.facultyservice.Dao.FacultyDao;
import com.example.facultyservice.Model.Faculty;
import org.apache.poi.xssf.model.SharedStrings;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

public class SheetHandler extends DefaultHandler {
    
    private SharedStrings sst;
    private String lastContents;
    private boolean nextIsString;
    private List<Faculty> faculties;
    private List<Map<String, Object>> users;
    private FacultyDao facultyDao;
    
    private Faculty currentFaculty;
    private Map<String, Object> currentUser;
    private int currentColumn = -1;
    private int rowCount = 0;
    private String duplicateEmail = null;

    public SheetHandler(SharedStrings sst, List<Faculty> faculties, List<Map<String, Object>> users, FacultyDao facultyDao) {
        this.sst = sst;
        this.faculties = faculties;
        this.users = users;
        this.facultyDao = facultyDao;
    }

    public int getRowCount() {
        return rowCount;
    }

    public String getDuplicateEmail() {
        return duplicateEmail;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equals("row")) {
            currentColumn = -1;
            if (rowCount > 0) {
                currentFaculty = new Faculty();
                currentUser = new HashMap<>();
            }
        } else if (name.equals("c")) {
            String cellType = attributes.getValue("t");
            nextIsString = cellType != null && cellType.equals("s");
            currentColumn++;
        }
        lastContents = "";
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (nextIsString) {
            int idx = Integer.parseInt(lastContents);
            lastContents = sst.getItemAt(idx).getString();
            nextIsString = false;
        }

        if (name.equals("c") && rowCount > 0 && currentFaculty != null) {
            switch (currentColumn) {
                case 0:
                    currentFaculty.setName(lastContents);
                    currentUser.put("username", lastContents);
                    break;
                case 1:
                    String email = lastContents;
                    Faculty existing = facultyDao.findByEmail(email);
                    if (existing != null) {
                        duplicateEmail = email;
                        throw new SAXException("Duplicate email: " + email);
                    }
                    currentFaculty.setEmail(email);
                    break;
                case 2:
                    currentFaculty.setPassword(lastContents);
                    currentUser.put("password", lastContents);
                    break;
                case 3:
                    currentFaculty.setDepartment(lastContents);
                    break;
            }
        }

        if (name.equals("row")) {
            if (rowCount > 0 && currentFaculty != null && currentFaculty.getEmail() != null) {
                faculties.add(currentFaculty);
                currentUser.put("userRole", "FACULTY");
                users.add(currentUser);
            }
            rowCount++;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        lastContents += new String(ch, start, length);
    }
}
