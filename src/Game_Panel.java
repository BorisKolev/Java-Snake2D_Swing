import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.*;

/*
    In this class everything that is drawn on the JPanel
    all the logic, movement, images, scoring happens here
 */
public class Game_Panel extends JPanel implements ActionListener {

    private final int BOARD_WIDTH = 250; // Determines the WIDTH of the board
    private final int BOARD_HEIGHT = 250; // Determines the HEIGHT of the board

    private final int DOT_SIZE = 10; // Determines the size of the apple and the dot used as snake's body and head

    // Determines the maximum number of possible DOTS in the panel
    // Each dot has the same height and width
    private final int ALL_DOTS = (BOARD_WIDTH * BOARD_HEIGHT) / (DOT_SIZE^2);

    private final int MSG_SIZE = 15; // Size for all messages that will be displayed

    private final int[] snakeX_Coor = new int[ALL_DOTS]; // x coordinate for all parts of the snake
    private final int[] snakeY_Coor = new int[ALL_DOTS]; // y coordinate for all parts of the snake
    private int snake_size; // The size of the snake
    private int score = 0; // Keep track of the score
    private ArrayList<Integer> arrOfScores; // keep all the scores
                                            // necessary for outputting the top five scores

    private int appleX_Coor; // x coordinate for apple
    private int appleY_Coor; // y coordinate for apple

    // Timer and images used to draw the snake ( instead of drawing a square )
    private Timer timer;
    private Image body;
    private Image apple;
    private Image head;

    // Direction booleans

    private boolean up = false;
    private boolean right = true;
    private boolean down = false;
    private boolean left = false;

    // Game will be running until this boolean is true
    private boolean playing = true;

    // File we will be writing to and reading from
    private File file = new File("scores.txt");

    /*
        In the constructor we are adding a listener which is taking an object from TAdapter to handle the keyboard inputs
        We are also setting the the background of the Panel black, and gaining the JPanel focus in order for the
        keyboard inputs to work, setting the size of the panel, displaying the images and initiating the game
     */
    Game_Panel(){

        this.addKeyListener(new TAdapter());
        this.setBackground(Color.black);
        this.setFocusable(true);

        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.DisplayImages();
        this.initGame();

    }

    /*
        This method is simply loading the images
        MAKE SURE THE PATH IS RIGHT OTHERWISE YOU WILL NOT BE ABLE TO SEE THE SNAKE
     */
    private void DisplayImages() {

        // MAKE SURE THE PATH IS RIGHT
        ImageIcon iih = new ImageIcon("images/head.png");
        head = iih.getImage();

        // MAKE SURE THE PATH IS RIGHT
        ImageIcon iid = new ImageIcon("images/body.png");
        body = iid.getImage();

        // MAKE SURE THE PATH IS RIGHT
        ImageIcon iia = new ImageIcon("images/apple.png");
        apple = iia.getImage();

    }

    /*
        This method initiate the game
        It creates the snake, randomly allocate the apply and start the time
     */
    private void initGame() {
        snake_size = 3; // Initial size of the snake

        for (int i = 0; i < snake_size; i++) {
            snakeX_Coor[i] = 50 - i * 10; // the height in which snake will first appear
            snakeY_Coor[i] = 50; // the width in which snake will first appear
        }

        AppleLocating();

        // Determines the speed of the game
        int DELAY = 70;

        timer = new Timer(DELAY, this);
        timer.start();
    }

    /*
        This method is displaying the score while the game is running,
        It makes sure it is placed in the upper right corner of the JPanel.
     */
    private void DisplayScoring(Graphics g){
        String msg = "Score:: " + score;
        Font small = new Font("Helvetica", Font.BOLD, MSG_SIZE);
        FontMetrics fontMetr = getFontMetrics(small);

        g.setColor(Color.WHITE);
        g.setFont(small);
        g.drawString(msg, BOARD_WIDTH - (fontMetr.stringWidth(msg) + 2), MSG_SIZE);
    }

    /*
        Displaying the top scores after the game has finished.
        It is using a for loop to output each score from the arrOfScores array.
        With the right attributes it is placing the strings on the right place.
     */
    private void DisplayTopScores(Graphics g){
        final int height = 100;
        String msg = "TOP SCORES";
        Font small = new Font("Helvetica", Font.BOLD, MSG_SIZE);
        FontMetrics fontMetr = getFontMetrics(small);

        g.setColor(Color.WHITE);
        g.setFont(small);
        g.drawString(msg, (BOARD_WIDTH - fontMetr.stringWidth(msg)) / 2, height);

        for(int i = 0; i < arrOfScores.size(); i++){

            String place = "Score " + arrOfScores.get(i);
            g.drawString(place, (BOARD_WIDTH - fontMetr.stringWidth(place)) / 2, height + MSG_SIZE + (MSG_SIZE * i));
        }

    }

    /*
        This method is simply outputting the string "Game over" on the screen when the game has finished
     */
    private void DisplayGameOver(Graphics g) {

        String msg = "GAME OVER";
        Font small = new Font("Helvetica", Font.BOLD, MSG_SIZE);
        FontMetrics fontMetr = getFontMetrics(small);

        g.setColor(Color.RED);
        g.setFont(small);


        g.drawString(msg, (BOARD_WIDTH - fontMetr.stringWidth(msg)) / 2, MSG_SIZE);
    }

