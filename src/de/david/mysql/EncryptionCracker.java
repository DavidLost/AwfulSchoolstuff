package de.david.mysql;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EncryptionCracker {

    public static final int AVERAGE_ASCII_VALUE = 105;
    public static final int AVERAGE_ASCII_RANGE = 88;
    public static final int LOWEST_USED_CHARACTER = 32;
    public static final int HIGHEST_USED_CHARACTER = 128;

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
        List<String> solutions = new ArrayList<>();
        int factor = (int) Math.round(                                          //get the maximal unicode range from the chars after encryption.
                (getMax(chars)-getMin(chars)) / (double)AVERAGE_ASCII_RANGE     //if all characters would be multiplied with 2, the range would also double
        );                                                                      //so by dividing with the average ascii range, when using "normal" characters, we should get an first aproximation of the multiplication factor
        System.out.println("factor guess: "+factor);
        double newFac = factor*getCorrectionFactor(chars);
        factor = (int) Math.round(newFac);
        if (factor < 1) factor = 1;
        System.out.println("factor corrected: "+factor);
        int minDiff = getMinDiff(chars);                                        //the minimal diffrence between 2 characters can be atmost the factor or a multiple of it
        System.out.println("minDiff: "+minDiff);                                //the larger the text, the higher the chance 2 chars are neighbours in the ascii table, and so the minimal diffrence would eqal the factor
        int factorAnswer = -1;
        int range = (int) Math.round(factor/4.0);
        System.out.println("range: "+range);
        int distance = Integer.MAX_VALUE;
        for (int fac = factor-range; fac < factor+range; fac++) {               //to test our assumption, we are testing if minDiff is equal to a multiple of any number in a small range around the factor
            for (int i = 1; i < 666/encryptedString.length(); i++) {
                if (fac*i == minDiff) {
                    if (Math.abs(minDiff-factor) < distance) {
                        factorAnswer = fac;
                        distance = minDiff-factor;
                        System.out.println("factor confirmed: "+fac+" * "+i+" = "+minDiff);
                        break;
                    }
                }
            }
        }
        if (factorAnswer == -1) factorAnswer = minDiff;                                 //if we found nothing (mostly because, the factorguess was to bad and the range to small then) we just try with the minDiff
        System.out.println("min value: "+getMin(chars));
        int shiftGuess1 = getMin(chars)-factorAnswer*LOWEST_USED_CHARACTER;             //3 diffrent ways to aproximate
        int shiftGuess2 = getMax(chars)-factorAnswer*HIGHEST_USED_CHARACTER;
        int shiftGuess3 = (int) (getAverage(chars)-factorAnswer*AVERAGE_ASCII_VALUE);
        int shiftGuess = (shiftGuess1+shiftGuess2+shiftGuess3)/3;
        System.out.println("shiftguess: "+shiftGuess1+" - "+shiftGuess2+" - "+shiftGuess3+" - "+shiftGuess);
        String priorityGuess = "";
        if ((getMin(chars)-shiftGuess1)/factorAnswer == LOWEST_USED_CHARACTER) {
            shiftGuess = shiftGuess1;
            System.out.println("FACTOR WILL BE: "+factorAnswer+" - "+shiftGuess);
            System.out.println("PRIORITY GUESS: "+Encrypter.decryptSimpleAsymetric(encryptedString, factorAnswer, shiftGuess));
            priorityGuess = Encrypter.decryptSimpleAsymetric(encryptedString, factorAnswer, shiftGuess);
        }
        System.out.println("factorA: " +factorAnswer);
        ArrayList<String> words = new ArrayList<>();
        for (File file : new File(Paths.get("").toAbsolutePath().toString()+"\\src\\de\\david\\mysql").listFiles()) {   //find all files in the project folder
            if (file.getAbsolutePath().endsWith(".txt")) {                                                                             //only search for .txt files
                Scanner sc = null;
                try {
                    sc = new Scanner(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (sc.hasNextLine()) {
                    words.add(sc.nextLine());                                                                                           //add words from files to the list
                }
            }
        }
        int highestTrust = 0;
        int range2 = shiftGuess;
        if (shiftGuess < 0) {
            shiftGuess = 10000;
            range2 = 10000;
        }
        for (int i = shiftGuess-range2; i < shiftGuess+range2; i += factorAnswer) {
            String decrypted = "";
            for (char c : chars) {
                decrypted += (char)((c-i)/factorAnswer);                                                //decrypt with our calculated values
            }
            int wordAmount = 0;
            int validCharAmount = 0;
            for (String word : words) {
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
        if (!priorityGuess.equals("")) solutions.add(0, priorityGuess);
        String[] solutionsArray = new String[solutions.size()];
        return solutions.toArray(solutionsArray);
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
                if (d-c > 0 && d-c < min) {
                    System.out.println("new min: "+(int)c+" - "+(int)d);
                    min = d-c;
                }
            }
        }
        return min;
    }

}