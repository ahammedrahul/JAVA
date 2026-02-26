import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.*;

public class QuizApplication extends JFrame {
    CardLayout cardLayout;
    JPanel mainPanel;
    String username = ""; 

    public static final Color BG = new Color(15, 23, 42);           
    public static final Color CARD = new Color(30, 41, 59);         
    public static final Color ACCENT = new Color(56, 189, 248);     
    public static final Color SUCCESS = new Color(16, 185, 129);    
    public static final Color TEXT_MAIN = new Color(241, 245, 249); 

    public QuizApplication() {
        setTitle("Elite CBT Assessment System");
        setSize(1150, 750); // Increased width slightly for better sidebar fit
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new StartPanel(this), "Start");
        mainPanel.add(new UsernamePanel(this), "Username");
        mainPanel.add(new CategoryPanel(this), "Category");

        add(mainPanel);
        setVisible(true);
    }

    public void showScreen(String name) { cardLayout.show(mainPanel, name); }

    public void replacePanel(JPanel panel, String name) {
        mainPanel.add(panel, name);
        mainPanel.revalidate();
        mainPanel.repaint();
        showScreen(name);
    }

    public static void styleButton(JButton btn, boolean isPrimary) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (isPrimary) { btn.setBackground(ACCENT); btn.setForeground(Color.BLACK); } 
        else { btn.setBackground(new Color(51, 65, 85)); btn.setForeground(Color.WHITE); }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(QuizApplication::new);
    }
}

class RoundedNavButton extends JButton {
    public RoundedNavButton(String text) {
        super(text);
        setPreferredSize(new Dimension(45, 45));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new Ellipse2D.Double(0, 0, 44, 44));
        super.paintComponent(g2);
        g2.dispose();
    }
}

/* ================= SCREEN PANELS ================= */

class StartPanel extends JPanel {
    StartPanel(QuizApplication frame) {
        setBackground(QuizApplication.BG);
        setLayout(new GridBagLayout());
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("QUIZ MASTER PRO");
        title.setFont(new Font("SansSerif", Font.BOLD, 52));
        title.setForeground(QuizApplication.TEXT_MAIN);
        title.setAlignmentX(0.5f);
        JButton start = new JButton("Launch Assessment") {
            @Override protected void paintComponent(Graphics g) { g.setColor(getBackground()); g.fillRoundRect(0,0,getWidth(),getHeight(), 10, 10); super.paintComponent(g); }
        };
        QuizApplication.styleButton(start, true);
        start.setAlignmentX(0.5f);
        start.addActionListener(e -> frame.showScreen("Username"));
        container.add(title);
        container.add(Box.createRigidArea(new Dimension(0, 30)));
        container.add(start);
        add(container);
    }
}

class UsernamePanel extends JPanel {
    UsernamePanel(QuizApplication frame) {
        setBackground(QuizApplication.BG);
        setLayout(new GridBagLayout());
        JPanel card = new JPanel();
        card.setBackground(QuizApplication.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 60, 40, 60));
        JLabel label = new JLabel("Enter Candidate Name");
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setForeground(QuizApplication.TEXT_MAIN);
        label.setAlignmentX(0.5f);
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(300, 45));
        field.setBackground(QuizApplication.BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(QuizApplication.ACCENT);
        field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JButton cont = new JButton("Proceed") {
            @Override protected void paintComponent(Graphics g) { g.setColor(getBackground()); g.fillRoundRect(0,0,getWidth(),getHeight(), 10, 10); super.paintComponent(g); }
        };
        QuizApplication.styleButton(cont, true);
        cont.setAlignmentX(0.5f);
        cont.addActionListener(e -> {
            if (!field.getText().trim().isEmpty()) {
                frame.username = field.getText().trim();
                frame.showScreen("Category");
            }
        });
        card.add(label); card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(field); card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(cont); add(card);
    }
}

