import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

class LZW {

    public static String compact(String origin) {

        origin = origin.replaceAll(" ", "_");

        // create a dictionary
        ArrayList<String> dictionary = new ArrayList<String>();

        for(int i = 0; i < origin.length(); i++) {

            String s = Character.toString(origin.charAt(i));
            
            if(!dictionary.contains(s)) dictionary.add(s);
        }

        // -------------------------------------------------------------- //

        ArrayList<Integer> output = new ArrayList<Integer>();

        for(int i = 0; i < origin.length(); i++) {

            String s = Character.toString(origin.charAt(i));

            while(true) {

                if(i == origin.length() - 1) break;

                s += origin.charAt(i + 1);

                if(dictionary.contains(s)) {
                    
                    if(i == origin.length() - 2) {
                        
                        output.add(dictionary.indexOf(s));
                        break;
                    }
                    else i++;
                }
                else {

                    dictionary.add(s);
                    
                    if(i == origin.length() - 2) output.add(dictionary.indexOf(s));
                    else output.add(dictionary.indexOf(s.substring(0, s.length() - 1)));
                    break;
                }
            }

            // --------------- //

            if(i == origin.length() - 1) break;
        }
        return output.toString();
    }

    public static void main(String[] args) throws Exception {
        
        String s = new String();

        try {

            String source = Main.DEFAULT_FILE;

            RandomAccessFile raf = new RandomAccessFile(source, "rw");

            if(source.equals("accounts.bin")) raf.seek(4);
            else raf.seek(0);

            while(raf.getFilePointer() < raf.length()) {

                s += raf.readChar();
            }
        
            raf.close();
        }
        catch(Exception e) { e.printStackTrace(); }

        System.out.println(s);

        // -------------------------------------------------------------- //
    }
}