
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class StationInputFrame extends JFrame implements ActionListener {
    JComboBox comboBox;
    JTextField intervalTextField;
    String[] stopList;

    API_Handler.requestType requestType;

    StationInputFrame(API_Handler.requestType type)
    {
        requestType=type;
        this.setTitle("Choose Station");
        this.setSize(500,200);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        JLabel inputTextLabel = new JLabel();
        inputTextLabel.setText("Station: ");
        inputTextLabel.setBounds(5,50,50,20);

        this.add(inputTextLabel);

        stopList = API_Handler.getStopNameArray();

        comboBox = new JComboBox();
        comboBox.setBounds(60,45,300,25);
        AutoCompleteSupport.install(comboBox, GlazedLists.eventListOf(stopList));
        this.add(comboBox);

        JButton okayButton = new JButton();
        okayButton.setBounds(380, 22,60,60);
        okayButton.setText("OK");
        okayButton.setActionCommand("OK");
        okayButton.addActionListener(this);
        this.add(okayButton);

        JLabel intervalTextLabel = new JLabel();
        intervalTextLabel.setText("Interval: ");
        intervalTextLabel.setBounds(5,100,50,20);
        this.add(intervalTextLabel);

        intervalTextField = new JTextField();
        intervalTextField.setText("60");
        intervalTextField.setBounds(60,100,50,20);
        this.add(intervalTextField);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getActionCommand().equals("OK"))
        {
            String interval = intervalTextField.getText();


            String stopName = (String)comboBox.getSelectedItem(); //can only be String

            if(requestType== API_Handler.requestType.DEPARTUREBOARD)
            {
                Vector<String> stops = API_Handler.getDeparturesByStopname(stopName);
                new BoardFrame(stops,"DepartureBoard");
            }
            else if (requestType== API_Handler.requestType.ARRIVALBOARD)
            {
                Vector<String> stops = API_Handler.getArrivalsByStopname(stopName);
                new BoardFrame(stops, "ArrivalBoard");
            }

        }

    }
}
