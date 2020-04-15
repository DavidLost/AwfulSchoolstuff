package de.david.mysql;

public class Encrypter {

    public static final int MAX_FACTOR = 500;
    public static final int MAX_SHIFT = 5000;

    public int factor;
    public int shift;

    public Encrypter(int factor, int shift) {
        this.factor = factor;
        this.shift = shift;
    }

    public Encrypter() {
        this.factor = (int)(Math.random()*MAX_FACTOR)+1;
        this.shift = (int)(Math.random()*MAX_SHIFT);
    }

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
