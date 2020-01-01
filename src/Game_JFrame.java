import javax.swing.JFrame;

/*
    In this class we are adding an object from Game_Panel class to the frame.
    E.G. adding a frame to the game
    and a simple method to make everything visible
 */
class Game_JFrame {

    private JFrame frame; // Used as a frame of the game

    // Creating the frame
    private void initFrame(){
        frame = new JFrame("Snake");
        frame.add(new Game_Panel());

        frame.setResizable(false);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Setting everything to be visible
    void startGame(){
        initFrame();
        frame.setVisible(true);

    }
}
