package org.gluu.agama.update;

import io.jans.as.common.model.common.User;
import io.jans.as.common.service.common.EncryptionService;
import io.jans.as.common.service.common.UserService;
import io.jans.orm.exception.operation.EntryNotFoundException;
import io.jans.service.cdi.util.CdiUtil;
import io.jans.util.StringHelper;

import org.gluu.agama.user.UsernameUpdate;
import io.jans.agama.engine.script.LogUtils;
import java.io.IOException;
import io.jans.as.common.service.common.ConfigurationService;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.regex.Pattern;
import org.gluu.agama.smtp.SendEmailTemplateEn;
import org.gluu.agama.smtp.SendEmailTemplateAr;
import org.gluu.agama.smtp.SendEmailTemplateEs;
import org.gluu.agama.smtp.SendEmailTemplateFr;
import org.gluu.agama.smtp.SendEmailTemplateId;
import org.gluu.agama.smtp.SendEmailTemplatePt;

import io.jans.model.SmtpConfiguration;
import io.jans.service.MailService;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jans.as.server.service.token.TokenService;
import io.jans.as.server.model.common.AuthorizationGrant;
import io.jans.as.server.model.common.AuthorizationGrantList;
import io.jans.as.server.model.common.AbstractToken;

public class JansUsernameUpdate extends UsernameUpdate {

    private static final String MAIL = "mail";
    private static final String UID = "uid";
    private static final String DISPLAY_NAME = "displayName";
    private static final String GIVEN_NAME = "givenName";
    private static final String LAST_NAME = "sn";
    private static final String PASSWORD = "userPassword";
    private static final String INUM_ATTR = "inum";
    private static final String EXT_ATTR = "jansExtUid";
    private static final String USER_STATUS = "jansStatus";
    private static final String EXT_UID_PREFIX = "github:";
    private static final String LANG = "lang";
    private static final SecureRandom RAND = new SecureRandom();

    private static JansUsernameUpdate INSTANCE = null;

    public JansUsernameUpdate() {
    }

    public static synchronized JansUsernameUpdate getInstance() {
        if (INSTANCE == null)
            INSTANCE = new JansUsernameUpdate();

        return INSTANCE;
    }

    // validate token starts here
    public static Map<String, Object> validateBearerToken(String access_token) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (access_token == null || access_token.trim().isEmpty()) {
                result.put("valid", false);
                result.put("errorMessage", "Access token is missing");
                return result;
            }

            // Get AuthorizationGrantList service
            AuthorizationGrantList authorizationGrantList = CdiUtil.bean(AuthorizationGrantList.class);
            if (authorizationGrantList == null) {
                result.put("valid", false);
                result.put("errorMessage", "Service not available");
                return result;
            }

            // Get the grant for this token
            AuthorizationGrant grant = authorizationGrantList.getAuthorizationGrantByAccessToken(access_token.trim());

            if (grant == null) {
                // Token not found
                result.put("valid", false);
                result.put("errorMessage", "Access token is invalid or expired");
                return result;
            }

            // Get the actual token object to check if it's valid (not expired)
            AbstractToken tokenObject = grant.getAccessToken(access_token.trim());

            // Check if token is active (exists and is valid)
            boolean isActive = tokenObject != null && tokenObject.isValid();

