import java.io.RandomAccessFile;
import java.util.ArrayList;

class LZW {

    public static ArrayList<Integer> compact(String origin) {

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

        for(Integer i : output) System.out.print(dictionary.get(i));
        return output;
    }

    public static void main(String[] args) throws Exception {
        
        String s = "";

        try {

            String source = Main.DEFAULT_FILE;

            RandomAccessFile raf = new RandomAccessFile(source, "rw");

            s += raf.readInt();
            
            while(raf.getFilePointer() < raf.length() - 1) {

                s += raf.readByte();
                s += raf.readInt();
                s += raf.readInt();

                for(int x = 0; x < 5; x++) s += raf.readUTF();

                s += raf.readFloat();
                s += raf.readInt();

                int emailsCount = raf.readInt();

                s += emailsCount;
                
                for(int i = 0; i < emailsCount; i++) s += raf.readUTF();
            }

            raf.close();
        }
        catch(Exception e) { e.printStackTrace(); }

        compact(s);

        // -------------------------------------------------------------- //
    }
}