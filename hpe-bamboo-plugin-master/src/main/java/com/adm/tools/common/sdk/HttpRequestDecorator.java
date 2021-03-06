package com.adm.tools.common.sdk;

import com.adm.tools.common.SSEException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class HttpRequestDecorator {
    /**
     *
     * @param headers
     *            headrs to decorate with user info depending on the resource access level.
     * @param userName
     * @param resourceAccessLevel
     */
    public static void decorateHeaderWithUserInfo(
            final Map<String, String> headers,
            String userName,
            ResourceAccessLevel resourceAccessLevel) {

        if (headers == null) {
            throw new IllegalArgumentException("header must not be null");
        }
        //attach encrypted user name for protected and public resources
        if (resourceAccessLevel.equals(ResourceAccessLevel.PROTECTED)
                || resourceAccessLevel.equals(ResourceAccessLevel.PRIVATE)) {
            String userHeaderName = resourceAccessLevel.getUserHeaderName();
            String encryptedUserName = getDigestString("MD5", userName);
            if (userHeaderName != null) {
                headers.put(userHeaderName, encryptedUserName);
            }
        }
    }

    private static String getDigestString(String algorithmName, String dataToDigest) {

        try {
            MessageDigest md = MessageDigest.getInstance(algorithmName);
            byte[] digested = md.digest(dataToDigest.getBytes());

            return digestToString(digested);
        } catch (NoSuchAlgorithmException ex) {
            throw new SSEException(ex);
        }
    }

    /**
     * This method convert byte array to string regardless the charset
     *
     * @param b
     *            byte array input
     * @return the corresponding string
     */
    private static String digestToString(byte[] b) {

        StringBuilder result = new StringBuilder(128);
        for (byte aB : b) {
            result.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
        }

        return result.toString();
    }

}
