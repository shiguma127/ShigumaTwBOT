package ShigumaTwBot;

import twitter4j.StatusUpdate;

import java.awt.*;

class Mouse extends Thread {
    private Main main;

    public Mouse(Main main) {
        this.main = main;
    }
    @Override
    public void run() {
        while (Main.isRunning) {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            int x = pointerInfo.getLocation().x;
            int y = pointerInfo.getLocation().y;
            System.out.println("x:" + x + " " + "y:" + y);
            System.out.println("#現在のマウスの位置" + "\n" + "x:" + x + " " + "y:" + y);
            this.main.asyncTwitter.updateStatus(new StatusUpdate("#現在のマウスの位置" + "\n" + "x:" + x + " " + "y:" + y));
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}