package laba4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.UIManager.setLookAndFeel;

public class MainFrame extends JFrame {
    private BufferedImage image;
    private JPanel panel;
    private MainFrame() {
        initComponents();
        int width = 600;
        int height = 600;
        panel.setSize(width, height);
        int centerX = panel.getWidth() / 2 ;
        int centerY = panel.getHeight() / 2;
        int param = 100;
        image=new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, panel.getWidth(),panel.getHeight());
        g.setColor(Color.RED);
        g.setStroke(new MyStroke(2));
        g.draw(new Cissoid(centerX, centerY,param));
    }

    private void initComponents() {
        panel = new JPanel();
        JButton jButton1 = new JButton();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent event)
            {
                formWindowGainedFocus(event);
            }
            public void windowLostFocus(WindowEvent event)
            {
                formWindowLostFocus(event);
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent event)
            {
                formWindowActivated(event);
            }
        });
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt)
            {
                formComponentResized(evt);
            }
            public void componentShown(ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        GroupLayout jPanel1Layout = new GroupLayout(panel);
        panel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 244, Short.MAX_VALUE)
        );
        jButton1.setText("Print");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 325, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        pack();
    }

    private void jButton1ActionPerformed(ActionEvent event)
    {
        printDemoPage();
    }     
    
    private void formComponentResized(ComponentEvent event) {
        panel.getGraphics().drawImage(image, 0, 0, panel);
    }                                     

    private void formComponentShown(ComponentEvent event) {
        panel.getGraphics().drawImage(image, 0, 0, panel);
    } 

    private void formWindowActivated(WindowEvent evt) {
        panel.getGraphics().drawImage(image, 0, 0, panel);
    }                                    

    private void formWindowGainedFocus(WindowEvent evt) {
        panel.getGraphics().drawImage(image, 0, 0, panel);
    }                                      

    private void formWindowLostFocus(WindowEvent evt) {
        panel.getGraphics().drawImage(image, 0, 0, panel);
    }

    private void printDemoPage() {
        MPrint print;
        try {
            print = new MPrint("Cissoid", 14, .75, .75, .75, .75);
            PrintWriter out = new PrintWriter(print);
            int rows = print.getLinesPerPage(), cols = print.getCharactersPerLine();
            out.print("+"); // upper-left corner
            for (int i = 0; i < cols - 2; i++) {
                out.print(" ");
            }
            out.print("+");
            FileReader fr = new FileReader("input.txt");
            Scanner in = new Scanner(fr);
            while(in.hasNextLine()){
                out.println(in.nextLine());
            }
            print.setFontStyle();


        for (int i = 0; i < rows - 30; i++)
            out.println();
        out.print("+");
        for (int i = 0; i < cols - 2; i++)
            out.print(" ");
        out.print("+");
        out.close();
        }
        catch (MPrint.PrintCanceledException ignored){}
        catch (Exception ex){
            System.out.println(ex.toString());
        }
    }
    public static void main(String args[]) {
        try {
            for(UIManager.LookAndFeelInfo info : javax.swing.UIManager.
                getInstalledLookAndFeels())
            {
                if(info.getName().equals("Nimbus"))
                {
                    setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                new MainFrame().setVisible(true);
            }
        });
    }

}
