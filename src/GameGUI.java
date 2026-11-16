import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A parchment-themed graphical user interface for a text adventure game.
 *
 * Layout:
 *   - Top:    banner with the current room name
 *   - Center: "Where you are" (full room description) above the "Story log"
 *   - Right:  items in the room (click one to take it), and inventory
 *   - Bottom: command input, direction buttons, Save/Load buttons, status bar
 *
 * All commands (typed, from buttons, or from clicking an item) go to Game.runGameGUI.
 */
public class GameGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String GAME_TITLE = "Text Adventure";
    private static final String HINTS = "Commands: go, take, use, open, close, read, talk, i, save, load, exit";

    // ----- Parchment color palette -----
    private static final Color FRAME_BG     = new Color(232, 217, 181); // tan background
    private static final Color PAPER        = new Color(245, 235, 211); // panels
    private static final Color PAPER_LIGHT  = new Color(251, 245, 230); // description + input
    private static final Color INK          = new Color(59, 42, 26);    // normal text
    private static final Color DARK_BROWN   = new Color(92, 59, 30);    // banner, titles
    private static final Color BORDER_BROWN = new Color(139, 107, 62);  // strong borders
    private static final Color SOFT_BORDER  = new Color(184, 151, 95);  // light borders
    private static final Color BUTTON_TAN   = new Color(201, 168, 106); // direction buttons
    private static final Color FADED        = new Color(154, 130, 89);  // disabled button text
    private static final Color MUTED_TEXT   = new Color(107, 84, 53);   // hints, status bar
    private static final Color COMMAND_INK  = new Color(47, 93, 80);    // player's commands
    private static final Color ROOM_INK     = new Color(122, 46, 29);   // "You enter the ..."
    private static final Color HIGHLIGHT    = new Color(227, 207, 159); // selection, pressed

    // Use Java's cross-platform look so our button colors show up the same on every computer.
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // If this fails, Swing just keeps its default look.
        }
        UIManager.put("Button.disabledText", FADED);
        UIManager.put("Button.select", HIGHLIGHT);
    }

    private final JLabel locationLabel;
    private final JTextArea descriptionArea;
    private final JTextPane logPane;
    private final StyledDocument logDoc;
    private final SimpleAttributeSet normalStyle;
    private final SimpleAttributeSet commandStyle;
    private final SimpleAttributeSet roomStyle;
    private final DefaultListModel<String> roomItemsModel;
    private final JList<String> roomItemsList;
    private final JTextArea inventoryArea;
    private final JTextField inputField;
    private final JButton enterButton;
    private final JButton saveButton;
    private final JButton loadButton;
    private final JLabel statusLabel;
    private final Timer statusTimer;
    private final Map<String, JButton> directionButtons = new HashMap<>();

    public GameGUI() {
        super(GAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 680);
        setLocationRelativeTo(null); // center on screen

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(FRAME_BG);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        Font bodyFont = new Font(Font.SERIF, Font.PLAIN, 15);
        Font smallFont = new Font(Font.SERIF, Font.PLAIN, 13);

        // ----- Top: room name banner -----
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(DARK_BROWN);
        banner.setBorder(new EmptyBorder(10, 14, 10, 14));

        locationLabel = new JLabel("Unknown");
        locationLabel.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        locationLabel.setForeground(PAPER);

        JLabel titleLabel = new JLabel(GAME_TITLE);
        titleLabel.setFont(smallFont.deriveFont(Font.ITALIC));
        titleLabel.setForeground(HIGHLIGHT);

        banner.add(locationLabel, BorderLayout.WEST);
        banner.add(titleLabel, BorderLayout.EAST);
        root.add(banner, BorderLayout.NORTH);

        // ----- Center top: "Where you are" (full room description) -----
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(bodyFont);
        descriptionArea.setBackground(PAPER_LIGHT);
        descriptionArea.setForeground(INK);
        descriptionArea.setMargin(new Insets(8, 10, 8, 10));
        JScrollPane descriptionScroll = titledScroll(descriptionArea, "Where you are", BORDER_BROWN);
        descriptionScroll.setPreferredSize(new Dimension(0, 230));

        // ----- Center bottom: "Story log" with colored text -----
        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setBackground(PAPER);
        logPane.setMargin(new Insets(8, 10, 8, 10));
        logDoc = logPane.getStyledDocument();
        normalStyle = makeStyle(INK, false);
        commandStyle = makeStyle(COMMAND_INK, false);
        roomStyle = makeStyle(ROOM_INK, true);
        JScrollPane logScroll = titledScroll(logPane, "Story log", SOFT_BORDER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(descriptionScroll, BorderLayout.NORTH);
        centerPanel.add(logScroll, BorderLayout.CENTER);
        root.add(centerPanel, BorderLayout.CENTER);

        // ----- Right: items in the room (clickable) and inventory -----
        roomItemsModel = new DefaultListModel<>();
        roomItemsList = new JList<>(roomItemsModel);
        roomItemsList.setFont(bodyFont);
        roomItemsList.setBackground(PAPER);
        roomItemsList.setForeground(INK);
        roomItemsList.setSelectionBackground(HIGHLIGHT);
        roomItemsList.setSelectionForeground(INK);
        roomItemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomItemsList.setBorder(new EmptyBorder(4, 6, 4, 6));
        roomItemsList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        roomItemsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = roomItemsList.locationToIndex(e.getPoint());
                // Only count clicks that land on an actual item, not the empty space below it
                if (index >= 0 && roomItemsList.getCellBounds(index, index).contains(e.getPoint())) {
                    runCommand("take " + roomItemsModel.get(index));
                }
            }
        });

        JLabel clickHint = new JLabel("Click an item to take it");
        clickHint.setFont(smallFont.deriveFont(Font.ITALIC));
        clickHint.setForeground(MUTED_TEXT);
        clickHint.setBorder(new EmptyBorder(4, 2, 0, 0));

        JPanel roomItemsPanel = new JPanel(new BorderLayout());
        roomItemsPanel.setOpaque(false);
        roomItemsPanel.add(titledScroll(roomItemsList, "In this room", SOFT_BORDER), BorderLayout.CENTER);
        roomItemsPanel.add(clickHint, BorderLayout.SOUTH);

        inventoryArea = new JTextArea("(empty)");
        inventoryArea.setEditable(false);
        inventoryArea.setFocusable(false);
        inventoryArea.setFont(bodyFont);
        inventoryArea.setBackground(PAPER);
        inventoryArea.setForeground(INK);
        inventoryArea.setMargin(new Insets(4, 6, 4, 6));

        JPanel sidePanel = new JPanel(new GridLayout(2, 1, 0, 10));
        sidePanel.setOpaque(false);
        sidePanel.add(roomItemsPanel);
        sidePanel.add(titledScroll(inventoryArea, "Inventory", SOFT_BORDER));
        sidePanel.setPreferredSize(new Dimension(200, 0));
        root.add(sidePanel, BorderLayout.EAST);

        // ----- Bottom row 1: command input -----
        inputField = new JTextField();
        inputField.setFont(bodyFont);
        inputField.setBackground(PAPER_LIGHT);
        inputField.setForeground(INK);
        inputField.setCaretColor(INK);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_BROWN), new EmptyBorder(4, 6, 4, 6)));
        inputField.addActionListener(e -> submitCommand()); // Enter key

        enterButton = makeButton("Enter", DARK_BROWN, PAPER);
        enterButton.addActionListener(e -> submitCommand());

        JLabel prompt = new JLabel(">");
        prompt.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        prompt.setForeground(INK);

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.add(prompt, BorderLayout.WEST);
        inputRow.add(inputField, BorderLayout.CENTER);
        inputRow.add(enterButton, BorderLayout.EAST);

        // ----- Bottom row 2: direction buttons (left) and Save/Load (right) -----
        JPanel directionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        directionPanel.setOpaque(false);
        for (String dir : new String[] {"North", "South", "East", "West", "Up", "Down"}) {
            JButton b = makeButton(dir, BUTTON_TAN, INK);
            b.addActionListener(e -> runCommand("go " + dir.toLowerCase()));
            directionButtons.put(dir.toLowerCase(), b);
            directionPanel.add(b);
        }

        saveButton = makeButton("Save", PAPER, INK);
        saveButton.addActionListener(e -> runCommand("save"));
        loadButton = makeButton("Load", PAPER, INK);
        loadButton.addActionListener(e -> runCommand("load"));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filePanel.setOpaque(false);
        filePanel.add(saveButton);
        filePanel.add(loadButton);

        JPanel buttonRow = new JPanel(new BorderLayout());
        buttonRow.setOpaque(false);
        buttonRow.add(directionPanel, BorderLayout.WEST);
        buttonRow.add(filePanel, BorderLayout.EAST);

        // ----- Bottom row 3: status bar -----
        statusLabel = new JLabel(HINTS);
        statusLabel.setFont(smallFont);
        statusLabel.setForeground(MUTED_TEXT);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, SOFT_BORDER), new EmptyBorder(6, 2, 0, 0)));

        // After a flashed message, go back to showing the command hints
        statusTimer = new Timer(3000, e -> statusLabel.setText(HINTS));
        statusTimer.setRepeats(false);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setOpaque(false);
        bottomPanel.add(inputRow, BorderLayout.NORTH);
        bottomPanel.add(buttonRow, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        root.add(bottomPanel, BorderLayout.SOUTH);

        setAvailableDirections(); // start with every direction greyed out
        inputField.requestFocusInWindow();
    }

    // ==================================================================
    // Methods your game can call to update the display
    // ==================================================================

    /** Adds a normal line to the story log. */
    public void appendText(String text) {
        append(text + "\n", normalStyle);
    }

    /** Adds a highlighted "You enter the ..." line to the story log. */
    public void appendRoomEntry(String roomName) {
        append("You enter the " + roomName + ".\n", roomStyle);
    }

    /** Updates the room name in the banner. */
    public void setLocationName(String name) {
        locationLabel.setText(name);
    }

    /** Shows the full description of the current room in the "Where you are" panel. */
    public void setRoomDescription(String description) {
        descriptionArea.setText(description == null ? "" : description.trim());
        descriptionArea.setCaretPosition(0); // show it from the top
    }

    /** Fills the "In this room" list. Each name can be clicked to take it. */
    public void setRoomItems(String... names) {
        roomItemsModel.clear();
        for (String name : names) {
            roomItemsModel.addElement(name);
        }
    }

    /** Updates the inventory panel (one item per line). */
    public void setInventoryText(String items) {
        inventoryArea.setText(items.isEmpty() ? "(empty)" : items);
    }

    /**
     * Enables only the direction buttons that match the exits in the current room.
     * Example: setAvailableDirections("north", "east") enables North and East,
     * and greys out the rest. Case-insensitive. Call with no arguments to grey out all.
     */
    public void setAvailableDirections(String... exits) {
        for (JButton b : directionButtons.values()) {
            b.setEnabled(false);
        }
        for (String exit : exits) {
            JButton b = directionButtons.get(exit.toLowerCase());
            if (b != null) {
                b.setEnabled(true);
            }
        }
    }

    /** Shows a message in the status bar for a few seconds, e.g. "Game saved." */
    public void flashStatus(String message) {
        statusLabel.setText(message);
        statusTimer.restart();
    }

    /** Locks the controls, then closes the window after a short pause (used by "exit"). */
    public void closeLater() {
        inputField.setEnabled(false);
        enterButton.setEnabled(false);
        saveButton.setEnabled(false);
        loadButton.setEnabled(false);
        roomItemsList.setEnabled(false);
        setAvailableDirections();
        Timer timer = new Timer(1500, e -> System.exit(0));
        timer.setRepeats(false);
        timer.start();
    }

    // ==================================================================
    // Command handling
    // ==================================================================

    private void submitCommand() {
        String command = inputField.getText().trim();
        inputField.setText("");
        if (!command.isEmpty()) {
            runCommand(command);
        }
        inputField.requestFocusInWindow();
    }

    /** Echoes the command, then hands it to the game. The game prints its replies back to us. */
    private void runCommand(String command) {
        append("> " + command + "\n", commandStyle);
        Game.runGameGUI(command);
        appendText(""); // blank line between turns
        inputField.requestFocusInWindow();
    }

    // ==================================================================
    // Helpers for building and styling the window
    // ==================================================================

    /** Adds text to the story log in the given style and scrolls to the bottom. */
    private void append(String text, SimpleAttributeSet style) {
        try {
            logDoc.insertString(logDoc.getLength(), text, style);
        } catch (BadLocationException e) {
            // Can't happen when inserting at the end of the document
        }
        logPane.setCaretPosition(logDoc.getLength());
    }

    /** A text style for the story log: serif font in the given color. */
    private static SimpleAttributeSet makeStyle(Color color, boolean bold) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, Font.SERIF);
        StyleConstants.setFontSize(style, 15);
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        return style;
    }

    /** A flat parchment-style button. */
    private static JButton makeButton(String text, Color background, Color foreground) {
        JButton b = new JButton(text);
        b.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        b.setBackground(background);
        b.setForeground(foreground);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_BROWN), new EmptyBorder(5, 14, 5, 14)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Wraps a component in a scroll pane with a brown titled border. */
    private static JScrollPane titledScroll(JComponent content, String title, Color edge) {
        JScrollPane scroll = new JScrollPane(content);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(edge), title);
        border.setTitleFont(new Font(Font.SERIF, Font.BOLD, 13));
        border.setTitleColor(DARK_BROWN);
        scroll.setBorder(border);
        scroll.setBackground(FRAME_BG);
        scroll.getViewport().setBackground(content.getBackground());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    /** Lets you run this class directly in Eclipse; it just starts the game. */
    public static void main(String[] args) {
        Game.main(args);
    }
}