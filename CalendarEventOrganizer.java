package calendareventorganizer;

import com.toedter.calendar.JCalendar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.Timer;

// Main class following OOP principles
public class CalendarEventOrganizer extends JFrame {

    private JTable eventTable;
    private DefaultTableModel tableModel;
    private JTextField titleField, searchField;
    private JComboBox<String> categoryBox;
    private JSpinner dateSpinner, timeSpinner;
    private JButton addButton, updateButton, deleteButton, resetButton;
    private List<Event> events = new ArrayList<>();
    private Event selectedEvent = null;
    private JCalendar calendar;
    private final String FILE_NAME = "events.dat";

    public CalendarEventOrganizer() {
        setTitle("📅 Calendar & Event Organizer");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        setupTopPanel(mainPanel);
        setupLeftPanel(mainPanel);
        setupCenterTable(mainPanel);
        setupBottomPanel(mainPanel);

        loadEventsFromFile();
        startReminderChecker();
    }

    private void setupTopPanel(JPanel mainPanel) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("🔍 Search");
        searchButton.setBackground(new Color(255, 182, 193));
        searchButton.addActionListener(e -> searchEvents());

        resetButton = new JButton("🔄 Reset View");
        resetButton.setBackground(new Color(220, 220, 255));
        resetButton.addActionListener(e -> resetView());

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(resetButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);
    }

    private void setupLeftPanel(JPanel mainPanel) {
        calendar = new JCalendar();
        calendar.setWeekOfYearVisible(false);
        calendar.setSundayForeground(Color.RED);
        calendar.setTodayButtonVisible(true);

        calendar.getDayChooser().getDayPanel().setUI(new javax.swing.plaf.PanelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);

                Calendar todayCal = Calendar.getInstance();
                int today = todayCal.get(Calendar.DAY_OF_MONTH);
                todayCal.setTime(new Date());
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.setTime(calendar.getDate());
                int selected = selectedCal.get(Calendar.DAY_OF_MONTH);

                Component[] components = calendar.getDayChooser().getDayPanel().getComponents();
                for (Component comp : components) {
                    if (comp instanceof JButton dayButton) {
                        int dayValue;
                        try {
                            dayValue = Integer.parseInt(dayButton.getText());
                        } catch (NumberFormatException e) {
                            continue;
                        }

                        if (dayValue == today) {
                            dayButton.setBackground(new Color(173, 216, 230)); // 💙 Light Blue for today
                        } else if (dayValue == selected) {
                            dayButton.setBackground(new Color(255, 204, 229)); // 💗 Light Pink for selected
                        } else {
                            dayButton.setBackground(Color.WHITE);
                        }

                        dayButton.setForeground(Color.BLACK);
                    }

                }
            }
        });

        calendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            calendar.repaint();
            dateSpinner.setValue(calendar.getDate());
        });

        SwingUtilities.invokeLater(() -> {
            for (Component comp : calendar.getComponents()) {
                if (comp instanceof JPanel panel) {
                    for (Component sub : panel.getComponents()) {
                        if (sub instanceof JButton btn && btn.getText().equalsIgnoreCase("Today")) {
                            btn.setBackground(new Color(250, 190, 230));
                            btn.setForeground(Color.BLACK);
                            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                            btn.addActionListener(e -> {
                                calendar.setDate(new Date());
                                calendar.repaint();
                            });
                        }
                    }
                }
            }
        });

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(300, 400));
        leftPanel.add(calendar, BorderLayout.CENTER);
        mainPanel.add(leftPanel, BorderLayout.WEST);
    }

    private void setupCenterTable(JPanel mainPanel) {
        tableModel = new DefaultTableModel(new String[]{"Title", "Date", "Time", "Category"}, 0);
        eventTable = new JTable(tableModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.getSelectionModel().addListSelectionListener(e -> loadSelectedEvent());

        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.getViewport().setBackground(new Color(255, 247, 255));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupBottomPanel(JPanel mainPanel) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(true);
        bottomPanel.setBackground(new Color(255, 240, 245));

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        fieldsPanel.setOpaque(false);

        titleField = new JTextField();
        categoryBox = new JComboBox<>(new String[]{"Work", "Personal", "Other"});

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        timeSpinner = new JSpinner(timeModel);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "hh:mm a"));

        fieldsPanel.add(new JLabel("Title:"));
        fieldsPanel.add(titleField);
        fieldsPanel.add(new JLabel("Date:"));
        fieldsPanel.add(dateSpinner);

        fieldsPanel.add(new JLabel("Time:"));
        fieldsPanel.add(timeSpinner);
        fieldsPanel.add(new JLabel("Category:"));
        fieldsPanel.add(categoryBox);

        bottomPanel.add(fieldsPanel, BorderLayout.NORTH);

        addButton = new JButton("➕ Add");
        updateButton = new JButton("✏ Update");
        deleteButton = new JButton("🗑 Delete");

        Dimension btnSize = new Dimension(120, 30);
        addButton.setPreferredSize(btnSize);
        updateButton.setPreferredSize(btnSize);
        deleteButton.setPreferredSize(btnSize);

        addButton.setBackground(new Color(144, 238, 144));
        updateButton.setBackground(new Color(255, 255, 102));
        deleteButton.setBackground(new Color(255, 99, 71));

        addButton.addActionListener(e -> addEvent());
        updateButton.addActionListener(e -> updateEvent());
        deleteButton.addActionListener(e -> deleteEvent());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        buttonPanel.setOpaque(false);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addEvent() {
        String title = titleField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        LocalDate date = Instant.ofEpochMilli(((Date) dateSpinner.getValue()).getTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime time = Instant.ofEpochMilli(((Date) timeSpinner.getValue()).getTime())
                .atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.");
            return;
        }

        // Duplicate event check
        for (Event event : events) {
            if (event.getDateTime().equals(LocalDateTime.of(date, time))) {
                JOptionPane.showMessageDialog(this, "❌ Event at this date and time already exists.");
                return;
            }
        }

        Event event = new Event(title, date, time, category);
        events.add(event);
        saveEventsToFile();
        updateTable();
        clearFields();
    }

    private void updateEvent() {
        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "PLEASE SELECT AN EVENT FIRST.");
            return;
        } else {
            selectedEvent.title = titleField.getText().trim();
            selectedEvent.category = (String) categoryBox.getSelectedItem();
            selectedEvent.date = Instant.ofEpochMilli(((Date) dateSpinner.getValue()).getTime())
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            selectedEvent.time = Instant.ofEpochMilli(((Date) timeSpinner.getValue()).getTime())
                    .atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
            saveEventsToFile();
            updateTable();
            clearFields();
        }
    }

    private void deleteEvent() {
        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "PLEASE SELECT AN EVENT FIRST.");
            return;
        } else {
            events.remove(selectedEvent);
            saveEventsToFile();
            updateTable();
            clearFields();
        }
    }

    private void searchEvents() {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        boolean found = false;

        for (Event event : events) {
            if (event.title.toLowerCase().contains(query) || event.category.toLowerCase().contains(query)) {
                tableModel.addRow(event.toTableRow());
                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "❌ EVENT NOT FOUND.");
        }
    }

    private void resetView() {
        searchField.setText("");
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Event event : events) {
            tableModel.addRow(event.toTableRow());
        }
    }

    private void clearFields() {
        titleField.setText("");
        dateSpinner.setValue(new Date());
        timeSpinner.setValue(new Date());
        categoryBox.setSelectedIndex(0);
        selectedEvent = null;
        eventTable.clearSelection();
    }

    private void loadSelectedEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < events.size()) {
            String title = (String) tableModel.getValueAt(selectedRow, 0);
            for (Event event : events) {
                if (event.title.equals(title)) {
                    selectedEvent = event;
                    titleField.setText(event.title);
                    categoryBox.setSelectedItem(event.category);
                    dateSpinner.setValue(Date.from(event.date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    LocalDateTime dt = LocalDateTime.of(event.date, event.time);
                    timeSpinner.setValue(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()));
                    break;
                }
            }
        }
    }

    private void startReminderChecker() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
            for (Event event : events) {
                if (event.getDateTime().equals(now)) {
                    JOptionPane.showMessageDialog(this, "🔔 Reminder: " + event.title + " at " + event.time);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private void saveEventsToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(events);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEventsFromFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                events = (List<Event>) in.readObject();
                updateTable();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarEventOrganizer().setVisible(true));
    }
}

// Event class - abstraction of an event entity
class Event implements Serializable {
    String title;
    LocalDate date;
    LocalTime time;
    String category;
    public Event(String title, LocalDate date, LocalTime time, String category) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(date, time);
    }

    public Object[] toTableRow() {
        return new Object[]{title, date.toString(), time.toString(), category};
    }
}

// Custom JPanel with gradient background - example of inheritance
class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Color color1 = new Color(245, 195, 250);
        Color color2 = new Color(255, 205, 225);
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
