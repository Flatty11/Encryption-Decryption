package encryptdecrypt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {

        String inputFile = checkInput("-in",args,"noInput");

        char[] initialFile = readFileAsString(inputFile).toCharArray();

        String outputFile = checkInput("-out",args,"noOutput");

        String key = checkInput("-mode",args,"enc");

        char[] initial = checkInput("-data",args,"").toCharArray();

        int delta = Integer.parseInt(checkInput("-key",args,"0"));
        
        String result;

        useAlgorithm alg = new useAlgorithm();

        if (checkInput("-alg",args,"shift") == "unicode" ) {
            alg.setAlg(new unicode());
        } else alg.setAlg(new shift());


        boolean checkInputLogic = inputFile.equals("noInput") || (inputFile.equals("-in") && initial.length == 0);

        switch (key) {
            case "enc" :

                if (checkInputLogic) {
                    result = alg.encryption(initial,delta);
                } else {
                    result = alg.encryption(initialFile,delta);
                }

                break;
            case "dec" :

                if (checkInputLogic) {
                    result = alg.decryption(initial,delta);
                } else {
                    result = alg.decryption(initialFile,delta);
                }

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + key);
        }



        if (outputFile.equals("noOutput")) {

            System.out.println(result);

        } else {

            File file = new File(outputFile);
            try (PrintWriter printWriter = new PrintWriter(file)) {
                printWriter.println(result);
            } catch (IOException e) {
                System.out.printf("Error %s", e.getMessage());
            }

        }

    }

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static String checkInput(String argument, String[] args,String def) {
        String result = "";
        boolean flag = false;
        for (int i = 0; i < args.length; i++) {
            if (argument.equals(args[i])) {
                result = args[i+1];
                flag = true;
            }
        }
        if (!flag) {
            result = def;
        }
        return result;
    }


    interface Algorithm {
        String decryption(char[] decrypt, int key);
        String encryption(char[] initial, int key);
    }

    static class shift implements Algorithm {

        @Override
        public String decryption(char[] decrypt, int key) {
            for (int i = 0; i < decrypt.length; i++) {
                if (decrypt[i] >= 'A' && decrypt[i] <= 'Z') {

                    if (decrypt[i] - 'A' - key < 0) {
                        decrypt[i] = (char) ((decrypt[i] - 'A' - key) % 26 + 'Z' + 1);
                    } else {
                        decrypt[i] = (char) ((decrypt[i] - 'A' - key) % 26 + 'A');
                    }
                }

                if (decrypt[i] >= 'a' && decrypt[i] <= 'z') {

                    if (decrypt[i] - 'a' - key < 0) {
                        decrypt[i] = (char) ((decrypt[i] - 'a' - key) % 26 + 'z' + 1);
                    } else {

                        decrypt[i] = (char) ((decrypt[i] - 'a' - key) % 26 + 'a');
                    }

                }
            }
                return new String(decrypt);
        }

        @Override
        public String encryption(char[] initial, int key) {
            for (int i = 0; i < initial.length; i++) {
                if (initial[i] >= 'A' && initial[i] <= 'Z') {
                    initial[i] = (char) ((initial[i] - 'A' + key)  % 26 + 'A');
                }

                if (initial[i] >= 'a' && initial[i] <= 'z') {
                    initial[i] = (char) ((initial[i] - 'a' + key)  % 26 + 'a');
                }

            }
            return new String(initial);
        }
    }

    static class unicode implements Algorithm {

        @Override
        public String decryption(char[] decrypt, int key) {
            for (int i = 0; i < decrypt.length; i++) {
                decrypt[i] -= key;
            }
            return new String(decrypt);
        }

        @Override
        public String encryption(char[] initial, int key) {
            for (int i = 0; i < initial.length; i++) {
                initial[i] += key;
            }
            return new String(initial);
        }
    }

    static class useAlgorithm {
        private Algorithm alg;
        public void setAlg(Algorithm alg) {
            this.alg = alg;
        }

        public String encryption(char[] initial, int key) {
            return this.alg.encryption(initial, key);
        }

        public String decryption(char[] initial, int key) {
            return this.alg.decryption(initial, key);
        }
    }

}
