import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.xml.parsers.ParserConfigurationException;

public class Controller implements ActionListener {
	private Model model;
	private View program;

	public Controller(View vIn) {
		program = vIn;
		model = new Model();
	}
	
    public void loadConfig() {
        program.loadConfig();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == program.forcePullButton) {
			System.out.print("Forcing a Pull from Webscraper!\n");
			try {
				model.XmlFetch();
				//model.EmailSender();
				//need try catch here otherwise error occurs.
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (e.getSource() == program.changeNavigationButton) {
			System.out.print("Converting XML to CSV and back\n");
			try {
				model.XmlToCSV();
				Model.CsvToXml();
				//need try catch here otherwise error occurs.
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (e.getSource() == program.changePullButton) {
			System.out.print("Comparing two XML Files\n");
			try {
				Model.compareXml();
				//need try catch here otherwise error occurs.
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if(e.getSource()== program.getLogsButton) {
			System.out.println("Parsing xml file");
			try {
				model.XMLParser();
				program.updateStatusLabel();
			} catch (ParserConfigurationException e1) {
				System.out.println("Fault = " + e1); 
			}
		}
		if(e.getSource()== program.changeRePullButton) {
			System.out.println("Saving to Config");
			try {
				program.saveConfig();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
