package br.com.cardmanager.utils;

import br.com.cardmanager.model.layout.LayoutFile;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class CardUtil {

    public static LayoutFile.Header parseHeader(String line) {
        // [01-29] -> index 0 to 29
        String name = line.substring(0, 29).trim();

        // [30-37] -> index 29 to 37
        String dateStr = line.substring(29, 37).trim();

        // [38-45] -> index 37 to 45
        String batchId = line.substring(37, 45).trim();

        // [46-51] -> index 45 to 51
        int count = Integer.parseInt(line.substring(45, 51).trim());

        return new LayoutFile.Header(name, LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd")), batchId, count);
    }

    public static LayoutFile.Information parseInformation(String line) {
        // [02-07] -> index 1 to 7 (Skipping the 'C' at index 0)
        int sequence = Integer.parseInt(line.substring(1, 7).trim());

        // [08-26] -> index 7 to 26
        String cardNumber = line.substring(7, 26).trim();

        return new LayoutFile.Information(sequence, cardNumber);
    }

    public static LayoutFile.Trailer parseTrailer(String line) {
        // [01-08] -> index 0 to 8
        String batchId = line.substring(0, 8).trim();

        // [09-14] -> index 8 to 14
        int count = Integer.parseInt(line.substring(8, 14).trim());

        return new LayoutFile.Trailer(batchId, count);
    }

    public static String generateEncryptedCardNumber(String cardNumber, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = cardNumber + salt;
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm error", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
