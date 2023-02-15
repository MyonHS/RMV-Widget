import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class BoardFrame extends JFrame implements ActionListener {

    String Boardtype;
    Vector<String> stopsOfStation;
    public BoardFrame(Vector<String> stops, String type)
    {
        stopsOfStation = stops;

        if(type.equals("ArrivalBoard")) this.setTitle("ArrivalBoard");
        else this.setTitle("DepartureBoard");

        this.setSize(600,900); //should be enough
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        for(int index=0; index<stopsOfStation.size(); index++)
        {
            JLabel newStop = new JLabel(stopsOfStation.elementAt(index));
            newStop.setBounds(5,5+(20*index), 500,20);
            this.add(newStop);
        }

        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