            if (isActive) {
                result.put("valid", true);
            } else {
                result.put("valid", false);
                result.put("errorMessage", "Access token is invalid or expired");
            }

        } catch (Exception e) {
            result.put("valid", false);
            result.put("errorMessage", "Access token is invalid or expired");
        }

        return result;
    }

    // validate token ends here

    public boolean passwordPolicyMatch(String userPassword) {
        String regex = '''^(?=.*[!@#$^&*])[A-Za-z0-9!@#$^&*]{6,}$'''
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(userPassword).matches();
    }

    public boolean usernamePolicyMatch(String userName) {
        // Regex: Only alphabets (uppercase and lowercase), minimum 1 character
        String regex = '''^[A-Za-z]+$''';
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(userName).matches();
    }

    public Map<String, String> getUserEntityByMail(String email) {
        User user = getUser(MAIL, email);
        boolean local = user != null;
        LogUtils.log("There is % local account for %", local ? "a" : "no", email);

        if (local) {
            String uid = getSingleValuedAttr(user, UID);
            String inum = getSingleValuedAttr(user, INUM_ATTR);
            String name = getSingleValuedAttr(user, GIVEN_NAME);

            if (name == null) {
                name = getSingleValuedAttr(user, DISPLAY_NAME);
                if (name == null && email != null && email.contains("@")) {
                    name = email.substring(0, email.indexOf("@"));
                }
            }

            // Creating a truly modifiable map
            Map<String, String> userMap = new HashMap<>();
            userMap.put(UID, uid);
            userMap.put(INUM_ATTR, inum);
            userMap.put("name", name);
            userMap.put("email", email);

            return userMap;
        }

        return new HashMap<>();
    }

    public Map<String, String> getUserEntityByUsername(String username) {
        User user = getUser(UID, username);
        boolean local = user != null;
        LogUtils.log("There is % local account for %", local ? "a" : "no", username);

        if (local) {
            String email = getSingleValuedAttr(user, MAIL);
            String inum = getSingleValuedAttr(user, INUM_ATTR);
            String name = getSingleValuedAttr(user, GIVEN_NAME);
            String uid = getSingleValuedAttr(user, UID); // Define uid properly
            String displayName = getSingleValuedAttr(user, DISPLAY_NAME);
            String givenName = getSingleValuedAttr(user, GIVEN_NAME);
            String sn = getSingleValuedAttr(user, LAST_NAME);
            String lang = getSingleValuedAttr(user, LANG);

            if (name == null) {
                name = getSingleValuedAttr(user, DISPLAY_NAME);
                if (name == null && email != null && email.contains("@")) {
                    name = email.substring(0, email.indexOf("@"));
                }
            }
            // Creating a modifiable HashMap directly
            Map<String, String> userMap = new HashMap<>();
            userMap.put(UID, uid);
            userMap.put(INUM_ATTR, inum);
            userMap.put("name", name);
            userMap.put("email", email);
            userMap.put(DISPLAY_NAME, displayName);
            userMap.put(LAST_NAME, sn);
            userMap.put(LANG, lang);

            return userMap;
        }

        return new HashMap<>();
    }

    public String addNewUser(Map<String, String> profile) throws Exception {
        Set<String> attributes = Set.of("uid", "mail", "displayName", "givenName", "sn", "userPassword");
        User user = new User();

        attributes.forEach(attr -> {
            String val = profile.get(attr);
            if (StringHelper.isNotEmpty(val)) {
                user.setAttribute(attr, val);
            }
        });

        UserService userService = CdiUtil.bean(UserService.class);
        user = userService.addUser(user, true); // Set user status active

        if (user == null) {
            throw new EntryNotFoundException("Added user not found");
        }

        return getSingleValuedAttr(user, INUM_ATTR);
    }

    public String updateUser(Map<String, String> profile) throws Exception {
        String inum = profile.get(INUM_ATTR);
        User user = getUser(INUM_ATTR, inum);

        if (user == null) {
            throw new EntryNotFoundException("User not found for inum: " + inum);
        }

        // 🔒 Preserve current email and lang
        String currentEmail = getSingleValuedAttr(user, MAIL);
        String currentLanguage = getSingleValuedAttr(user, LANG);

        // ✅ Update UID if provided
        String newUid = profile.get(UID);
        if (StringHelper.isNotEmpty(newUid)) {
            user.setAttribute(UID, newUid);
            user.setUserId(newUid);
        }

        // ✅ Always preserve email and lang
        if (StringHelper.isNotEmpty(currentEmail)) {
            user.setAttribute(MAIL, currentEmail);
        }
        if (StringHelper.isNotEmpty(currentLanguage)) {
            user.setAttribute(LANG, currentLanguage);
        }

        // ✅ Save the user
        UserService userService = CdiUtil.bean(UserService.class);
        user = userService.updateUser(user);

        if (user == null) {
            throw new EntryNotFoundException("Updated user not found");
        }

        return getSingleValuedAttr(user, INUM_ATTR);
    }

    public Map<String, String> getUserEntityByInum(String inum) {
        User user = getUser(INUM_ATTR, inum);
        boolean local = user != null;
        LogUtils.log("There is % local account for %", local ? "a" : "no", inum);

        if (local) {
            String email = getSingleValuedAttr(user, MAIL);
            // String inum = getSingleValuedAttr(user, INUM_ATTR);
            String name = getSingleValuedAttr(user, GIVEN_NAME);
            String uid = getSingleValuedAttr(user, UID); // Define uid properly
            String displayName = getSingleValuedAttr(user, DISPLAY_NAME);
            String givenName = getSingleValuedAttr(user, GIVEN_NAME);
            String sn = getSingleValuedAttr(user, LAST_NAME);
            String userPassword = getSingleValuedAttr(user, PASSWORD);
            String lang = getSingleValuedAttr(user, LANG);

            if (name == null) {
                name = getSingleValuedAttr(user, DISPLAY_NAME);
                if (name == null && email != null && email.contains("@")) {
                    name = email.substring(0, email.indexOf("@"));
                }
            }
            // Creating a modifiable HashMap directly
            Map<String, String> userMap = new HashMap<>();
            userMap.put(UID, uid);
            userMap.put("userId", uid);
            userMap.put(INUM_ATTR, inum);
            userMap.put("name", name);
            userMap.put("email", email);
            userMap.put(DISPLAY_NAME, displayName);
            userMap.put(LAST_NAME, sn);
            userMap.put(PASSWORD, userPassword);
            userMap.put(LANG, lang);

            return userMap;
        }

        return new HashMap<>();
    }

    private String getSingleValuedAttr(User user, String attribute) {
        Object value = null;
        if (attribute.equals(UID)) {
            // user.getAttribute("uid", true, false) always returns null :(
            value = user.getUserId();
        } else {
            value = user.getAttribute(attribute, true, false);
        }
        return value == null ? null : value.toString();

    }

    private User getUser(String attributeName, String value) {
        UserService userService = CdiUtil.bean(UserService.class);
        return userService.getUserByAttribute(attributeName, value, true);
    }

    public boolean sendUsernameUpdateEmail(String to, String newUsername, String lang) {
        try {
            // Fetch SMTP configuration
            ConfigurationService configService = CdiUtil.bean(ConfigurationService.class);
            SmtpConfiguration smtpConfig = configService.getConfiguration().getSmtpConfiguration();

            if (smtpConfig == null) {
                LogUtils.log("SMTP configuration is missing.");
                return false;
            }

            // Fetch givenName from user profile
            String givenName = null;
            try {
                User user = getUser(MAIL, to); // using your existing getUser() helper
                if (user != null) {
                    givenName = getSingleValuedAttr(user, GIVEN_NAME);
                }
            } catch (Exception ex) {
                LogUtils.log("Error fetching givenName for user with email %: %", to, ex.getMessage());
            }

            if (givenName == null || givenName.isEmpty()) {
                givenName = "User";
            }

            // Preferred language from user profile or fallback to English
            String preferredLang = (lang != null && !lang.isEmpty())
                    ? lang.toLowerCase()
                    : "en";


            // Select correct template
            Map<String, String> templateData;
            switch (preferredLang) {
                case "ar":
                    templateData = SendEmailTemplateAr.get(newUsername);
                    break;
                case "es":
                    templateData = SendEmailTemplateEs.get(newUsername);
                    break;
                case "fr":
                    templateData = SendEmailTemplateFr.get(newUsername);
                    break;
                case "id":
                    templateData = SendEmailTemplateId.get(newUsername);
                    break;
                case "pt":
                    templateData = SendEmailTemplatePt.get(newUsername);
                    break;
                default:
                    templateData = SendEmailTemplateEn.get(givenName, newUsername);
                    break;
            }

            String subject = templateData.get("subject");
            String htmlBody = templateData.get("body");
            String textBody = htmlBody.replaceAll("\\<.*?\\>", ""); // crude HTML → text

            // Send signed email
            MailService mailService = CdiUtil.bean(MailService.class);
            boolean sent = mailService.sendMailSigned(
                    smtpConfig.getFromEmailAddress(),
                    smtpConfig.getFromName(),
                    to,
                    null,
                    subject,
                    textBody,
                    htmlBody
            );

            if (sent) {
                LogUtils.log("Localized username update email sent successfully to %  (Given name: %)", to, givenName);
            } else {
                LogUtils.log("Failed to send localized username update email to %  (Given name: %)", to, givenName);
            }

            return sent;

        } catch (Exception e) {
            LogUtils.log("Failed to send username update email: %", e.getMessage());
            return false;
        }
    }


    // Helper method to fetch SMTP configuration
    private SmtpConfiguration getSmtpConfiguration() {
        ConfigurationService configurationService = CdiUtil.bean(ConfigurationService.class);
        return configurationService.getConfiguration().getSmtpConfiguration();
    }
}