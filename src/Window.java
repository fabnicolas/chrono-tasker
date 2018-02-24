import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


public class Window extends JFrame{
	private static final long serialVersionUID = -8255319694373975038L;
	public JTextField textfield_timer;
	public JCheckBox checkbox_disable;

	public Window() {
		initComponents();
	}

	public void initComponents() {
		JPanel panel_frame = new JPanel();
		panel_frame.setLayout(new FlowLayout());

		JLabel label_timer = new JLabel("Insert timer: ");
		panel_frame.add(label_timer);
		textfield_timer = new JTextField("40");
		textfield_timer.setColumns(4);
		panel_frame.add(textfield_timer);
		checkbox_disable = new JCheckBox("Disable starting audio");
		panel_frame.add(checkbox_disable);

		add(panel_frame);
		setTitle("chrono-tasker v1.0");
		setResizable(false);
		centerWindow();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void centerWindow() {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int window_width=200;
		int window_height=100;
		setBounds((int)((d.getWidth()-window_width)/2), (int)((d.getHeight()-window_height)/2), window_width, window_height);
	}

	public void showWindow() {
		if(!isVisible()) {
			centerWindow();
			setVisible(true);
		}
	}

	public void hideWindow() {
		setVisible(false);
	}
}
