package com.example.reports;

/**
 * Viewer depends on Report abstraction.
 */
public class ReportViewer {

    public void open(Report report, User user) {
        report.display(user);
    }
}
