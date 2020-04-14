package de.david.mysql;

public class Main {

    public static void main(String[] args) {

        String message = "leRz1ichen LüCkWunSCH";
        Encrypter encrypter = new Encrypter();
        String encrypted = encrypter.encryptSimpleAsymetric(message);
        String decrypted = encrypter.decryptSimpleAsymetric(encrypted);
        String decrypted2 = new EncryptionCracker().crackSimpleAsymetricEncryption(encrypter, encrypted);
        encrypter.printKeys();
        String[] decrypted3 = new EncryptionCracker().crackSimpleAsymetricEncryptionWithoutEncrypter(encrypted);
        System.out.println(encrypted+"\n"+decrypted+"\n"+decrypted2);
        for (String str : decrypted3) System.out.println("d3: "+str);
    }

}