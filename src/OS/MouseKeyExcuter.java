package OS;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

public class MouseKeyExcuter {
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//Lay size goc
    public static float dpi = Toolkit.getDefaultToolkit().getScreenResolution();
    private Robot robot;
    private final int screenWidth;
    private final int screenHeight;
    private final int leftMark = InputEvent.BUTTON1_DOWN_MASK;
    private final int midMark = InputEvent.BUTTON2_DOWN_MASK;
    private final int rightMark = InputEvent.BUTTON3_DOWN_MASK;

    public MouseKeyExcuter() {
        // TODO Auto-generated constructor stub
        try {
            robot = new Robot();
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();
    }

    public void ExecuteByMessage(String message) {
        String[] arr = message.split("-");
        //Mouse move vs drag
        switch (arr[0]) {
            case "MMOVE" -> {
                int x = (int) (Float.parseFloat(arr[1]) * screenWidth);//xRatio*width
                int y = (int) (Float.parseFloat(arr[2]) * screenHeight);//xRatio*width
                robot.mouseMove(x, y);
            }
            //Mouse press vs release
            case "CLK" -> {
                // đối với arr[0] = 'CLK' thì arr[1] là nút được nhấn
                switch (arr[1]) {
                    case "LEFT" -> {
                        if (arr[2].equals("DOWN")) {
                            robot.mousePress(leftMark);
                        } else if (arr[2].equals("UP")) {
                            robot.mouseRelease(leftMark);
                        }
                    }
                    case "MID" -> {
                        if (arr[2].equals("DOWN")) {
                            robot.mousePress(midMark);
                        } else if (arr[2].equals("UP")) {
                            robot.mouseRelease(midMark);
                        }
                    }
                    case "RIGHT" -> {
                        if (arr[2].equals("DOWN")) {
                            robot.mousePress(rightMark);
                        } else if (arr[2].equals("UP")) {
                            robot.mouseRelease(rightMark);
                        }
                    }
                }
            }
            //Wheel
            case "MWHEEL" -> {
                int amount = Integer.parseInt(arr[2]);
                if (arr[1].equals("DOWN")) {
                    robot.mouseWheel(amount);
                } else {// if(arr[1].equals("UP")) {
                    robot.mouseWheel(-amount);
                }
            }
            //Key
            case "KEY" -> {
                int keycode = Integer.parseInt(arr[2]);
                if (arr[1].equals("DOWN")) {
                    robot.keyPress(keycode);
                } else { //if(arr[1].equals("UP")) {
                    robot.keyRelease(keycode);
                }
            }
        }
    }
}
