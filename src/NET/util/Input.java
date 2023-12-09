package NET.util;

import java.util.Scanner;

public class Input {
    public static String getInputFromKeyboard(String mess) {
        Scanner sc = new Scanner(System.in);

        System.out.println(mess);
        return sc.nextLine();
    }


}
