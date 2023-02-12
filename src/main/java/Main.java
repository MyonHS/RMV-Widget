public class Main {

    public static void main(String[] args)
    {
        API_Handler.init();
        API_Handler.parseStops();
        MainFrame mainFrame = new MainFrame();
    }


}
