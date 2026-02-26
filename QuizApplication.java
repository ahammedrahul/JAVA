import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuizApplication extends JFrame {
    private Map<String, ArrayList<Question>> quizCategories;
    private ArrayList<Question> questions;
    private int currentQuestionIndex;
    private int score;
    private JLabel questionLabel;
    private JRadioButton[] options;
    private ButtonGroup optionGroup;
    private JButton nextButton;
    private JButton skipButton;
    private JPanel questionPanel;
    private JLabel timerLabel;
    private Timer timer;
    private int timeLeft;
    private JComboBox<String> categoryComboBox;
    private final Map<String, String> categoryToFileMap;

    private static class Question {
        String questionText;
        String[] options;
        int correctOptionIndex;

        Question(String questionText, String[] options, int correctOptionIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
        }
    }

    public QuizApplication() {
        setTitle("Quiz Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        quizCategories = new HashMap<>();
        setBackground(new Color(191, 219, 254)); // Light blue background

        // Initialize category to file mapping
        categoryToFileMap = new HashMap<>();
        categoryToFileMap.put("General Knowledge", "GeneralKnowledge.txt");
        categoryToFileMap.put("Science", "Science.txt");
        categoryToFileMap.put("History", "History.txt");
        categoryToFileMap.put("Sports", "Sports.txt");

        showHomeScreen();
    }

    private void showHomeScreen() {
        getContentPane().removeAll();
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout(40, 40));
        homePanel.setBackground(new Color(191, 219, 254)); // Light blue
        homePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        
        JLabel titleLabel = new JLabel("Welcome to Quiz Master", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(31, 41, 55)); // Dark gray
        homePanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton startQuizButton = createStyledButton("Start Quiz");
        startQuizButton.addActionListener(e -> showStartScreen());
        buttonPanel.add(startQuizButton);

        JButton aboutUsButton = createStyledButton("About Us");
        aboutUsButton.addActionListener(e -> showAboutUsScreen());
        buttonPanel.add(aboutUsButton);

        JButton feedbackButton = createStyledButton("Feedback");
        feedbackButton.addActionListener(e -> showFeedbackScreen());
        buttonPanel.add(feedbackButton);

        homePanel.add(buttonPanel, BorderLayout.CENTER);
        add(homePanel);
        revalidate();
        repaint();
    }

    private void initializeQuizCategories(String fileName) throws IOException {
        quizCategories.clear();
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("Question file not found: " + fileName);
        }
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length != 7) {
                    System.err.println("Skipping malformed line " + lineNumber + ": " + line);
                    continue;
                }
                String category = parts[0].trim();
                String questionText = parts[1].trim();
                String[] options = new String[]{parts[2].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim()};
                boolean validOptions = true;
                for (String option : options) {
                    if (option.isEmpty()) {
                        System.err.println("Skipping line " + lineNumber + ": Empty option detected");
                        validOptions = false;
                        break;
                    }
                }
                if (!validOptions) continue;
                int correctOptionIndex;
                try {
                    correctOptionIndex = Integer.parseInt(parts[6].trim());
                    if (correctOptionIndex < 0 || correctOptionIndex > 3) {
                        throw new NumberFormatException("Correct option index out of range");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Skipping line " + lineNumber + ": Invalid correctOptionIndex");
                    continue;
                }
                quizCategories.computeIfAbsent(category, k -> new ArrayList<>())
                             .add(new Question(questionText, options, correctOptionIndex));
            }
        }
        if (quizCategories.isEmpty()) {
            throw new IOException("No valid questions found in file: " + fileName);
        }
    }

    private void showStartScreen() {
        getContentPane().removeAll();
        JPanel startPanel = new JPanel();
        startPanel.setLayout(new BorderLayout(20, 20));
        startPanel.setBackground(new Color(191, 219, 254)); // Light blue
        startPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Quiz Master", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(31, 41, 55)); // Dark gray
        startPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel selectionPanel = new JPanel();
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setLayout(new GridLayout(3, 1, 10, 15));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Category selection
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.setBackground(Color.WHITE);
        JLabel categoryLabel = new JLabel("Select Quiz Category:");
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        categoryLabel.setForeground(new Color(31, 41, 55));
        categoryComboBox = new JComboBox<>(new String[]{"Select a category"});
        categoryComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        categoryComboBox.setBackground(Color.WHITE);
        categoryComboBox.setForeground(new Color(31, 41, 55));
        for (String category : categoryToFileMap.keySet()) {
            categoryComboBox.addItem(category);
        }
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryComboBox);
        selectionPanel.add(categoryPanel);

        // Start button
        JPanel startButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButtonPanel.setBackground(Color.WHITE);
        JButton startButton = createStyledButton("Start Quiz");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            if (selectedCategory == null || selectedCategory.equals("Select a category")) {
                JOptionPane.showMessageDialog(QuizApplication.this, 
                    "Please select a category.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String fileName = categoryToFileMap.get(selectedCategory);
            try {
                initializeQuizCategories(fileName);
                questions = new ArrayList<>(quizCategories.get(selectedCategory));
                if (questions.isEmpty()) {
                    JOptionPane.showMessageDialog(QuizApplication.this, 
                        "No questions available for this category.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Collections.shuffle(questions);
                currentQuestionIndex = 0;
                score = 0;
                showQuestionScreen();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(QuizApplication.this, 
                    "Error loading questions for " + selectedCategory + ": " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                categoryComboBox.setSelectedIndex(0);
            }
        });
        startButtonPanel.add(startButton);

        // Back to Home button
        JButton backButton = createStyledButton("Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.addActionListener(e -> showHomeScreen());
        startButtonPanel.add(backButton);

        selectionPanel.add(startButtonPanel);
        startPanel.add(selectionPanel, BorderLayout.CENTER);
        add(startPanel);
        revalidate();
        repaint();
    }

    private void showAboutUsScreen() {
        getContentPane().removeAll();
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BorderLayout(20, 20));
        aboutPanel.setBackground(new Color(191, 219, 254)); // Light blue
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("About Us", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(31, 41, 55)); // Dark gray
        aboutPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea aboutText = new JTextArea(
            "Quiz Master is an interactive quiz application designed to test your knowledge across various categories.\n\n" +
            "Features:\n" +
            "- Multiple quiz categories (General Knowledge, Science, History, Sports)\n" +
            "- 60-second timer per question\n" +
            "- Option to skip questions\n" +
            "- Score tracking and result display\n\n" +
            "Created by: Rahul Ahammed\nJatin\n AHIRE CHAITANYA DADAJI\n" +
            "Contact: ahammedrahul@gmail.com"
        );
        aboutText.setFont(new Font("Arial", Font.PLAIN, 16));
        aboutText.setForeground(new Color(31, 41, 55));
        aboutText.setBackground(Color.WHITE);
        aboutText.setEditable(false);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(aboutText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        aboutPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(191, 219, 254)); // Light blue
        JButton backButton = createStyledButton("Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.addActionListener(e -> showHomeScreen());
        buttonPanel.add(backButton);
        aboutPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(aboutPanel);
        revalidate();
        repaint();
    }

    private void showFeedbackScreen() {
        getContentPane().removeAll();
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BorderLayout(20, 20));
        feedbackPanel.setBackground(new Color(191, 219, 254)); // Light blue
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Feedback", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(31, 41, 55)); // Dark gray
        feedbackPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(new Color(31, 41, 55));
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(nameLabel);
        formPanel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(new Color(31, 41, 55));
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        JLabel feedbackLabel = new JLabel("Feedback:");
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        feedbackLabel.setForeground(new Color(31, 41, 55));
        JTextArea feedbackText = new JTextArea(5, 20);
        feedbackText.setFont(new Font("Arial", Font.PLAIN, 16));
        feedbackText.setLineWrap(true);
        feedbackText.setWrapStyleWord(true);
        JScrollPane feedbackScroll = new JScrollPane(feedbackText);
        formPanel.add(feedbackLabel);
        formPanel.add(feedbackScroll);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton submitButton = createStyledButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 18));
        submitButton.addActionListener(e -> {++
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String feedback = feedbackText.getText().trim();
            if (feedback.isEmpty()) {
                JOptionPane.showMessageDialog(QuizApplication.this, 
                    "Please enter feedback before submitting.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("feedback.txt", true))) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.write(String.format("[%s] Name: %s, Email: %s, Feedback: %s", timestamp, name, email, feedback));
                writer.newLine();
                JOptionPane.showMessageDialog(QuizApplication.this, 
                    "Feedback submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                emailField.setText("");
                feedbackText.setText("");
                showHomeScreen();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(QuizApplication.this, 
                    "Error saving feedback: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton backButton = createStyledButton("Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.addActionListener(e -> showHomeScreen());
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);
        formPanel.add(new JLabel()); // Placeholder for alignment
        formPanel.add(buttonPanel);

        feedbackPanel.add(formPanel, BorderLayout.CENTER);
        add(feedbackPanel);
        revalidate();
        repaint();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18)); // Bold, size 18
        button.setBackground(new Color(20, 184, 166)); // Teal background
        button.setForeground(Color.BLUE); // Blue text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24)); // Increased padding
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR
        ));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(45, 212, 191)); // Lighter teal on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(20, 184, 166)); // Revert to teal
            }
        });
        return button;
    }

    private void showQuestionScreen() {
        getContentPane().removeAll();
        questionPanel = new JPanel();
        questionPanel.setLayout(new BorderLayout(15, 15));
        questionPanel.setBackground(new Color(191, 219, 254)); // Light blue
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Question panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionLabel = new JLabel("Question " + (currentQuestionIndex + 1) + ": " + currentQuestion.questionText);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        questionLabel.setForeground(new Color(31, 41, 55));
        topPanel.add(questionLabel, BorderLayout.CENTER);

        // Timer label
        timeLeft = 60;
        timerLabel = new JLabel("Time Left: " + timeLeft + " seconds", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timerLabel.setForeground(new Color(220, 38, 38)); // Red for timer
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(timerLabel, BorderLayout.SOUTH);
        questionPanel.add(topPanel, BorderLayout.NORTH);

        // Options panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setLayout(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        options = new JRadioButton[4];
        optionGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton(currentQuestion.options[i]);
            options[i].setFont(new Font("Arial", Font.PLAIN, 16));
            options[i].setForeground(new Color(31, 41, 55));
            options[i].setBackground(Color.WHITE);
            options[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            options[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            optionGroup.add(options[i]);
            optionsPanel.add(options[i]);
        }
        questionPanel.add(optionsPanel, BorderLayout.CENTER);

        // Next and Skip buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(191, 219, 254)); // Light blue
        nextButton = createStyledButton(currentQuestionIndex < questions.size() - 1 ? "Next" : "Finish");
        nextButton.addActionListener(new NextButtonListener());
        skipButton = createStyledButton("Skip");
        skipButton.addActionListener(new SkipButtonListener());
        buttonPanel.add(skipButton);
        buttonPanel.add(nextButton);
        questionPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Start timer
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time Left: " + timeLeft + " seconds");
            if (timeLeft <= 0) {
                timer.stop();
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    showQuestionScreen();
                } else {
                    showResultScreen();
                }
            }
        });
        timer.start();

        add(questionPanel);
        revalidate();
        repaint();
    }

    private class NextButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            timer.stop();
            boolean optionSelected = false;
            Question currentQuestion = questions.get(currentQuestionIndex);
            for (int i = 0; i < options.length; i++) {
                if (options[i].isSelected()) {
                    optionSelected = true;
                    if (i == currentQuestion.correctOptionIndex) {
                        score++;
                    }
                    break;
                }
            }

            if (!optionSelected) {
                JOptionPane.showMessageDialog(QuizApplication.this, 
                    "Please select an option or click Skip to proceed.", "Warning", JOptionPane.WARNING_MESSAGE);
                timer.start();
                return;
            }

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                showQuestionScreen();
            } else {
                showResultScreen();
            }
        }
    }

    private class SkipButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            timer.stop();
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                showQuestionScreen();
            } else {
                showResultScreen();
            }
        }
    }

    private void showResultScreen() {
        if (timer != null) {
            timer.stop();
        }
        getContentPane().removeAll();
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout(20, 20));
        resultPanel.setBackground(new Color(191, 219, 254)); // Light blue
        resultPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel resultLabel = new JLabel("Quiz Completed!", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 32));
        resultLabel.setForeground(new Color(31, 41, 55));
        resultPanel.add(resultLabel, BorderLayout.NORTH);

        JLabel scoreLabel = new JLabel("Your Score: " + score + " out of " + questions.size(), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        scoreLabel.setForeground(new Color(31, 41, 55));
        resultPanel.add(scoreLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(191, 219, 254)); // Light blue
        JButton restartButton = createStyledButton("Restart Quiz");
        restartButton.setFont(new Font("Arial", Font.BOLD, 18));
        restartButton.addActionListener(e -> showHomeScreen());
        buttonPanel.add(restartButton);
        resultPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(resultPanel);
        revalidate();
        repaint();
 
  }
1
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new QuizApplication().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}