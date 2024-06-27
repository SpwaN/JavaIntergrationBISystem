
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class View extends JFrame {
	private Controller controller;
	public JLabel titleLabel, navigateLabel, veckovisPullLabel, rePullLabel, statusLabel;
	public JTextField navigateTextField, veckovisPullTextField, rePullTextField;
	public JButton forcePullButton, getLogsButton, changeNavigationButton, changePullButton, changeRePullButton;
	public Random random;
	private JPanel content;
	
	//Config file load
    private static final String CONFIG_FILE_NAME = "src/config.properties";
    private static final String KEY_NAVIGATE_TEXT = "navigate_text";
    private static final String KEY_VECKOVIS_PULL_TEXT = "veckovis_pull_text";
    private static final String KEY_RE_PULL_TEXT = "re_pull_text";
    private Model model;

	public View() {
		controller = new Controller(this);
		setupLayout(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 400);
		setResizable(false);
		setVisible(true);
		
		//Config file load
		model = new Model();
	    model.loadConfig(CONFIG_FILE_NAME);
	    navigateTextField.setText(model.getConfigValue(KEY_NAVIGATE_TEXT));
	    veckovisPullTextField.setText(model.getConfigValue(KEY_VECKOVIS_PULL_TEXT));
	    rePullTextField.setText(model.getConfigValue(KEY_RE_PULL_TEXT));
	}

	private void setupLayout(JFrame frame) {
		content = new JPanel();
		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(content);
		content.setLayout(null);

		// Titel
		titleLabel = new JLabel("Welcome to Lead intergration collector!");
		titleLabel.setBounds(100, 10, 250, 20);
		content.add(titleLabel);

		// Navigera till Dolibar text, textfï¿½lt och knapp
		navigateLabel = new JLabel("Navigate to Dolibarr HTDOCS");
		navigateLabel.setBounds(50, 80, 250, 20);
		content.add(navigateLabel);

		navigateTextField = new JTextField(20);
		navigateTextField.setBounds(50, 100, 300, 25);
		content.add(navigateTextField);

		changeNavigationButton = new JButton("Change");
		changeNavigationButton.setBounds(350, 100, 75, 25);
		content.add(changeNavigationButton);

		// Navigera till Dolibar text, textfï¿½lt och knapp
		veckovisPullLabel = new JLabel("Time for weekly pull (Default: 01:00)");
		veckovisPullLabel.setBounds(50, 150, 250, 20);
		content.add(veckovisPullLabel);

		veckovisPullTextField = new JTextField(20);
		veckovisPullTextField.setBounds(50, 170, 300, 25);
		content.add(veckovisPullTextField);

		changePullButton = new JButton("Change");
		changePullButton.setBounds(350, 170, 75, 25);
		content.add(changePullButton);

		// Navigera till Dolibar text, textfï¿½lt och knapp
		rePullLabel = new JLabel("Re-Pull by failed Pull (Default: 60 min)");
		rePullLabel.setBounds(50, 220, 250, 20);
		content.add(rePullLabel);

		rePullTextField = new JTextField(20);
		rePullTextField.setBounds(50, 240, 300, 25);
		content.add(rePullTextField);

		changeRePullButton = new JButton("Change");
		changeRePullButton.setBounds(350, 240, 75, 25);
		content.add(changeRePullButton);

		// Force pull knappen lï¿½ngst ner
		forcePullButton = new JButton("Force Pull");
		forcePullButton.setBounds(70, 290, 100, 50);
		content.add(forcePullButton);

		// Get Logs knappen lï¿½ngst ner
		getLogsButton = new JButton("Get Logs");
		getLogsButton.setBounds(220, 290, 100, 50);
		content.add(getLogsButton);
		
		// Status label längst ner
		statusLabel = new JLabel("Show current status/error");
		statusLabel.setBounds(10, 340, 250, 20);
		content.add(statusLabel);

		// Lï¿½gger in actionListeners till alla knapparna
		changeNavigationButton.addActionListener(controller);
		changePullButton.addActionListener(controller);
		changeRePullButton.addActionListener(controller);
		forcePullButton.addActionListener(controller);
		getLogsButton.addActionListener(controller);

		// Lï¿½gger in alla knapparna i programmet.
		changeNavigationButton.setEnabled(true);
		changeNavigationButton.setVisible(true);
		changePullButton.setEnabled(true);
		changePullButton.setVisible(true);
		changeRePullButton.setEnabled(true);
		changeRePullButton.setVisible(true);
		forcePullButton.setEnabled(true);
		forcePullButton.setVisible(true);
		getLogsButton.setEnabled(true);
		getLogsButton.setVisible(true);
	}
	public void updateFields(String navigateText, String veckovisPullText, String rePullText) {
		navigateTextField.setText(navigateText);
		veckovisPullTextField.setText(veckovisPullText);
		rePullTextField.setText(rePullText);
	}
	public String getNavigateText() {
		return navigateTextField.getText();
	}

	public String getVeckovisPullText() {
		return veckovisPullTextField.getText();
	}

	public String getRePullText() {
		return rePullTextField.getText();
	}
    public void loadConfig() {
        model.loadConfig(CONFIG_FILE_NAME);
        String navigateText = model.getConfigValue(KEY_NAVIGATE_TEXT);
        String veckovisPullText = model.getConfigValue(KEY_VECKOVIS_PULL_TEXT);
        String rePullText = model.getConfigValue(KEY_RE_PULL_TEXT);
        updateFields(navigateText, veckovisPullText, rePullText);
    }

    public void saveConfig() {
        String navigateText = getNavigateText();
        String veckovisPullText = getVeckovisPullText();
        String rePullText = getRePullText();
        model.saveConfig(CONFIG_FILE_NAME, KEY_NAVIGATE_TEXT, navigateText);
        model.saveConfig(CONFIG_FILE_NAME, KEY_VECKOVIS_PULL_TEXT, veckovisPullText);
        model.saveConfig(CONFIG_FILE_NAME, KEY_RE_PULL_TEXT, rePullText);
    }
    
    public void updateStatusLabel() {
    	statusLabel.setText(Integer.toString(model.getIncorrectleads()));
    	System.out.println(model.getIncorrectleads());
    }
	 
}
