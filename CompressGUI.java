import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class CompressGUI {
    private JFrame frame;

    public CompressGUI() {
        frame = new JFrame("Huffman Compression");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(frame.getSize());
        frame.add(new InnerCompress(frame.getSize()));
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String... argv) {
        new CompressGUI();
    }

    public static class InnerCompress extends JPanel implements Runnable, MouseListener {

        private Thread animator;
        Dimension d;
        // String s = "shesellsseashells";
        String s = "supercalifragilisticexpialidocious";
        String mapCode = "a:1101, :0010,b:000100,c:00011,d:000101,e:11000,f:1100110,g:010,h:0000,i:0110,l:101,m:1100111,n:1110,o:1111,p:110010,r:1001,s:10000,t:10001,w:0111,y:0011";
        String coded = "101101110111101100110110101101001110010011110110101001110011111001000111000100000101111010110001001110100011000001110011100111100001011001111100010001111011011011011101111010001001110000011010101101111010111101011110101111000110000001001101000000101101001010111111110010001011101101110011111000";
        BinarySearchTree b = null;
        Rectangle button1 = new Rectangle(50, 50, 100, 50);
        Rectangle button2 = new Rectangle(50, 120, 100, 50);
        Rectangle button3 = new Rectangle(50, 190, 100, 50);
        Image myImage;
        int startX = 20;
        int startY = 20;
        String nName = "";
        int prev = 0;

        public InnerCompress(Dimension dimension) {
            setSize(dimension);
            setPreferredSize(dimension);
            addMouseListener(this);
            addKeyListener(new TAdapter());
            setFocusable(true);
            d = getSize();
            // Load the image using ImageIcon
            myImage = new ImageIcon("/Users/frenwd24/IdeaProjects/BubbleProgram.java/src/astral-unicorn.jpeg")
                    .getImage(); // Replace "path_to_image.jpg" with your image path

            // for animating the screen - you won't need to edit
            if (animator == null) {
                animator = new Thread(this);
                animator.start();
            }
            setDoubleBuffered(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(Color.black);
            g2.fillRect(0, 0, (int) d.getWidth(), (int) d.getHeight());

            // make three buttons
            g2.setColor(Color.WHITE);
            g2.fillRect(button1.x, button1.y, button1.width, button1.height);
            g2.fillRect(button2.x, button2.y, button2.width, button2.height);
            g2.fillRect(button3.x, button3.y, button3.width, button3.height);

            g2.setColor(Color.BLACK);
            g2.drawString("Encode", button1.x + 10, button1.y + 30);
            g2.drawString("Decode", button2.x + 10, button2.y + 30);
            g2.drawString("Tree Decode", button3.x + 10, button3.y + 30);

            // String
            g2.setColor(Color.WHITE);
            g2.drawString("String: " + s, 50, 300);
            // Coded String
            g2.drawString("Coded: " + coded, 50, 350);

            g2.drawImage(myImage, 500, 50, 250, 250, this);

        }

        public static int random(int a, int b) {
            int max = a;
            int min = b;
            int random = (int) (Math.random() * (max - min) + min);

            return random;
        }

        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if (button1.contains(e.getPoint())) {
                System.out.println("Button1 clicked");
                button1();

            } else if (button2.contains(e.getPoint())) {
                System.out.println("Button2 clicked");
                button2();

            } else if (button3.contains(e.getPoint())) {
                System.out.println("Button3 clicked");
                button3();
            }

        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        private class TAdapter extends KeyAdapter {

            public void keyReleased(KeyEvent e) {
                int keyr = e.getKeyCode();

            }

            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                String c = KeyEvent.getKeyText(e.getKeyCode());
                c = Character.toString((char) key);

                if (prev != 16) {
                    c = c.toLowerCase();
                }

                prev = key;
                if (key == 8)
                    nName = nName.substring(0, nName.length() - 1);
                if (key == 10) {
                    nName = "";
                }

                if (key != 8 && key != 10 && key != 16)
                    nName += c;
                System.out.println(key + " - " + c);

                // message = "Key Pressed: " + e.getKeyCode();
            }
        }// end of adapter

        public void button1() {
            ArrayList<Node> frequencyList = new ArrayList<>();

            // assigning values to the f node.
            // f.data = x.data + y.data;

            for (int i = 0; i < s.length(); i++) {
                char currentChar = s.charAt(i);
                boolean found = false;
                for (Node node : frequencyList) {
                    if (node.c == currentChar) {
                        found = true;
                        node.data++;
                    }
                }
                if (!found) {
                    Node n = new Node();
                    n.data = 1;
                    n.c = currentChar;
                    frequencyList.add(n);
                }
            }

            PriorityQueue<Node> q = new PriorityQueue<Node>(frequencyList.size(), new MyComparator());
            for (int j = 0; j < frequencyList.size(); j++) {

                q.add(frequencyList.get(j));
            }

            /*
             * for (int j = 0; j < frequencyList.size(); j++) {
             * Node n = q.poll();
             * System.out.print(n.data + " " + n.c + " -- ");
             * }
             */

            // to the sum of the frequency of the two nodes
            // Assume 'queue' is your PriorityQueue of Node objects
            while (q.size() > 1) {
                // Remove the two nodes with the smallest frequencies
                Node node1 = q.poll();
                Node node2 = q.poll();

                // Create a new node with these two nodes as children
                Node parentNode = new Node(node1.data + node2.data, '_');
                parentNode.left = node1;
                parentNode.right = node2;

                // Insert the new node back into the queue
                q.add(parentNode);
            }

            BinarySearchTree b = new BinarySearchTree();
            Node nn = q.poll();
            b.root = nn;
            b.showHuff(b.root, "");
            coded = "";
            this.b = b;

            for (int i = 0; i < s.length(); i++) {
                char currentChar = s.charAt(i);
                coded += b.hash_map.get(currentChar);
                System.out.print(b.hash_map.get(currentChar) + " ");
            }
        }

        public void button2() {
            /*
             * HashMap<String,Character> rev_map = new HashMap<String, Character>();
             * for(char key : b.hash_map.keySet()){
             * rev_map.put(b.hash_map.get(key), key);
             * }
             * String temp = "1100110111001011111100001100110010111";
             * String temp2 = "";
             * String coded = "";
             * for(int i = 0; i < temp.length(); i ++){
             * char currentChar = temp.charAt(i);
             * temp2 += currentChar;
             * if(rev_map.get(temp2) != null){
             * coded += rev_map.get(temp2);
             * temp2 = "";
             * }
             * }
             * this.coded = coded;
             */
            String[] parts = mapCode.split(",");
            HashMap<String, Character> rev_map = new HashMap<String, Character>();
            for (int i = 0; i < parts.length; i++) {
                String[] temp = parts[i].split(":");
                rev_map.put(temp[1], temp[0].charAt(0));
            }
            String c = "";
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < coded.length(); i++) {
                c += coded.charAt(i);
                if (rev_map.containsKey(c)) {
                    char cc = rev_map.get(c);
                    temp.append(cc);
                    c = "";
                }
            }
            s = temp.toString();
        }

        public void button3() {
            Node examine = b.root;
            for (int i = 0; i < coded.length(); i++) {
                char currentChar = coded.charAt(i);
                int a = Character.getNumericValue(currentChar);
                System.out.println("a " + a);
                if (a == 0) {
                    if (examine.left != null) {
                        // do something
                        examine = examine.left;
                        if (examine.right == null && examine.left == null) {
                            s += examine.c;
                            System.out.println("c " + examine.c);
                            examine = b.root;
                        }
                    }

                } // end of left

                if (a == 1) {
                    if (examine.right != null) {
                        // do something
                        examine = examine.right;
                        if (examine.right == null && examine.left == null) {
                            s += examine.c;
                            System.out.println("c " + examine.c);
                            examine = b.root;
                        }
                    }

                } // end of right
                /*
                 * for(int i = 0; i < coded.length(); i++){
                 * char c = coded.charAt(i);
                 * int a = Character.getNumericValue(c);
                 * }
                 * String c = "";
                 * StringBuilder temp = new StringBuilder();
                 * for(int i = 0; i < coded.length(); i++){
                 * c += coded.charAt(i);
                 * if(b.find(b.root, c) != null) {
                 * char cc = b.find(b.root, c);
                 * temp.append(cc);
                 * c = "";
                 * }
                 * }
                 * s = temp.toString();
                 */
            }
        }

        public void run() {
            long beforeTime, timeDiff, sleep;
            beforeTime = System.currentTimeMillis();
            int animationDelay = 37;
            long time = System.currentTimeMillis();
            while (true) {// infinite loop
                // spriteManager.update();
                repaint();
                try {
                    time += animationDelay;
                    Thread.sleep(Math.max(0, time - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    System.out.println(e);
                } // end catch
            } // end while loop
        }// end of run

    }// end of class
}
