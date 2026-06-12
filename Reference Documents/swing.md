# Swing Cheat Sheet

All Swing/AWT imports used here:

```
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
```

Always build UI on the Event Dispatch Thread:

```
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
}
```

## Containers

```
JFrame frame = new JFrame("Title");
frame.setSize(400, 300);
frame.setLocationRelativeTo(null);          // center on screen
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

JPanel panel = new JPanel();                 // groups components
frame.add(panel);

// JDialog (modal popup window)
JDialog dialog = new JDialog(frame, "Dialog", true);
dialog.setSize(200, 120);
dialog.setVisible(true);
```

## Layouts

```
panel.setLayout(new FlowLayout());           // left-to-right, wraps
panel.setLayout(new BorderLayout());         // NORTH/SOUTH/EAST/WEST/CENTER
panel.setLayout(new GridLayout(3, 2, 5, 5)); // rows, cols, hgap, vgap
panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // vertical stack
panel.setLayout(null);                        // "pixel"/absolute: setBounds(x,y,w,h)

// BorderLayout placement
frame.add(new JButton("Top"), BorderLayout.NORTH);
frame.add(new JButton("Middle"), BorderLayout.CENTER);

// GridBagLayout (flexible grid)
JPanel form = new JPanel(new GridBagLayout());
GridBagConstraints c = new GridBagConstraints();
c.insets = new Insets(5, 5, 5, 5);
c.fill = GridBagConstraints.HORIZONTAL;
c.gridx = 0; c.gridy = 0; form.add(new JLabel("Name:"), c);
c.gridx = 1;              form.add(new JTextField(15), c);
```

## Components

```
JLabel label = new JLabel("Hello");
JButton button = new JButton("Click");
JTextField text = new JTextField(15);
JPasswordField pass = new JPasswordField(15);
String typed = new String(pass.getPassword());

JTextArea area = new JTextArea(5, 20);
area.setLineWrap(true);
JScrollPane scroll = new JScrollPane(area);  // make anything scrollable

JCheckBox check = new JCheckBox("Agree");
boolean on = check.isSelected();

JRadioButton r1 = new JRadioButton("A", true);
JRadioButton r2 = new JRadioButton("B");
ButtonGroup group = new ButtonGroup();        // only one selected at a time
group.add(r1); group.add(r2);

JComboBox<String> combo = new JComboBox<>(new String[]{"One", "Two"});
String picked = (String) combo.getSelectedItem();

JList<String> list = new JList<>(new String[]{"x", "y", "z"});

JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
JSlider slider = new JSlider(0, 100, 50);
JProgressBar bar = new JProgressBar(0, 100);
bar.setValue(40);
JToggleButton toggle = new JToggleButton("Off");
```

## JTable

```
String[] columns = {"ID", "Name", "Balance"};
DefaultTableModel model = new DefaultTableModel(columns, 0) {
    @Override public boolean isCellEditable(int r, int c) { return false; }
};
JTable table = new JTable(model);
model.addRow(new Object[]{"AB000001", "Jane", "1000.00"});
model.setRowCount(0);                          // clear all rows
frame.add(new JScrollPane(table), BorderLayout.CENTER);
```

## Menus and toolbars

```
JMenuBar menuBar = new JMenuBar();
JMenu fileMenu = new JMenu("File");
JMenuItem exitItem = new JMenuItem("Exit");
fileMenu.add(exitItem);
menuBar.add(fileMenu);
frame.setJMenuBar(menuBar);

JToolBar toolBar = new JToolBar();
toolBar.add(new JButton("Save"));
```

## Events / listeners

```
button.addActionListener(e -> System.out.println("clicked"));

field.addActionListener(e -> System.out.println(field.getText())); // Enter pressed

button.addActionListener(new ActionListener() {
    @Override public void actionPerformed(ActionEvent e) { /* ... */ }
});

frame.getRootPane().setDefaultButton(loginButton); // Enter triggers this
```

## Dialogs

```
JOptionPane.showMessageDialog(frame, "Saved", "Info", JOptionPane.INFORMATION_MESSAGE);
JOptionPane.showMessageDialog(frame, "Bad input", "Error", JOptionPane.ERROR_MESSAGE);

int answer = JOptionPane.showConfirmDialog(frame, "Delete?", "Confirm",
        JOptionPane.YES_NO_OPTION);
if (answer == JOptionPane.YES_OPTION) { /* ... */ }

String name = JOptionPane.showInputDialog(frame, "Your name?");

JFileChooser chooser = new JFileChooser();
if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
    java.io.File file = chooser.getSelectedFile();
}

Color color = JColorChooser.showDialog(frame, "Pick a color", Color.WHITE);
```

## Tabs (group multiple panels)

```
JTabbedPane tabs = new JTabbedPane();
tabs.addTab("Form", form);
tabs.addTab("Table", new JScrollPane(table));
frame.add(tabs);
```

## Login window pattern

```
public class LoginPage extends JFrame {
    private final JTextField user = new JTextField(15);
    private final JPasswordField pass = new JPasswordField(15);

    public LoginPage() {
        setLayout(new GridLayout(3, 2, 5, 5));
        add(new JLabel("User:"));  add(user);
        add(new JLabel("Pass:"));  add(pass);
        JButton login = new JButton("Login");
        login.addActionListener(e -> {
            // validate + check credentials, then open next frame
        });
        add(login);
    }
}
```
