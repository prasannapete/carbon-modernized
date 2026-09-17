package com.pcpl.carbon.shell_analytics_service.Mailer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EmailParameters {
    private String subject;
    private String toEmail;
    private String message;
    private List<String> ccEmails = new ArrayList<>();
    private List<HashMap<String, String>> attachments = new ArrayList<>();
    private HashMap<String, String> customArgs = new HashMap<>();

    public EmailParameters() {

    }

    public EmailParameters(String subject, String toEmail, String message, List<HashMap<String, String>> attachments, HashMap<String, String> customArgs) {
        this.subject = subject;
        this.toEmail = toEmail;
        this.message = message;
        this.attachments = attachments;
        this.customArgs = customArgs;
    }

    public EmailParameters(String subject, String toEmail, String message) {
        this.subject = subject;
        this.toEmail = toEmail;
        this.message = message;
    }

    public EmailParameters(String subject, String toEmail, String message, List<String> ccEmails, List<HashMap<String, String>> attachmentPaths) {
        this.subject = subject;
        this.toEmail = toEmail;
        this.message = message;
        this.ccEmails = ccEmails;
        this.attachments = attachmentPaths;
    }

    public EmailParameters(String subject, String toEmail, String message, List<HashMap<String, String>> attachmentPaths) {
        this.subject = subject;
        this.toEmail = toEmail;
        this.message = message;
        this.attachments = attachmentPaths;
    }

    public EmailParameters(String subject, String toEmail, String message, List<String> ccEmails, List<HashMap<String, String>> attachmentPaths, HashMap<String, String> customArgs) {
        this.subject = subject;
        this.toEmail = toEmail;
        this.message = message;
        this.ccEmails = ccEmails;
        this.attachments = attachmentPaths;
        this.customArgs = customArgs;
    }

    public EmailParameters(String subject, String toEmail, String message, HashMap<String, String> customArgs) {
        this.subject = subject;
        this.toEmail = toEmail;
        this.message = message;
        this.customArgs = customArgs;
    }

    public EmailParameters(String subject, String toEmail, List<String> ccEmails, String message) {
        this.subject = subject;
        this.toEmail = toEmail;
        this.message = message;
        this.ccEmails = ccEmails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getCcEmails() {
        return ccEmails;
    }

    public void setCcEmails(List<String> ccEmails) {
        this.ccEmails = ccEmails;
    }

    public List<HashMap<String, String>> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<HashMap<String, String>> attachments) {
        this.attachments = attachments;
    }

    public HashMap<String, String> getCustomArgs() {
        return customArgs;
    }

    public void setCustomArgs(HashMap<String, String> customArgs) {
        this.customArgs = customArgs;
    }
}