    /*
        This is the ONLY method called in paintComponent method
        All methods that are doing any kind of Drawing with object Graphics
        e.g. (DisplayScoring, DisplayGameOver, DisplayTopScores) are called here
        It is throwing FileNotFoundException because we are also reading and writing from a file
     */
    private void DoAllPanelDrawing(Graphics g) throws FileNotFoundException{

        if (playing) {
            DisplayScoring(g);

            // Draw the apple image in location 'apple_x ' and 'apple_y'
            g.drawImage(apple, appleX_Coor, appleY_Coor, this);

            // Loop through the body of the snake and drawImage instead of square
            for (int i = 0; i < snake_size; i++) {
                if (i == 0) {
                    g.drawImage(head, snakeX_Coor[i], snakeY_Coor[i], this);
                } else {
                    g.drawImage(body,  snakeX_Coor[i], snakeY_Coor[i], this);
                }
            }

            // Synchronizes this toolkit's graphics state.
            Toolkit.getDefaultToolkit().sync(); // Make sure it will be displayed up-to-date

        }

        else { // When the game has finished

            DisplayGameOver(g);
            writeCurrentScoreToFile();
            readFromFile();
            DisplayTopScores(g);
        }
    }

    /*
        Overriding the paintComponent method to actually make all the drawings appear on the JPanel
        uses only the DoAllPanelDrawing method, because all drawing methods are stored within.
        It has to catch the FileNotFoundException since the DoAllPanelDrawing method has methods
        that are using files
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            DoAllPanelDrawing(g);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
        Check Weather the head of the snake collides with the apply, if so increase the size, score
        and locate new apply
     */
    private void AppleChecking() {

        if ((snakeX_Coor[0] == appleX_Coor) && (snakeY_Coor[0] == appleY_Coor)) {

            snake_size++;
            score++;
            AppleLocating();
        }
    }

    /*
        Here is the moving algorithm with which the snake is controlled
        it is using an if statements to move the head of the snake
        and a for loop the move each of the elements one element further
     */
    private void snakeMoving() {

        // From the end of the snake we move each element further in the array
        for (int s = snake_size; s > 0; s--) {
            snakeX_Coor[s] = snakeX_Coor[(s - 1)];
            snakeY_Coor[s] = snakeY_Coor[(s - 1)];
        }

        // controlling the HEAD of the snake
        if (up) {
            snakeY_Coor[0] -= DOT_SIZE;
        }

        if (right) {
            snakeX_Coor[0] += DOT_SIZE;
        }

        if (down) {
            snakeY_Coor[0] += DOT_SIZE;
        }

        if (left) {
            snakeX_Coor[0] -= DOT_SIZE;
        }

    }

    /*
        This method checks whether snake hits the wall or itself
     */
    private void checkCollision() {

        // Loop through the body of the snake from the end to the part behind the head e.g. (snake_x[1] and snake_y[1])
        for (int i = snake_size; i > 0; i--) {

            if ((i > 3) && (snakeX_Coor[0] == snakeX_Coor[i]) && (snakeY_Coor[0] == snakeY_Coor[i])) {
                playing = false;
                break;
            }
        }

        // IF THE SNAKE HITS THE WALL
        if (snakeY_Coor[0] > BOARD_HEIGHT || snakeY_Coor[0] < 0 || snakeX_Coor[0] >= BOARD_WIDTH || snakeX_Coor[0] < 0) {
            playing = false;
        }

        // if the game is over stop the timer
        if (!playing) {
            timer.stop();
        }
    }

    /*
        This method is simply allocating the apple using the random method not much to be explained
     */
    private void AppleLocating() {

        int RAND_POS = 24; // MAKE SURE IT IS WITHIN THE RANGE OF THE JPanel
                           // we are multiply it by 10 later so if the JPanel is 300x300 maximum RAND_POS should be 29

        int r = (int) (Math.random() * RAND_POS);
        appleX_Coor = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        appleY_Coor = ((r * DOT_SIZE));

    }

    /*
        This method is constantly repainting the JPanel
        While the game is still playing we are using methods to move the snake, check weather the snake collides
        or a new apple is needed for allocation.
     */
    public void actionPerformed(ActionEvent e) {

        if (playing) {

            snakeMoving(); // First the snake moves
            checkCollision(); // THEN we are checking for collision
            AppleChecking(); // and then checking weather we need to allocate an apple
            repaint();

        }

    }

    // <-- KEYBOARD HANDLER CLASS --->

    /*
        This class is extending KeyAdapter because we only need keyPressed method
     */
    private class TAdapter extends KeyAdapter {

        /*
            This method is getting the user input and setting the appropriate booleans
            which then help for the moving method to work
         */
        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_DOWN) && (!up)) {
                down = true;
                right = false;
                left = false;
            }

            if ((key == KeyEvent.VK_LEFT) && (!right)) {
                left = true;
                up = false;
                down = false;
            }

            if ((key == KeyEvent.VK_UP) && (!down)) {
                up = true;
                right = false;
                left = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!left)) {
                right = true;
                up = false;
                down = false;
            }

        }
    }

    // <--- FILE METHODS --->

    /*
        Simple creating a printWrite and writes to the file
        using new FileOutputStream not to overwrite the previous content in the file
     */
    private void writeCurrentScoreToFile() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
        pw.println("Score:: " + score); // writing the score within the file
        pw.close();
    }

    /*
        Reading from the file and storing only the top 5 scores in the arrOfScores
     */
    private void readFromFile() throws FileNotFoundException {

        Scanner x = new Scanner(file);
        arrOfScores = new ArrayList<>();

        while(x.hasNext()){
            x.next(); // passing the word 'score' in the file
            arrOfScores.add(x.nextInt()); // adding the score from the file
        }

        Collections.sort(arrOfScores); // Sorting them in ascending order
        Collections.reverse(arrOfScores); // Arranging them in descendant order

        // Loop through arrOfScores and leave only the first five elements e.g. ( top 5 scores )
        for(int i = 0; i < arrOfScores.size(); i++){
            if(i > 4){
                arrOfScores.remove(i);
                i--;
            }
        }

    }

}
