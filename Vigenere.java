import java.util.*;

public class Vigenere {
    static char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};


    public static String cipherMaker(String key, String plainText){
        key = key.toUpperCase();
        plainText = plainText.trim().replaceAll("\\s","").toUpperCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < plainText.length(); i++) {
            sb.append(alphabet[(plainText.charAt(i)-'A' + key.charAt(i%key.length())-'A') % alphabet.length]);
        }
        return sb.toString();
    }

    public static String decode(String key, String code){
        key = key.toUpperCase();
        code = code.trim().replaceAll("\\s","").toUpperCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            sb.append(alphabet[ Math.abs((key.charAt(i%key.length())-'A'- code.charAt(i)-'A' )) % alphabet.length]);
        }
        return sb.toString();
    }

}