class CategoryPanel extends JPanel {
    CategoryPanel(QuizApplication frame) {
        setBackground(QuizApplication.BG);
        setLayout(new GridBagLayout());
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setOpaque(false);
        String[] categories = {"Java", "Sports", "Science", "History", "General"};
        for (String cat : categories) {
            JButton btn = new JButton(cat) {
                @Override protected void paintComponent(Graphics g) { g.setColor(getBackground()); g.fillRoundRect(0,0,getWidth(),getHeight(), 20, 20); super.paintComponent(g); }
            };
            btn.setPreferredSize(new Dimension(180, 140));
            QuizApplication.styleButton(btn, false);
            btn.setFont(new Font("SansSerif", Font.BOLD, 22));
            btn.addActionListener(e -> frame.replacePanel(new QuizPanel(frame, cat), "Quiz"));
            grid.add(btn);
        }
        add(grid);
    }
}

/* ================= QUIZ PANEL WITH FIXED SIDEBAR ================= */

class QuizPanel extends JPanel {
    QuizApplication frame;
    String category;
    ArrayList<Question> questionList = new ArrayList<>();
    int current = 0, totalTimeLeft;
    int[] questionTimeRemaining;
    JLabel questionLabel, questionTimerLabel, totalTimerLabel, attemptLabel;
    JRadioButton[] choices = new JRadioButton[4];
    ButtonGroup bg = new ButtonGroup();
    RoundedNavButton[] navButtons;
    javax.swing.Timer questionTimer, totalTimer;

