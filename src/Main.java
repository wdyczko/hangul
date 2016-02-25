import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wdyczko on 9/25/2015.
 */
public class Main {

    private JPanel mainPanel;
    private JTextField tfQuestion;
    private JTextField tfAnswer;
    private JTextField tfLastQuestion;
    private JTextField tfCorrectAnswer;
    private JTextField tfYourAnswer;
    private JTextField tfIsVowel;
    private JTextField tfIsConsonant;
    private JComboBox cbModule;
    private JButton btnSound;
    private JTextField tfDescription;
    private QuestionManager qManager;
    private Question last;

    public Main()
    {
        Font font = new Font("Serif", Font.PLAIN, 30);
        Font small = new Font("Serif", Font.PLAIN, 20);
        final Color normal = tfYourAnswer.getBackground();
        tfQuestion.setFont(font);
        tfQuestion.setHorizontalAlignment(JTextField.CENTER);
        tfLastQuestion.setFont(font);
        tfLastQuestion.setHorizontalAlignment(JTextField.CENTER);
        tfCorrectAnswer.setFont(small);
        tfCorrectAnswer.setHorizontalAlignment(JTextField.CENTER);
        tfAnswer.setFont(small);
        tfAnswer.setHorizontalAlignment(JTextField.CENTER);
        tfYourAnswer.setFont(small);
        tfYourAnswer.setHorizontalAlignment(JTextField.CENTER);
        tfIsVowel.setFont(small);
        tfIsVowel.setHorizontalAlignment(JTextField.CENTER);
        tfIsConsonant.setFont(small);
        tfIsConsonant.setHorizontalAlignment(JTextField.CENTER);
        tfAnswer.grabFocus();
        qManager = new QuestionManager();
        tfDescription.setText(qManager.getModuleDescription());
        tfDescription.setFont(small);
        tfDescription.setHorizontalAlignment(JTextField.CENTER);
        last = qManager.randQuestion();
        tfQuestion.setText(last.getHangul());
        for(String s : qManager.getModules())
        {
            cbModule.addItem(s.replaceFirst("\\.xml", ""));
        }
        cbModule.setFont(small);
        tfAnswer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    tfLastQuestion.setText(last.getHangul());
                    tfCorrectAnswer.setText(last.getPronunciation());
                    tfYourAnswer.setText(tfAnswer.getText());
                    tfIsVowel.setText(last.isVowel() ? "Tak" : "Nie");
                    tfIsConsonant.setText(last.isConsonant() ? "Tak" : "Nie");
                    if(tfAnswer.getText().equals(tfCorrectAnswer.getText()))
                    {
                        tfYourAnswer.setBackground(Color.GREEN);
                    }
                    else
                    {
                        tfYourAnswer.setBackground(Color.RED);
                    }
                    last = qManager.randQuestion();
                    tfQuestion.setText(last.getHangul());
                    tfAnswer.setText("");
                    tfAnswer.grabFocus();
                }
                else if(e.getKeyCode() == KeyEvent.VK_SPACE && e.isShiftDown())
                {
                    tfAnswer.setText("");
                    btnSound.doClick();
                }
            }
        });
        cbModule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = (String)cbModule.getSelectedItem();
                qManager.loadQuestions(name + ".xml");
                last = qManager.randQuestion();
                tfDescription.setText(qManager.getModuleDescription());
                tfQuestion.setText(last.getHangul());
                tfAnswer.setText("");
                tfYourAnswer.setText("");
                tfYourAnswer.setBackground(normal);
                tfCorrectAnswer.setText("");
                tfIsConsonant.setText("");
                tfIsVowel.setText("");
            }
        });
        btnSound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File soundFile = new File(System.getProperty("user.dir") + "\\data\\sounds\\" + last.getHangul() + ".wav");
                playClip(soundFile);
                tfAnswer.grabFocus();
            }
        });
    }

    public static void main(String[] args) {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            System.err.print(e);
        }
        JFrame frame = new JFrame("Hangul v. 1.0");
        frame.setContentPane(new Main().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            frame.setIconImage(ImageIO.read(new File(System.getProperty("user.dir") + "\\data\\icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void playClip(final File clipFile) {
        class AudioListener implements LineListener{
            private boolean done = false;

            @Override
            public synchronized void update(LineEvent event) {
                LineEvent.Type eventType = event.getType();
                if(eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE)
                {
                    done = true;
                    notifyAll();
                }
            }

            public synchronized void waitUntilDone() throws InterruptedException{
                while (!done) { wait(); }
            }
        }

        Thread soundThread = new Thread() {
            @Override
            public void run(){
                AudioListener listener = new AudioListener();
                AudioInputStream audioInputStream = null;
                try {
                    audioInputStream = AudioSystem.getAudioInputStream(clipFile);
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try

                {
                    Clip clip = AudioSystem.getClip();
                    clip.addLineListener(listener);
                    clip.open(audioInputStream);
                    try {
                        clip.start();
                        listener.waitUntilDone();
                    } finally {
                        clip.close();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally

                {
                    try {
                        audioInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        };
        soundThread.start();
    }
}
