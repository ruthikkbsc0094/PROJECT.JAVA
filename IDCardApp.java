import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IDCardApp extends JFrame {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "password123";

    private final JTextField tfName = new JTextField(20);
    private final JTextField tfId = new JTextField(20);
    private final JTextField tfDept = new JTextField(20);
    private final JTextField tfProgram = new JTextField(20);
    private final JTextField tfAddress = new JTextField(20);
    private final JTextField tfBirthday = new JTextField(20);
    private final JTextField tfValid = new JTextField(10);

    private final JLabel photoPreview = new JLabel();
    private BufferedImage userPhoto = null;

    private final IDPreviewPanel previewPanel = new IDPreviewPanel();

    public IDCardApp() {
        super("ID Card Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(870, 560);
        setLocationRelativeTo(null);

        JPanel left = buildInputPanel();
        JPanel right = buildPreviewContainer();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(380);
        add(split);
    }

    private JPanel buildInputPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;

        c.gridx = 0; c.gridy = row; p.add(new JLabel("Name:"), c);
        c.gridx = 1; p.add(tfName, c);

        row++;
        c.gridx = 0; c.gridy = row; p.add(new JLabel("ID Number:"), c);
        c.gridx = 1; p.add(tfId, c);

        row++;
        c.gridx = 0; c.gridy = row; p.add(new JLabel("Department:"), c);
        c.gridx = 1; p.add(tfDept, c);

        row++;
        c.gridx = 0; c.gridy = row; p.add(new JLabel("Program:"), c);
        c.gridx = 1; p.add(tfProgram, c);

        row++;
        c.gridx = 0; c.gridy = row; p.add(new JLabel("Address:"), c);
        c.gridx = 1; p.add(tfAddress, c);

        row++;
        c.gridx = 0; c.gridy = row; p.add(new JLabel("Birthday:"), c);
        c.gridx = 1; p.add(tfBirthday, c);

        row++;
        c.gridx = 0; c.gridy = row; p.add(new JLabel("Valid Till:"), c);
        c.gridx = 1; p.add(tfValid, c);

        row++;
        JButton choosePhoto = new JButton("Upload Photo");
        choosePhoto.addActionListener(e -> onChoosePhoto());
        c.gridx = 0; c.gridy = row; p.add(choosePhoto, c);

        photoPreview.setPreferredSize(new Dimension(120, 140));
        photoPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        c.gridx = 1; p.add(photoPreview, c);

        row++;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnGenerate = new JButton("Generate Preview");
        btnGenerate.addActionListener(e -> onGeneratePreview());
        JButton btnSave = new JButton("Save as PNG");
        btnSave.addActionListener(e -> onSaveImage());
        btnRow.add(btnGenerate);
        btnRow.add(btnSave);

        c.gridx = 0; c.gridy = row + 1; c.gridwidth = 2; p.add(btnRow, c);

        p.setPreferredSize(new Dimension(360, 0));
        return p;
    }

    private JPanel buildPreviewContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createTitledBorder("Preview"));

        previewPanel.setPreferredSize(new Dimension(440, 400));
        container.add(previewPanel, BorderLayout.CENTER);
        return container;
    }

    private void onChoosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                File f = chooser.getSelectedFile();
                userPhoto = ImageIO.read(f);
                ImageIcon icon = new ImageIcon(userPhoto.getScaledInstance(120, 140, Image.SCALE_SMOOTH));
                photoPreview.setIcon(icon);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onGeneratePreview() {
        previewPanel.setUser(new User(
                tfName.getText().trim(),
                tfId.getText().trim(),
                tfDept.getText().trim(),
                tfProgram.getText().trim(),
                tfAddress.getText().trim(),
                tfBirthday.getText().trim(),
                tfValid.getText().trim()
        ));
        previewPanel.setPhoto(userPhoto);
        previewPanel.repaint();
    }

    private void onSaveImage() {
        BufferedImage img = previewPanel.renderToImage();
        if (img == null) {
            JOptionPane.showMessageDialog(this, "Generate a preview first.", "No Preview", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("rv_id_card.png"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                File out = chooser.getSelectedFile();
                ImageIO.write(img, "png", out);
                JOptionPane.showMessageDialog(this, "Saved: " + out.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // LOGIN WINDOW
    private boolean showLoginDialog() {
        JDialog dialog = new JDialog(this, "Login", true);
        dialog.setSize(350, 200);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(null);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        JTextField tfUser = new JTextField(15);
        JPasswordField pfPass = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");

        c.gridx = 0; c.gridy = 0; dialog.add(new JLabel("Username:"), c);
        c.gridx = 1; dialog.add(tfUser, c);
        c.gridx = 0; c.gridy = 1; dialog.add(new JLabel("Password:"), c);
        c.gridx = 1; dialog.add(pfPass, c);

        c.gridx = 1; c.gridy = 2;
        dialog.add(loginBtn, c);

        loginBtn.addActionListener(e -> {
            if (tfUser.getText().trim().equals(ADMIN_USER)
                    && new String(pfPass.getPassword()).equals(ADMIN_PASS)) {
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IDCardApp app = new IDCardApp();
            boolean ok = app.showLoginDialog();
            if (ok) app.setVisible(true);
        });
    }

    static class User {
        String name, id, dept, program, address, birthday, valid;
        User(String name, String id, String dept, String program, String address, String birthday, String valid) {
            this.name = name;
            this.id = id;
            this.dept = dept;
            this.program = program;
            this.address = address;
            this.birthday = birthday;
            this.valid = valid;
        }
    }

    class IDPreviewPanel extends JPanel {
        private User user;
        private BufferedImage photo;

        IDPreviewPanel() {
            setBackground(Color.white);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        public void setUser(User u) { this.user = u; }
        public void setPhoto(BufferedImage p) { this.photo = p; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            BufferedImage id = renderToImage();
            if (id != null) {
                int x = (getWidth() - id.getWidth()) / 2;
                int y = (getHeight() - id.getHeight()) / 2;
                g.drawImage(id, x, y, null);
            }
        }



