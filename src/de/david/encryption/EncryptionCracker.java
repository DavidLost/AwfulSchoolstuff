package de.david.encryption;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EncryptionCracker {

    public static final int AVERAGE_ASCII_VALUE = 105;
    public static final int AVERAGE_ASCII_RANGE = 88;
    public static final int LOWEST_USED_CHARACTER = KeyEvent.VK_SPACE;
    public static final int HIGHEST_USED_CHARACTER = 128;

    public String[] wordsList = getWordList();

    public String crackSimpleAsymetricEncryption(Encrypter encrypter, String encryptedString) {

        StringBuilder decryptedString = new StringBuilder();
        for (char c : encryptedString.toCharArray()) {                      //decrypt every char individually, because they werde encrypted individually as well
            for (int i = 0; i < 1114112; i++) {                             //try every single character in the unicode table
                char encrypted = encrypter.encryptSimpleAsymetric((char)i); //get encrypted version of the char
                if (encrypted == c) {                                       //if the both encrypted characters match, the char with value i must be the original char.
                    decryptedString.append((char)i);                        //so it is addet to the solution string
                    break;
                }
            }
        }
        return decryptedString.toString();
    }

    public String[] crackSimpleAsymetricEncryptionWithoutEncrypter(String encryptedString) {

        char[] chars = encryptedString.toCharArray();                           //create char array of the encrypted string
        int factorGuess = (int) Math.round(                                          //get the maximal unicode range from the chars after encryption.
                (getMax(chars)-getMin(chars)) / (double)AVERAGE_ASCII_RANGE     //if all characters would be multiplied with 2, the range would also double
        );                                                                      //so by dividing with the average ascii range, when using "normal" characters, we should get an first aproximation of the multiplication factor
        System.out.println("factor guess: "+factorGuess);
        factorGuess = (int) Math.round(factorGuess*getCorrectionFactor(chars));
        System.out.println("factor corrected: "+factorGuess);
        int minDiff = getMinDiff(chars);                                        //the minimal diffrence between 2 characters can be atmost the factor or a multiple of it
        System.out.println("minDiff: "+minDiff);                                //the larger the text, the higher the chance 2 chars are neighbours in the ascii table, and so the minimal diffrence would eqal the factor
        int factor = calculateFactor(factorGuess, chars.length, minDiff);
        System.out.println("min value: "+getMin(chars));
        System.out.println("factorA: " +factor);
        return calculateSolutions(encryptedString, factor, getShiftGuess(chars, factor, 0));
    }

    private int getShiftGuess(char[] chars, int factor, int index) {
        switch (index) {
            case 0: return (
                    (       getShiftGuess(chars, factor, 1)+
                            getShiftGuess(chars, factor, 2)+
                            getShiftGuess(chars, factor, 3)
                    )/3
            );
            case 1: return getMin(chars)-factor*LOWEST_USED_CHARACTER;
            case 2: return getMax(chars)-factor*HIGHEST_USED_CHARACTER;
            case 3: return (int) (getAverage(chars)-factor*AVERAGE_ASCII_VALUE);
        }
        return 0;
    }

    private String getPriorityGuess(String encryptedString, int factor) {

        String priorityGuess = "";
        int shiftGuess1 = getShiftGuess(encryptedString.toCharArray(), factor, 1);
        if ((getMin(encryptedString.toCharArray())-shiftGuess1)/factor == LOWEST_USED_CHARACTER) {
            System.out.println("FACTOR WILL BE: "+factor+" - "+shiftGuess1);
            System.out.println("PRIORITY GUESS: "+Encrypter.decryptSimpleAsymetric(encryptedString, factor, shiftGuess1));
            priorityGuess = Encrypter.decryptSimpleAsymetric(encryptedString, factor, shiftGuess1);
        }
        return priorityGuess;
    }

    private String[] calculateSolutions(String encryptedString, int factor, int shiftGuess) {
        List<String> solutions = new ArrayList<>();
        int highestTrust = 0;
        int range2 = shiftGuess;
        if (shiftGuess < 0) {
            shiftGuess = Encrypter.MAX_SHIFT/2;
            range2 = Encrypter.MAX_SHIFT/2;
        }
        for (int i = shiftGuess-range2; i < shiftGuess+range2; i += factor) {
            String decrypted = Encrypter.decryptSimpleAsymetric(encryptedString, factor, shiftGuess);
            int wordAmount = 0;
            int validCharAmount = 0;
            for (String word : wordsList) {
                for (char c : word.toCharArray()) {
                    if (c == 32) validCharAmount += 2;
                    if (c > 32 && c <= 128) validCharAmount++;
                }
                if (decrypted.toLowerCase().contains(word.toLowerCase())) {                             //check if the result contains any german words
                    wordAmount++;
                }
                int trustworthyness = wordAmount*2+validCharAmount;
                if (trustworthyness > highestTrust) {                                                       //if there is a new record of matching words
                    highestTrust = trustworthyness;                                                         //new amount of the applicable words is saved
                    solutions.add(0, decrypted);                                                               //string with the most hits is saved as best string
                    if (solutions.size() > 2) solutions.remove(solutions.size()-1);
                }
                //else if (trustworthyness == highestTrust) System.out.println("Another possible Answer: "+decrypted);    //if a word has as many matching words as the current best, it gets outputed as well
            }
            //System.out.println(i+" - "+decrypted+" - "+wordAmount);
        }
        String priorityGuess = getPriorityGuess(encryptedString, factor);
        if (!priorityGuess.equals("")) solutions.add(0, priorityGuess);
        String[] solutionsArray = new String[solutions.size()];
        JOptionPane.showMessageDialog(null, "Decryption Crack finished!");
        return solutions.toArray(solutionsArray);
    }
    
    private int calculateFactor(int factorGuess, int length, int minDiff) {
        int factor = -1;
        int range = (int) Math.round(factorGuess/2.0);
        System.out.println("range: "+range);
        int distance = Integer.MAX_VALUE;
        for (int fac = factorGuess-range; fac < factorGuess+range; fac++) {        //to test our assumption, we are testing if minDiff is equal to a multiple of any number in a small range around the factor
            for (int i = 1; i < 666/length; i++) {
                if (fac*i == minDiff) {
                    if (Math.abs(fac-factorGuess) < distance) {                    //find the factor, which is a dividor of minDiff, and the closest one to our factorguess from before
                        factor = fac;
                        distance = fac-factorGuess;
                        System.out.println("factor confirmed: "+fac+" * "+i+" = "+minDiff);
                        break;
                    }
                }
            }
        }
        if (factor == -1) factor = minDiff;                                         //if we found nothing (mostly because, the factorguess was to bad and the range to small then) we just try with the minDiff
        return factor;
    }

    private double getCorrectionFactor(char[] chars) {
        class CharCounter {
            final int value;
            int amount = 1;
            public CharCounter(int value) {
                this.value = value;
            }
        }
        int diffrentCharAmount = chars.length;
        ArrayList<CharCounter> charAmounts = new ArrayList<>();
        for (char c : chars) {
            boolean foundExisting = false;
            for (CharCounter counter : charAmounts) {
                if (c == counter.value) {
                    foundExisting = true;
                    counter.amount++;
                }
            }
            if (!foundExisting) charAmounts.add(new CharCounter(c));
        }
        for (CharCounter c : charAmounts) {
            if (c.amount > 1) diffrentCharAmount -= c.amount-1;
        }
        System.out.println(chars.length+" - different char mount: "+diffrentCharAmount);
        double correction = 7.0*Math.pow(diffrentCharAmount, -1.2)+0.8;
        System.out.println("correction: "+correction);
        return correction;
    }

    private int getMin(char[] chars) {
        int min = Integer.MAX_VALUE;
        for (char c : chars) {
            if ((int)c < min) min = c;
        }
        return min;
    }

    private int getMax(char[] chars) {
        int max = 0;
        for (char c : chars) {
            if ((int)c > max) max = c;
        }
        return max;
    }

    private double getAverage(char[] chars) {
        int all = 0;
        for (char c : chars) {
            all += c;
        }
        return all/((double)chars.length);
    }

    private int getMinDiff(char[] chars) {
        int min = Integer.MAX_VALUE;
        for (char c : chars) {
            for (char d : chars) {
                int diff = Math.abs(d-c);
                if (diff > 0 && Math.abs(diff) < min) {
                    System.out.println("new min: "+(int)c+" - "+(int)d);
                    min = diff;
                }
            }
        }
        return min;
    }

    private String[] getWordList() {
        ArrayList<String> wordsList = new ArrayList<>();
        File dir = new File(Paths.get("").toAbsolutePath().toString()+
                "\\EncryptionCracker\\src\\"+
                getClass().getPackage().getName().replace(".", "\\")+
                "\\resources");
        System.out.println(dir.getAbsolutePath());
        for (File file : dir.listFiles()) {   //find all files in the project folder
            if (file.getAbsolutePath().endsWith(".txt")) {                                                                             //only search for .txt files
                Scanner sc = null;
                try {
                    sc = new Scanner(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (true) {
                    assert sc != null;
                    if (!sc.hasNextLine()) break;
                    wordsList.add(sc.nextLine());                                                                                           //add words from files to the list
                }
            }
        }
        String[] words = new String[wordsList.size()];
        return wordsList.toArray(words);
    }

}