package tuan.aprotrain.projectpetcare.entity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Recycle {
    // upper letter for string
    public String setUpperLetter(String text){
        String words[]=text.split("\\s");
        String capitalizeWord="";
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord+=first.toUpperCase()+afterfirst+" ";
        }
        return capitalizeWord.trim();
    }

    //upper letter a character
    public String getUpperLetter(String text){
        char c[] = text.toCharArray();
        System.out.println("The first character of each word: ");
        List<Character> chars = new LinkedList<Character>(Arrays.asList());
        for (int i=0; i < c.length; i++) {
            // Logic to implement first character of each word in a string
            if(c[i] != ' ' && (i == 0 || c[i-1] == ' ')) {
                chars.add(c[i]);
            }
        }
        StringBuilder sb = new StringBuilder();
        for(Character ch : chars){
            sb.append(ch);
        }
        return sb.toString();
    }

    public String idHashcode(String text){
        String upperCharacter = getUpperLetter(text);
        int randomNumber = ( int )( Math.random() * 9999 );

        if( randomNumber <= 1000 ) {
            randomNumber = randomNumber + 1000;
        }
        String bookingId  = upperCharacter + String.valueOf(randomNumber);
        return getMd5(bookingId);
    }

    public String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String CalculateDate(String start, long time){
        //int timeEnd = Integer.parseInt(end);
        String result = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(start);
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, (int) time);
            result = simpleDateFormat.format(calendar.getTime());
            //editTextEnd.setText(result);
            return result;
        }catch (Exception e){
            System.out.println("error"+ e);
        }
        return result;
    }
}
