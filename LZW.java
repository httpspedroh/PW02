import java.io.RandomAccessFile;
import java.util.ArrayList;

class LZW {

    public static ArrayList<Integer> compact(String source) throws Exception {

        // --------------------------------------------------- //

        // 1. Convert original file to string
        String originString = "";

        try {

            RandomAccessFile raf = new RandomAccessFile(source, "rw");
            
            int tmp_int = 0;
            float tmp_float = 0;
            String tmp_string = "";

            // read globalId
            tmp_int = raf.readInt();
            originString += Integer.toString(tmp_int).length();
            originString += tmp_int;
  
            while(raf.getFilePointer() < raf.length() - 1) {

                originString += raf.readByte(); // read lapide

                // read size
                tmp_int = raf.readInt();
                originString += Integer.toString(tmp_int).length();
                originString += tmp_int;

                // read Id
                tmp_int = raf.readInt();
                originString += Integer.toString(tmp_int).length();
                originString += tmp_int;

                // read name, user, pass, cpf, city
                for(int x = 0; x < 5; x++) {

                    tmp_string = raf.readUTF();
                    originString += tmp_string.length();
                    originString += tmp_string;
                }

                // read balance
                tmp_float = raf.readFloat();
                originString += Float.toString(tmp_float).length();
                originString += tmp_float;

                // read transactions
                tmp_int = raf.readInt();
                originString += Integer.toString(tmp_int).length();
                originString += tmp_int;

                // read emails number
                tmp_int = raf.readInt();
                originString += Integer.toString(tmp_int).length();
                originString += tmp_int;

                // read emails
                for(int i = 0; i < tmp_int; i++) {
                    
                    tmp_string = raf.readUTF();
                    originString += tmp_string.length();
                    originString += tmp_string;
                }
            }

            raf.close();
        }
        catch(Exception e) { e.printStackTrace(); }

        System.out.println(originString);

        // --------------------------------------------------- //

        // 2. Create dictionary
        originString = originString.replaceAll(" ", "_");

        ArrayList<String> dictionary = new ArrayList<String>();

        for(int i = 0; i < originString.length(); i++) {

            String s = Character.toString(originString.charAt(i));
            
            if(!dictionary.contains(s)) dictionary.add(s);
        }

        // -------------------------------------------------------------- //

        // 3. Create dictionary output
        ArrayList<Integer> output = new ArrayList<Integer>();

        for(int i = 0; i < originString.length(); i++) {

            String s = Character.toString(originString.charAt(i));

            while(true) {

                if(i == originString.length() - 1) break;

                s += originString.charAt(i + 1);

                if(dictionary.contains(s)) {
                    
                    if(i == originString.length() - 2) {
                        
                        output.add(dictionary.indexOf(s));
                        break;
                    }
                    else i++;
                }
                else {

                    dictionary.add(s);
                    
                    if(i == originString.length() - 2) output.add(dictionary.indexOf(s));
                    else output.add(dictionary.indexOf(s.substring(0, s.length() - 1)));
                    break;
                }
            }

            // --------------- //

            if(i == originString.length() - 1) break;
        }

        // -------------------------------------------------------------- //

        // 4. Create compressed file
        RandomAccessFile raf = new RandomAccessFile("compactado.pedrip", "rw");

        raf.writeInt(dictionary.size());

        for(String str : dictionary) raf.writeUTF(str);

        raf.writeInt(output.size());

        if(dictionary.size() < 256) {

            for(int i : output) raf.writeByte(i);
        }
        else if(dictionary.size() < 65536) {

            for(int i : output) raf.writeShort(i);
        }
        else {

            for(int i : output) raf.writeInt(i);
        }

        raf.close();
        return output;
    }

    // ------------------------------------------------------------------------------------------------------------ //

    public static void main(String[] args) throws Exception {

        compact(Main.DEFAULT_FILE);
    }
}