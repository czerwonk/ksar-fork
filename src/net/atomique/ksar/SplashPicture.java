

package net.atomique.ksar;


import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author alex
 */
public class SplashPicture extends JPanel {
    
    public static final long serialVersionUID = 501L;
    
    Image img=null;

    public SplashPicture(String file) {
        img = new ImageIcon(getClass().getResource(file)).getImage();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (img == null) {
            return;
        }

        int w = img.getWidth(this);
        int h = img.getHeight(this);
        boolean zoom = ((w > getWidth()) || (h > getHeight()));

        if (zoom) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.drawImage(img, (getWidth() - w) / 2, (getHeight() - h) / 2, this);
        }
    }
}