    QuizPanel(QuizApplication frame, String category) {
        this.frame = frame;
        this.category = category;
        loadQuestions();
        int totalQuestions = 15;
        questionTimeRemaining = new int[totalQuestions];
        Arrays.fill(questionTimeRemaining, 30);
        totalTimeLeft = totalQuestions * 30;

        setLayout(new BorderLayout());
        setBackground(QuizApplication.BG);

        /* HEADER */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(QuizApplication.CARD);
        header.setBorder(new EmptyBorder(15, 30, 15, 30));
        JLabel catLab = new JLabel(category.toUpperCase() + " ASSESSMENT");
        catLab.setForeground(QuizApplication.ACCENT);
        catLab.setFont(new Font("SansSerif", Font.BOLD, 16));
        JPanel timers = new JPanel(new FlowLayout(FlowLayout.RIGHT, 40, 0));
        timers.setOpaque(false);
        questionTimerLabel = new JLabel(); totalTimerLabel = new JLabel();
        questionTimerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        totalTimerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        questionTimerLabel.setForeground(Color.YELLOW);
        totalTimerLabel.setForeground(new Color(248, 113, 113));
        timers.add(questionTimerLabel); timers.add(totalTimerLabel);
        header.add(catLab, BorderLayout.WEST); header.add(timers, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        /* CENTER */
        JPanel contentArea = new JPanel(new GridBagLayout());
        contentArea.setOpaque(false);
        JPanel qCard = new JPanel();
        qCard.setLayout(new BoxLayout(qCard, BoxLayout.Y_AXIS));
        qCard.setBackground(QuizApplication.CARD);
        qCard.setBorder(new EmptyBorder(40, 50, 40, 50));
        qCard.setPreferredSize(new Dimension(750, 450));
        questionLabel = new JLabel();
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        qCard.add(questionLabel); qCard.add(Box.createRigidArea(new Dimension(0, 40)));
        for (int i = 0; i < 4; i++) {
            choices[i] = new JRadioButton();
            choices[i].setOpaque(false);
            choices[i].setForeground(new Color(203, 213, 225));
            choices[i].setFont(new Font("SansSerif", Font.PLAIN, 17));
            final int index = i;
            choices[i].addActionListener(e -> { questionList.get(current).userAnswer = index; updateLabels(); });
            bg.add(choices[i]); qCard.add(choices[i]); qCard.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        contentArea.add(qCard); add(contentArea, BorderLayout.CENTER);

        /* SIDEBAR - FIXED FOR 15 BUTTONS */
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(20, 30, 48));
        sidebar.setPreferredSize(new Dimension(280, 0));
        attemptLabel = new JLabel("Question Matrix", JLabel.CENTER);
        attemptLabel.setForeground(Color.WHITE);
        attemptLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        attemptLabel.setBorder(new EmptyBorder(20,0,10,0));
        
        // Using GridLayout (5 rows, 3 columns) to force 15 buttons to show
        JPanel gridWrapper = new JPanel(new GridBagLayout());
        gridWrapper.setOpaque(false);
        JPanel gridPanel = new JPanel(new GridLayout(5, 3, 10, 10));
        gridPanel.setOpaque(false);
        
        navButtons = new RoundedNavButton[totalQuestions];
        for (int i = 0; i < totalQuestions; i++) {
            int idx = i;
            navButtons[i] = new RoundedNavButton("" + (i + 1));
            navButtons[i].setBackground(new Color(51, 65, 85));
            navButtons[i].addActionListener(e -> { saveAnswer(); current = idx; loadQuestion(); });
            gridPanel.add(navButtons[i]);
        }
        gridWrapper.add(gridPanel);
        sidebar.add(attemptLabel, BorderLayout.NORTH); 
        sidebar.add(gridWrapper, BorderLayout.CENTER);
        add(sidebar, BorderLayout.EAST);

        /* FOOTER */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        footer.setBackground(QuizApplication.BG);
        JButton prev = new JButton("Previous") { @Override protected void paintComponent(Graphics g) { g.setColor(getBackground()); g.fillRoundRect(0,0,getWidth(),getHeight(), 10,10); super.paintComponent(g); } };
        JButton next = new JButton("Next") { @Override protected void paintComponent(Graphics g) { g.setColor(getBackground()); g.fillRoundRect(0,0,getWidth(),getHeight(), 10,10); super.paintComponent(g); } };
        JButton submit = new JButton("Submit Quiz") { @Override protected void paintComponent(Graphics g) { g.setColor(getBackground()); g.fillRoundRect(0,0,getWidth(),getHeight(), 10,10); super.paintComponent(g); } };
        QuizApplication.styleButton(prev, false); QuizApplication.styleButton(next, false); QuizApplication.styleButton(submit, true);
        submit.setBackground(QuizApplication.SUCCESS);
        prev.addActionListener(e -> previous()); next.addActionListener(e -> next()); submit.addActionListener(e -> submitQuiz());
        footer.add(prev); footer.add(next); footer.add(submit);
        add(footer, BorderLayout.SOUTH);

        questionTimer = new javax.swing.Timer(1000, e -> { if (questionTimeRemaining[current] > 0) questionTimeRemaining[current]--; updateLabels(); if (questionTimeRemaining[current] <= 0) next(); });
        totalTimer = new javax.swing.Timer(1000, e -> { totalTimeLeft--; if (totalTimeLeft <= 0) submitQuiz(); });
        loadQuestion(); questionTimer.start(); totalTimer.start();
    }

    void updateLabels() {
        questionTimerLabel.setText("Q-Time: " + questionTimeRemaining[current] + "s");
        totalTimerLabel.setText("Total: " + String.format("%02d:%02d", totalTimeLeft/60, totalTimeLeft%60));
        for (int i=0; i<navButtons.length; i++) {
            if (questionList.get(i).userAnswer != -1) navButtons[i].setBackground(QuizApplication.SUCCESS);
            else navButtons[i].setBackground(new Color(51, 65, 85));
            if (i == current) navButtons[i].setBorder(new LineBorder(QuizApplication.ACCENT, 2));
            else navButtons[i].setBorder(null);
        }
    }

    void loadQuestion() {
        Question q = questionList.get(current);
        questionLabel.setText("<html><body style='width:550px'>" + (current+1) + ". " + q.question + "</body></html>");
        bg.clearSelection(); 
        for (int i = 0; i < 4; i++) { choices[i].setText(q.options.get(i)); if (q.userAnswer == i) choices[i].setSelected(true); }
        updateLabels();
    }

    void saveAnswer() { for (int i = 0; i < 4; i++) if (choices[i].isSelected()) questionList.get(current).userAnswer = i; }
    void next() { saveAnswer(); if (current < 14) { current++; loadQuestion(); } }
    void previous() { saveAnswer(); if (current > 0) { current--; loadQuestion(); } }
    void submitQuiz() { questionTimer.stop(); totalTimer.stop(); saveAnswer(); int s = 0; for (Question q : questionList) if (q.userAnswer == q.correctAnswer) s++; frame.replacePanel(new ResultPanel(frame, frame.username, s, 15), "Result"); }

    void loadQuestions() {
        if (category.equals("Java")) {
            questionList.add(new Question("Which component is used to compile, debug and execute Java programs?", new String[]{"JRE", "JIT", "JDK", "JVM"}, 2));
            questionList.add(new Question("Which keyword is used for accessing the features of a package?", new String[]{"package", "import", "extends", "export"}, 1));
            questionList.add(new Question("Which of these is not a primitive data type?", new String[]{"int", "double", "char", "String"}, 3));
            questionList.add(new Question("Which operator is used to create an object?", new String[]{"class", "new", "alloc", "malloc"}, 1));
            questionList.add(new Question("What is the default value of double variable?", new String[]{"0.0d", "0.0f", "0", "not defined"}, 0));
            questionList.add(new Question("Which method is called only once in applet life cycle?", new String[]{"start", "init", "stop", "paint"}, 1));
            questionList.add(new Question("Which of these cannot be used for a variable name?", new String[]{"identifier", "keyword", "underscore", "none"}, 1));
            questionList.add(new Question("Which class is available to all classes automatically?", new String[]{"Swing", "Applet", "Object", "ActionEvent"}, 2));
            questionList.add(new Question("Which package contains the Random class?", new String[]{"java.util", "java.lang", "java.io", "java.net"}, 0));
            questionList.add(new Question("Which keyword makes a value unchangeable?", new String[]{"static", "final", "const", "volatile"}, 1));
            questionList.add(new Question("Java source code is compiled into ______.", new String[]{"Machine code", "Bytecode", "ASCII", "Assembly"}, 1));
            questionList.add(new Question("Which of these is a synchronized collection?", new String[]{"ArrayList", "Vector", "HashSet", "LinkedList"}, 1));
            questionList.add(new Question("Which keyword is used to define an interface?", new String[]{"implements", "extends", "interface", "abstract"}, 2));
            questionList.add(new Question("Which method must be implemented by all threads?", new String[]{"start()", "run()", "stop()", "main()"}, 1));
            questionList.add(new Question("What is the size of 'char' in Java?", new String[]{"8-bit", "16-bit", "32-bit", "4-bit"}, 1));
        } else if (category.equals("Sports")) {
            questionList.add(new Question("Who is the only cricketer to score 100 international centuries?", new String[]{"Ponting", "Tendulkar", "Kohl", "Lara"}, 1));
            questionList.add(new Question("How many players are on a soccer team on the field?", new String[]{"9", "10", "11", "12"}, 2));
            questionList.add(new Question("Which city hosted the first modern Olympic Games?", new String[]{"Athens", "Paris", "London", "Rome"}, 0));
            questionList.add(new Question("What is the standard length of a pool for Olympic swimming?", new String[]{"25m", "50m", "75m", "100m"}, 1));
            questionList.add(new Question("Which sport uses the term 'Love' for a zero score?", new String[]{"Badminton", "Tennis", "Squash", "Cricket"}, 1));
            questionList.add(new Question("Who won the FIFA World Cup in 2018?", new String[]{"Germany", "France", "Brazil", "Argentina"}, 1));
            questionList.add(new Question("Which country has won the most Hockey World Cups?", new String[]{"India", "Pakistan", "Australia", "Netherlands"}, 1));
            questionList.add(new Question("The term 'Double Fault' is associated with?", new String[]{"Basketball", "Tennis", "Football", "Hockey"}, 1));
            questionList.add(new Question("How many minutes is a standard Kabaddi match?", new String[]{"20", "40", "60", "30"}, 1));
            questionList.add(new Question("Who is the fastest man on Earth?", new String[]{"Yohan Blake", "Usain Bolt", "Tyson Gay", "Justin Gatlin"}, 1));
            questionList.add(new Question("Which team won the first IPL title?", new String[]{"CSK", "MI", "Rajasthan Royals", "KKR"}, 2));
            questionList.add(new Question("What is the maximum number of sets in Men's Grand Slam?", new String[]{"3", "4", "5", "6"}, 2));
            questionList.add(new Question("National game of USA?", new String[]{"Football", "Baseball", "Basketball", "Ice Hockey"}, 1));
            questionList.add(new Question("In which sport do you use a 'Puck'?", new String[]{"Polo", "Ice Hockey", "Golf", "Curling"}, 1));
            questionList.add(new Question("Who is called 'The Wall' in Cricket?", new String[]{"Dravid", "Ganguly", "Sehwag", "Kohli"}, 0));
        } else if (category.equals("Science")) {
            questionList.add(new Question("What is the chemical formula for Ozone?", new String[]{"O2", "O3", "CO2", "H2O"}, 1));
            questionList.add(new Question("Which planet has the most moons?", new String[]{"Jupiter", "Saturn", "Neptune", "Mars"}, 1));
            questionList.add(new Question("What is the atomic number of Carbon?", new String[]{"4", "5", "6", "7"}, 2));
            questionList.add(new Question("Which organ is responsible for filtering blood?", new String[]{"Heart", "Lungs", "Kidneys", "Liver"}, 2));
            questionList.add(new Question("Who is known as the Father of Evolution?", new String[]{"Mendel", "Darwin", "Lamarck", "Pasteur"}, 1));
            questionList.add(new Question("What is the SI unit of Force?", new String[]{"Joule", "Pascal", "Newton", "Watt"}, 2));
            questionList.add(new Question("Which gas is responsible for global warming?", new String[]{"Oxygen", "Nitrogen", "CO2", "Argon"}, 2));
            questionList.add(new Question("Hardest natural substance?", new String[]{"Graphite", "Gold", "Diamond", "Platinum"}, 2));
            questionList.add(new Question("Which part of the brain controls balance?", new String[]{"Cerebrum", "Cerebellum", "Medulla", "Thalamus"}, 1));
            questionList.add(new Question("Main component of LPG?", new String[]{"Methane", "Ethane", "Butane", "Propane"}, 2));
            questionList.add(new Question("Which mirror is used in car headlights?", new String[]{"Plane", "Convex", "Concave", "Cylindrical"}, 2));
            questionList.add(new Question("Human body temperature in Celsius?", new String[]{"35", "37", "39", "40"}, 1));
            questionList.add(new Question("Which instrument measures air pressure?", new String[]{"Ammeter", "Barometer", "Voltmeter", "Lactometer"}, 1));
            questionList.add(new Question("The value of 'g' is maximum at?", new String[]{"Equator", "Poles", "Center of Earth", "Space"}, 1));
            questionList.add(new Question("Study of fungi is called?", new String[]{"Mycology", "Phycology", "Virology", "Cytology"}, 0));
        } else if (category.equals("History")) {
            questionList.add(new Question("Who was the last Mughal Emperor?", new String[]{"Akbar II", "Bahadur Shah Zafar", "Aurangzeb", "Shah Alam"}, 1));
            questionList.add(new Question("First Battle of Panipat was in?", new String[]{"1526", "1556", "1761", "1520"}, 0));
            questionList.add(new Question("The slogan 'Do or Die' was given by?", new String[]{"Bose", "Nehru", "Gandhi", "Tilak"}, 2));
            questionList.add(new Question("Who founded the Mauryan Empire?", new String[]{"Ashoka", "Chandragupta", "Bindusara", "Kanishka"}, 1));
            questionList.add(new Question("World War II ended in which year?", new String[]{"1942", "1945", "1948", "1950"}, 1));
            questionList.add(new Question("First woman Prime Minister in the world?", new String[]{"Indira Gandhi", "Sirimavo Bandaranaike", "Margaret Thatcher", "Golda Meir"}, 1));
            questionList.add(new Question("The capital of France is?", new String[]{"Berlin", "London", "Paris", "Madrid"}, 2));
            questionList.add(new Question("Magna Carta was signed in?", new String[]{"1215", "1315", "1415", "1115"}, 0));
            questionList.add(new Question("Who discovered the sea route to India?", new String[]{"Columbus", "Vasco da Gama", "Magellan", "Cook"}, 1));
            questionList.add(new Question("Renaissance movement first started in?", new String[]{"France", "Italy", "England", "Germany"}, 1));
            questionList.add(new Question("The Indus Valley people worshiped?", new String[]{"Indra", "Pashupati", "Vishnu", "Varuna"}, 1));
            questionList.add(new Question("Who wrote the play 'Hamlet'?", new String[]{"Milton", "Shakespeare", "Dante", "Homer"}, 1));
            questionList.add(new Question("The Statue of Liberty was gifted by?", new String[]{"UK", "France", "Germany", "Italy"}, 1));
            questionList.add(new Question("Industrial Revolution started in?", new String[]{"USA", "Germany", "Great Britain", "France"}, 2));
            questionList.add(new Question("The Great Wall of China was built by?", new String[]{"Qin Shi Huang", "Han Wudi", "Kublai Khan", "Ming"}, 0));
        } else {
            questionList.add(new Question("Which is the largest coffee producer country?", new String[]{"India", "Vietnam", "Brazil", "Colombia"}, 2));
            questionList.add(new Question("What is the capital of Canada?", new String[]{"Toronto", "Vancouver", "Ottawa", "Montreal"}, 2));
            questionList.add(new Question("Currency of Russia?", new String[]{"Euro", "Ruble", "Lira", "Dollar"}, 1));
            questionList.add(new Question("Which country has the most population?", new String[]{"China", "India", "USA", "Indonesia"}, 1));
            questionList.add(new Question("Who invented the Telephone?", new String[]{"Edison", "Bell", "Tesla", "Marconi"}, 1));
            questionList.add(new Question("Largest bone in human body?", new String[]{"Skull", "Femur", "Spine", "Tibia"}, 1));
            questionList.add(new Question("Which is the largest freshwater lake?", new String[]{"Superior", "Victoria", "Baikal", "Caspian"}, 0));
            questionList.add(new Question("The headquarter of UN is in?", new String[]{"Geneva", "Paris", "New York", "London"}, 2));
            questionList.add(new Question("Author of 'Wings of Fire'?", new String[]{"Nehru", "APJ Abdul Kalam", "Gandhi", "Khushwant Singh"}, 1));
            questionList.add(new Question("Which city is known as Pink City?", new String[]{"Udaipur", "Jaipur", "Jodhpur", "Mysore"}, 1));
            questionList.add(new Question("Number of keys on a standard piano?", new String[]{"76", "84", "88", "92"}, 2));
            questionList.add(new Question("Which is the smallest continent?", new String[]{"Europe", "Australia", "Antarctica", "Africa"}, 1));
            questionList.add(new Question("Which color is a symbol of peace?", new String[]{"Blue", "Green", "White", "Yellow"}, 2));
            questionList.add(new Question("How many states are in India?", new String[]{"27", "28", "29", "30"}, 1));
            questionList.add(new Question("Which gas is filled in balloons?", new String[]{"Oxygen", "Helium", "Hydrogen", "Nitrogen"}, 1));
        }
    }
}

class Question {
    String question; ArrayList<String> options; int correctAnswer; int userAnswer = -1;
    Question(String q, String[] o, int c) { question = q; options = new ArrayList<>(Arrays.asList(o)); correctAnswer = c; }
}

class ResultPanel extends JPanel {
    ResultPanel(QuizApplication frame, String user, int score, int total) {
        setBackground(QuizApplication.BG); setLayout(new GridBagLayout());
        JPanel card = new JPanel(); card.setBackground(QuizApplication.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); card.setBorder(new EmptyBorder(50, 80, 50, 80));
        JLabel congrats = new JLabel("Well Done, " + user + "!");
        congrats.setForeground(QuizApplication.ACCENT); congrats.setFont(new Font("SansSerif", Font.BOLD, 26)); congrats.setAlignmentX(0.5f);
        JLabel res = new JLabel(score + " / " + total);
        res.setForeground(QuizApplication.SUCCESS); res.setFont(new Font("SansSerif", Font.BOLD, 72)); res.setAlignmentX(0.5f);
        JButton home = new JButton("Try Another Category") { @Override protected void paintComponent(Graphics g) { g.setColor(getBackground()); g.fillRoundRect(0,0,getWidth(),getHeight(), 10,10); super.paintComponent(g); } };
        QuizApplication.styleButton(home, true); home.setAlignmentX(0.5f);
        home.addActionListener(e -> frame.showScreen("Category"));
        card.add(congrats); card.add(Box.createRigidArea(new Dimension(0, 30))); card.add(res); card.add(Box.createRigidArea(new Dimension(0, 40))); card.add(home);
        add(card);
    }
}