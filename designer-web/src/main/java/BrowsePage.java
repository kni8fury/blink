import java.applet.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
public class BrowsePage extends JApplet
{

private static final long serialVersionUID = -1623452757240415044L;
public void init() {
JButton browse = new JButton("Browse");
browse.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent evt) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("/Users/rpoosar/"));
            chooser.setDialogTitle("Browse the folder to process");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                System.out.println("getCurrentDirectory(): "+ chooser.getCurrentDirectory());
                System.out.println("getSelectedFile() : "+ chooser.getSelectedFile());
                try {
					File srcFile = new File("/Users/rpoosar/blink/aymar/aymar/target/bin/aymar.jar");
					File dstFile = chooser.getSelectedFile();
					FileInputStream srcStrm = new FileInputStream(srcFile);
					FileOutputStream dstStrm = new FileOutputStream(dstFile);
					byte[] buf = new byte[4096];
					int len;
					while ((len = srcStrm.read(buf)) > 0) {
					dstStrm.write(buf, 0, len);
					}
					srcStrm.close();
					dstStrm.close();
					
					
				}
				catch (IOException ex) {
				}
            } 
            else {
                System.out.println("No Selection ");
            }
}
});


    add(browse);


}
}


