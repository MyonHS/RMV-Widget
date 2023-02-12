import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {

    MainFrame()
    {

        this.setTitle("RMV Widget");
        this.setSize(400,500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        this.setVisible(true);

        init();
    }

    private void init()
    {
        //no function yet
        JButton arrivalButton = new JButton("ArrivalBoard");
        arrivalButton.setBounds(20,20, 150,50);
        arrivalButton.addActionListener(this);
        arrivalButton.setActionCommand("ArrivalBoard");
        this.add(arrivalButton);

        JButton departureButton = new JButton("DepartureBoard");
        departureButton.setBounds(200,20, 150,50);
        departureButton.addActionListener(this);
        departureButton.setActionCommand("DepartureBoard");
        this.add(departureButton);

        this.repaint();
        this.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getActionCommand().equals("ArrivalBoard")) //not implemented yet
        {

        }
        else if(e.getActionCommand().equals("DepartureBoard"))
        {
            new StationInputFrame();
        }

    }
}
