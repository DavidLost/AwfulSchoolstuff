package de.david.mysql;

public class Encrypter {

    public static String encryptSimpleAsymetric(String input, int factor, int shift) {
        String encrypted = "";
        for (char c : input.toCharArray()) {
            encrypted += (char)(c*factor+shift);
        }
        return encrypted;
    }

    public static String decryptSimpleAsymetric(String input, int factor, int shift) {
        String decrypted = "";
        for (char c : input.toCharArray()) {
            decrypted += (char)((c-shift)/factor);
        }
        return decrypted;
    }

    int factor = 387;//(int)(Math.random()*500)+1;
    int shift = 163;//(int)(Math.random()*5000);
    
    public String encryptSimpleAsymetric(String input) {
        String encrypted = "";
        for (char c : input.toCharArray()) {
            encrypted += (char)(c*factor+shift);
        }
        return encrypted;
    }

    public char encryptSimpleAsymetric(char input) {
        return (char)(input*factor+shift);
    }

    public String decryptSimpleAsymetric(String input) {
        String decrypted = "";
        for (char c : input.toCharArray()) {
            decrypted += (char)((c-shift)/factor);
        }
        return decrypted;
    }

    public char decryptSimpleAsymetric(char input) {
        return (char)((input-shift)/factor);
    }

    public void printKeys() {
        System.out.println("factor: "+factor);
        System.out.println("shift: "+shift);
    }

}
