package de.david.encryption;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        this.factor = random.nextInt(MAX_FACTOR)+1;
        this.shift = random.nextInt(MAX_SHIFT);
        System.out.println("sucessfully created encrypter with factor: "+factor+" and shift: "+shift);
    }

    public static String encryptSimpleAsymetric(String input, int factor, int shift) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            encrypted.append((char) (c * factor + shift));
        }
        return encrypted.toString();
    }

    public static String decryptSimpleAsymetric(String input, int factor, int shift) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            decrypted.append((char) ((c - shift) / factor));
        }
        return decrypted.toString();
    }
    
    public String encryptSimpleAsymetric(String input) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            encrypted.append((char) (c * factor + shift));
        }
        return encrypted.toString();
    }

    public char encryptSimpleAsymetric(char input) {
        return (char)(input*factor+shift);
    }

    public String decryptSimpleAsymetric(String input) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : input.toCharArray()) {
            decrypted.append((char) ((c - shift) / factor));
        }
        return decrypted.toString();
    }

    public char decryptSimpleAsymetric(char input) {
        return (char)((input-shift)/factor);
    }

}
