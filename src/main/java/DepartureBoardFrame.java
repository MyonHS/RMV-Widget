import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class DepartureBoardFrame extends JFrame implements ActionListener {

    Vector<String> stopsOfStation;
    public DepartureBoardFrame(Vector<String> stops)
    {
        stopsOfStation = stops;

        this.setTitle("ArrivalBoard");
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
