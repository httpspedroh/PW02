import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

class LZW {

    // ---------------------------------------------------------------------------------------------- //

    public static void compress(String source, String destin) throws Exception {

        // --------------------------------------------------- //

        // 1. Convert original file to string
        String originString = "";

        try {

            RandomAccessFile raf = new RandomAccessFile(source, "rw");

            // read globalId
            originString += raf.readInt() + "~";
   
            while(raf.getFilePointer() < raf.length() - 1) {

                // read lapide
                originString += raf.readByte() + "~";
               
                // read size
                originString += raf.readInt() + "~";

                // read Id
                originString += raf.readInt() + "~";
       
                // read name, user, pass, cpf, city
                for(int x = 0; x < 5; x++) {

                    originString += raf.readUTF() + "~";
                }

                // read balance
                originString += raf.readFloat() + "~";

                // read transactions
                originString += raf.readInt() + "~";

                // read emails number
                int emailsNumber = raf.readInt();
                originString += emailsNumber + "~";

                // read emails
                for(int i = 0; i < emailsNumber; i++) {
                    
                    originString += raf.readUTF() + "~";
                }
            }

            raf.close();
        }
        catch(Exception e) { e.printStackTrace(); }

        // --------------------------------------------------- //

        // 2. Create dictionary
        originString = originString.replaceAll(" ", "\\^");

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
        RandomAccessFile raf = new RandomAccessFile(destin, "rw");

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

        // -------------------------------------------------------------- //

        new File(source).delete();
    }

    // ---------------------------------------------------------------------------------------------- //
    
    public static void decompress(String source, String destin) {

        ArrayList<String> dictionary = new ArrayList<String>();
        ArrayList<Integer> output = new ArrayList<Integer>();

        // -------------------------------------------------------------- //

        // 1. Read dictionary and output
        try {

            RandomAccessFile raf = new RandomAccessFile(source, "rw");

            int dictionarySize = raf.readInt();

            for(int i = 0; i < dictionarySize; i++) dictionary.add(raf.readUTF());

            int outputSize = raf.readInt();

            if(dictionarySize < 256) {

                for(int i = 0; i < outputSize; i++) output.add((int)raf.readByte());
            }
            else if(dictionarySize < 65536) {

                for(int i = 0; i < outputSize; i++) output.add((int)raf.readShort());
            }
            else {

                for(int i = 0; i < outputSize; i++) output.add(raf.readInt());
            }

            // -------------------------------------------------------------- //

            raf.close();
        }
        catch(Exception e) { e.printStackTrace(); }

        // -------------------------------------------------------------- //

        // 2. Create descompact string
        String file = "";

        for(int i : output) file += dictionary.get(i);

        file = file.replaceAll("\\^", " ");

        // -------------------------------------------------------------- //

        // 3. Create descompact file
        String args[] = file.split("~");

        try {
            
            RandomAccessFile raf = new RandomAccessFile(destin, "rw");

            raf.writeInt(Integer.parseInt(args[0]));

            for(int i = 1; i < args.length; i++) {

                raf.writeByte(Byte.parseByte(args[i]));
                raf.writeInt(Integer.parseInt(args[++i]));
                raf.writeInt(Integer.parseInt(args[++i]));
                
                for(int x = 0; x < 5; x++) raf.writeUTF(args[++i]);

                raf.writeFloat(Float.parseFloat(args[++i]));
                raf.writeInt(Integer.parseInt(args[++i]));

                int emails_count = Integer.parseInt(args[++i]);
                
                raf.writeInt(emails_count);

                for(int j = 0; j < emails_count; j++) raf.writeUTF(args[++i]);
            }

            raf.close();
        }
        catch(Exception e) { e.printStackTrace(); }

        // -------------------------------------------------------------- //

        new File(source).delete();
    }

    // ------------------------------------------------------------------------------------------------------------ //

}