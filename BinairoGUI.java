import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Random;

public class BinairoGUI extends JFrame {
    private BinairoPosition currentPosition;
    private JButton[][] gridButtons;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JPanel gridPanel;
    private JPanel controlPanel;
    private JPanel topPanel;
    private int gridSize = 6;

    private static final Color MAIN_BG = new Color(10, 25, 47);
    private static final Color PANEL_BG = new Color(15, 32, 60);
    private static final Color BORDER_COLOR = new Color(30, 64, 175);

    private static final Color SIDEBAR_BG = new Color(15, 23, 42);
    private static final Color SIDEBAR_HOVER = new Color(30, 64, 175);
    private static final Color SIDEBAR_TEXT = new Color(226, 232, 240);
    private static final Color SIDEBAR_BORDER = new Color(30, 41, 59);

    private static final Color NAVBAR_BG = new Color(15, 23, 42);
    private static final Color NAVBAR_HOVER = new Color(30, 64, 175);
    private static final Color NAVBAR_TEXT = new Color(226, 232, 240);

    private static final Color EMPTY_COLOR = new Color(15, 23, 42);
    private static final Color ZERO_COLOR = new Color(56, 189, 248);
    private static final Color ONE_COLOR = new Color(251, 146, 60);
    private static final Color ERROR_COLOR = new Color(248, 113, 113);

    private static final Color BUTTON_CLEAR = new Color(148, 163, 184);
    private static final Color BUTTON_CHECK = new Color(22, 163, 74);

    private static final Color ACCENT_COLOR = new Color(129, 140, 248);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(250, 204, 21);

    public BinairoGUI() {
        setTitle("Binairo - Jeu de Logique");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(MAIN_BG);

        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(NAVBAR_BG);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        add(topPanel, BorderLayout.NORTH);

        createSidebar();
        createNavbar();
        createControlPanel();
        createGridPanel();
        createStatusPanel();
        createLogPanel();

        pack();
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, SIDEBAR_BORDER),
                BorderFactory.createEmptyBorder(20, 18, 20, 18)
        ));

        JLabel title = new JLabel("BINAIRO");
        title.setForeground(SIDEBAR_TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, SIDEBAR_BORDER),
                BorderFactory.createEmptyBorder(0, 0, 25, 0)
        ));
        sidebar.add(title);

        addSidebarButton(sidebar, "1. Créer grille", e -> createManualGrid());
        addSidebarButton(sidebar, "2. Générer aléatoire", e -> generateRandomGrid());
        addSidebarButton(sidebar, "3. Charger grille", e -> loadGrid());
        addSidebarButton(sidebar, "4. Résoudre manuellement", e -> {
            if (currentPosition != null) {
                JOptionPane.showMessageDialog(this,
                        "Utilisez les cellules pour placer 0 ou 1.",
                        "Mode Manuel", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("Aucune grille chargée!");
            }
        });
        addSidebarButton(sidebar, "5. Résoudre auto", e -> showSolveMenu());
        addSidebarButton(sidebar, "6. Comparer méthodes", e -> {
            if (currentPosition != null) {
                compareMethods();
            } else {
                showError("Aucune grille chargée!");
            }
        });
        addSidebarButton(sidebar, "7. Sauvegarder", e -> saveGrid());

        sidebar.add(Box.createVerticalGlue());

        JButton quitBtn = addSidebarButton(sidebar, "🚪 Quitter", e -> {
            if (JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment quitter?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        quitBtn.setBackground(new Color(127, 29, 29));
        quitBtn.setForeground(SIDEBAR_TEXT);
        quitBtn.setOpaque(true);
        quitBtn.setBorderPainted(false);

        add(sidebar, BorderLayout.WEST);
    }

    private JButton addSidebarButton(JPanel sidebar, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setBackground(SIDEBAR_BG);
        button.setForeground(SIDEBAR_TEXT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBorder(new CompoundBorder(
                new EmptyBorder(10, 14, 10, 14),
                new MatteBorder(0, 0, 1, 0, new Color(31, 41, 55))
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SIDEBAR_BG);
            }
        });

        button.addActionListener(action);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(6));

        return button;
    }

    private void showSolveMenu() {
        if (currentPosition == null) {
            showError("Aucune grille chargée!");
            return;
        }

        String[] methods = {"Backtracking", "Forward Checking (FC)", "AC-3", "AC-4"};
        int choice = JOptionPane.showOptionDialog(this,
                "Choisissez la méthode de résolution:",
                "Résolution automatique",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, methods, methods[1]);

        if (choice == 0) solveAutomatically(GameSearch.BACKTRACKING);
        else if (choice == 1) solveAutomatically(GameSearch.FC);
        else if (choice == 2) solveAutomatically(GameSearch.AC3);
        else if (choice == 3) solveAutomatically(GameSearch.AC4);
    }

    private void createNavbar() {
        JPanel navbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        navbar.setBackground(NAVBAR_BG);
        navbar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        navbar.setPreferredSize(new Dimension(0, 50));

        JLabel groupLabel = new JLabel("Aide");
        groupLabel.setForeground(new Color(148, 163, 184));
        groupLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navbar.add(groupLabel);

        JButton aboutBtn = createNavbarButton("À propos", e -> showAbout());
        navbar.add(aboutBtn);

        topPanel.add(navbar, BorderLayout.NORTH);
    }

    private JButton createNavbarButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(NAVBAR_BG);
        button.setForeground(NAVBAR_TEXT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(NAVBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(NAVBAR_BG);
            }
        });

        button.addActionListener(action);
        return button;
    }

    private void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 12));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JButton btnClearGrid = createControlButton("Effacer grille", BUTTON_CLEAR);
        JButton btnCheck = createControlButton("Vérifier", BUTTON_CHECK);

        btnClearGrid.addActionListener(e -> clearGrid());
        btnCheck.addActionListener(e -> checkViolations());

        controlPanel.add(btnClearGrid);
        controlPanel.add(btnCheck);

        topPanel.add(controlPanel, BorderLayout.SOUTH);
    }

    private JButton createControlButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        Color borderColor = new Color(
                Math.max(0, bgColor.getRed() - 20),
                Math.max(0, bgColor.getGreen() - 20),
                Math.max(0, bgColor.getBlue() - 20)
        );
        button.setBorder(new CompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(8, 22, 8, 22)
        ));
        button.setPreferredSize(new Dimension(150, 38));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);

        Color hoverColor = new Color(
                Math.min(255, bgColor.getRed() + 15),
                Math.min(255, bgColor.getGreen() + 15),
                Math.min(255, bgColor.getBlue() + 15)
        );

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void createGridPanel() {
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(gridSize, gridSize, 5, 5));
        gridPanel.setBackground(MAIN_BG);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        gridButtons = new JButton[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JButton btn = createGridButton(i, j);
                gridButtons[i][j] = btn;
                gridPanel.add(btn);
            }
        }

        add(gridPanel, BorderLayout.CENTER);
    }

    private JButton createGridButton(int row, int col) {
        JButton btn = new JButton("");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 26));
        btn.setPreferredSize(new Dimension(68, 68));
        btn.setBackground(EMPTY_COLOR);
        btn.setForeground(new Color(148, 163, 184));
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(6, 6, 6, 6)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(true);

        btn.addActionListener(e -> handleCellClick(row, col));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.getBackground().equals(EMPTY_COLOR)) {
                    btn.setBackground(new Color(30, 64, 100));
                    btn.setBorder(new CompoundBorder(
                            new LineBorder(new Color(96, 165, 250), 1, true),
                            new EmptyBorder(6, 6, 6, 6)
                    ));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (currentPosition == null || currentPosition.isEmpty(row, col)) {
                    btn.setBackground(EMPTY_COLOR);
                    btn.setBorder(new CompoundBorder(
                            new LineBorder(new Color(51, 65, 85), 1, true),
                            new EmptyBorder(6, 6, 6, 6)
                    ));
                }
            }
        });

        return btn;
    }

    private void handleCellClick(int row, int col) {
        if (currentPosition == null) {
            showError("Veuillez d'abord créer ou charger une grille!");
            return;
        }

        String[] options = {"0", "1", "Effacer"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choisissez la valeur pour la cellule (" + row + ", " + col + "):",
                "Choix de valeur",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.CLOSED_OPTION) return;

        int newValue;
        if (choice == 0) newValue = 0;
        else if (choice == 1) newValue = 1;
        else newValue = -1;

        int oldValue = currentPosition.getValue(row, col);
        if (oldValue != -1 && oldValue != newValue && newValue != -1) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Remplacer " + oldValue + " par " + newValue + " ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        currentPosition.setValue(row, col, newValue);
        updateGridDisplay();

        if (newValue != -1) {
            List<String> violations = currentPosition.getViolations(row, col);
            if (!violations.isEmpty()) {
                StringBuilder msg = new StringBuilder("⚠️ Violations détectées:\n");
                for (String v : violations) {
                    msg.append("• ").append(v).append("\n");
                }
                JOptionPane.showMessageDialog(this, msg.toString(), "Violations", JOptionPane.WARNING_MESSAGE);
            } else {
                log("Valeur " + newValue + " placée en (" + row + ", " + col + ")");
            }
        } else {
            log("Cellule (" + row + ", " + col + ") effacée");
        }

        if (currentPosition.isSolution()) {
            JOptionPane.showMessageDialog(this,
                    " Félicitations! Grille résolue!",
                    "Victoire!", JOptionPane.INFORMATION_MESSAGE);
            log("✓ Grille résolue!");
        }
    }

    private void updateGridDisplay() {
        if (currentPosition == null) return;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JButton btn = gridButtons[i][j];
                int value = currentPosition.getValue(i, j);

                if (value == -1) {
                    btn.setText("");
                    btn.setBackground(EMPTY_COLOR);
                    btn.setForeground(new Color(148, 163, 184));
                } else {
                    btn.setText(String.valueOf(value));
                    Color bgColor = value == 0 ? ZERO_COLOR : ONE_COLOR;
                    btn.setBackground(bgColor);
                    btn.setForeground(new Color(15, 23, 42));
                }

                List<String> violations = currentPosition.getViolations(i, j);
                if (!violations.isEmpty() && value != -1) {
                    btn.setBackground(ERROR_COLOR);
                    btn.setForeground(new Color(24, 24, 27));
                }

                btn.setBorder(new CompoundBorder(
                        new LineBorder(new Color(51, 65, 85), 1, true),
                        new EmptyBorder(6, 6, 6, 6)
                ));
            }
        }

        updateStatus();
    }

    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(15, 23, 42));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        statusLabel = new JLabel("Prêt à commencer");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(148, 163, 184));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void updateStatus() {
        if (currentPosition == null) {
            statusLabel.setText("Aucune grille chargée");
            return;
        }

        if (currentPosition.isSolution()) {
            statusLabel.setText("✓ Grille complète et valide!");
        } else if (currentPosition.isValid()) {
            int empty = currentPosition.getEmptyCount();
            statusLabel.setText("✓ Grille valide - " + empty + " cellules vides");
        } else {
            statusLabel.setText("✗ Grille invalide!");
        }
    }

    private void createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(MAIN_BG);
        logPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        logPanel.setPreferredSize(new Dimension(320, 0));

        JLabel logTitle = new JLabel(" Journal");
        logTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logTitle.setForeground(new Color(226, 232, 240));
        logTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        logArea.setBackground(new Color(15, 23, 42));
        logArea.setForeground(new Color(226, 232, 240));
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logArea.setCaretColor(new Color(248, 250, 252));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(new Color(15, 23, 42));

        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        add(logPanel, BorderLayout.EAST);
    }

    private void log(String message) {
        logArea.append("[" + new java.util.Date().toString().substring(11, 19) + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void createManualGrid() {
        String sizeStr = JOptionPane.showInputDialog(this, "Taille de la grille (6, 8, ou 10):", "6");
        if (sizeStr == null) return;

        try {
            int size = Integer.parseInt(sizeStr);
            if (size != 6 && size != 8 && size != 10) {
                showError("Taille invalide! Utilisation de 6x6 par défaut.");
                size = 6;
            }

            gridSize = size;
            recreateGrid();
            currentPosition = new BinairoPosition(size);
            updateGridDisplay();
            log("Grille " + size + "x" + size + " créée manuellement");
        } catch (NumberFormatException e) {
            showError("Valeur invalide!");
        }
    }

    private void recreateGrid() {
        remove(gridPanel);
        createGridPanel();
        revalidate();
        repaint();
    }

    private void generateRandomGrid() {
        String sizeStr = JOptionPane.showInputDialog(this, "Taille de la grille (6, 8, ou 10):", "6");
        if (sizeStr == null) return;

        try {
            int size = Integer.parseInt(sizeStr);
            if (size != 6 && size != 8 && size != 10) size = 6;

            String[] difficulties = {"Débutant", "Intermédiaire", "Expert"};
            int difficulty = JOptionPane.showOptionDialog(this,
                    "Choisissez la difficulté:",
                    "Difficulté",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, difficulties, difficulties[1]);

            if (difficulty == JOptionPane.CLOSED_OPTION) return;
            difficulty++;

            gridSize = size;
            recreateGrid();

            final int finalSize = size;
            final int finalDifficulty = difficulty;

            log("Génération d'une grille " + finalSize + "x" + finalSize + "...");
            statusLabel.setText("Génération en cours...");

            SwingWorker<BinairoPosition, Void> worker = new SwingWorker<BinairoPosition, Void>() {
                @Override
                protected BinairoPosition doInBackground() {
                    BinairoPosition empty = new BinairoPosition(finalSize);
                    Random random = new Random();

                    int initialValues = finalSize / 2;
                    for (int i = 0; i < initialValues; i++) {
                        int row = random.nextInt(finalSize);
                        int col = random.nextInt(finalSize);
                        int value = random.nextInt(2);
                        empty.setValue(row, col, value);
                    }

                    GameSearch search = new GameSearch(empty);
                    BinairoPosition solved = search.solveForwardChecking();
                    if (solved == null) {
                        search = new GameSearch(empty);
                        solved = search.solveBacktracking();
                    }

                    if (solved == null) return null;

                    int emptyCells = (int) (finalSize * finalSize * (0.3 + finalDifficulty * 0.1));
                    java.util.List<int[]> positions = new java.util.ArrayList<>();
                    for (int i = 0; i < finalSize; i++) {
                        for (int j = 0; j < finalSize; j++) {
                            positions.add(new int[]{i, j});
                        }
                    }
                    java.util.Collections.shuffle(positions);

                    BinairoPosition result = new BinairoPosition(solved);
                    int removed = 0;
                    for (int[] pos : positions) {
                        if (removed >= emptyCells) break;
                        result.setValue(pos[0], pos[1], -1);
                        removed++;
                    }

                    return result;
                }

                @Override
                protected void done() {
                    try {
                        currentPosition = get();
                        if (currentPosition != null) {
                            updateGridDisplay();
                            log("Grille générée avec succès!");
                        } else {
                            showError("Erreur lors de la génération!");
                        }
                    } catch (Exception e) {
                        showError("Erreur: " + e.getMessage());
                    }
                }
            };

            worker.execute();
        } catch (NumberFormatException e) {
            showError("Valeur invalide!");
        }
    }

    private void loadGrid() {
        try {
            File file = new File("binairo_save.txt");
            if (!file.exists()) {
                showError("Aucune grille sauvegardée trouvée!");
                return;
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            currentPosition = BinairoPosition.fromSaveString(content.toString());
            gridSize = currentPosition.getSize();
            recreateGrid();
            updateGridDisplay();
            log("Grille chargée depuis binairo_save.txt");
        } catch (IOException e) {
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void saveGrid() {
        if (currentPosition == null) {
            showError("Aucune grille à sauvegarder!");
            return;
        }

        try {
            try (PrintWriter writer = new PrintWriter(new FileWriter("binairo_save.txt"))) {
                writer.print(currentPosition.toSaveString());
            }
            log("Grille sauvegardée dans binairo_save.txt");
            JOptionPane.showMessageDialog(this, "Grille sauvegardée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            showError("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    private void solveAutomatically(String method) {
        if (currentPosition == null) {
            showError("Aucune grille chargée!");
            return;
        }

        log("Résolution avec " + method + "...");
        statusLabel.setText("Résolution en cours...");

        SwingWorker<BinairoPosition, Void> worker = new SwingWorker<BinairoPosition, Void>() {
            private GameSearch search;

            @Override
            protected BinairoPosition doInBackground() {
                search = new GameSearch(currentPosition);
                BinairoPosition solution = null;

                switch (method) {
                    case GameSearch.BACKTRACKING:
                        solution = search.solveBacktracking();
                        break;
                    case GameSearch.FC:
                        solution = search.solveForwardChecking();
                        break;
                    case GameSearch.AC3:
                        solution = search.solveAC3();
                        break;
                    case GameSearch.AC4:
                        solution = search.solveAC4();
                        break;
                }

                return solution;
            }

            @Override
            protected void done() {
                try {
                    BinairoPosition solution = get();
                    if (solution != null && solution.isSolution()) {
                        int response = JOptionPane.showConfirmDialog(BinairoGUI.this,
                                " Solution trouvée!\n\n" +
                                        "Méthode: " + method + "\n" +
                                        "Nœuds explorés: " + search.getNodesExplored() + "\n" +
                                        "Temps: " + search.getExecutionTime() + " ms\n\n" +
                                        "Remplacer la grille actuelle par la solution?",
                                "Solution trouvée",
                                JOptionPane.YES_NO_OPTION);

                        if (response == JOptionPane.YES_OPTION) {
                            currentPosition = solution;
                            updateGridDisplay();
                        }

                        log("✓ Solution trouvée avec " + method + " (" +
                                search.getNodesExplored() + " nœuds, " +
                                search.getExecutionTime() + " ms)");
                    } else {
                        JOptionPane.showMessageDialog(BinairoGUI.this,
                                " Aucune solution trouvée!\nLa grille pourrait être insoluble.",
                                "Pas de solution",
                                JOptionPane.WARNING_MESSAGE);
                        log("✗ Aucune solution trouvée avec " + method);
                    }
                } catch (Exception e) {
                    showError("Erreur: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void compareMethods() {
        if (currentPosition == null) {
            showError("Aucune grille chargée!");
            return;
        }

        log("Comparaison des méthodes en cours...");
        statusLabel.setText("Comparaison en cours...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                GameSearch.compareMethods(currentPosition);
                return null;
            }

            @Override
            protected void done() {
                log("Comparaison terminée");
                statusLabel.setText("Comparaison terminée - voir la console");
                JOptionPane.showMessageDialog(BinairoGUI.this,
                        " Comparaison terminée!\nVoir la console pour les résultats détaillés.",
                        "Comparaison",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };

        worker.execute();
    }

    private void clearGrid() {
        if (currentPosition == null) return;

        if (JOptionPane.showConfirmDialog(this,
                "Effacer toutes les valeurs de la grille?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    currentPosition.setValue(i, j, -1);
                }
            }
            updateGridDisplay();
            log("Grille effacée");
        }
    }

    private void checkViolations() {
        if (currentPosition == null) {
            showError("Aucune grille chargée!");
            return;
        }

        boolean rule1 = currentPosition.checkRule1();
        boolean rule2 = currentPosition.checkRule2();
        boolean rule3 = currentPosition.checkRule3();

        StringBuilder msg = new StringBuilder("🔍 Vérification des règles:\n\n");
        msg.append("Règle 1 (Max 2 identiques côte à côte): ");
        msg.append(rule1 ? " OK" : "✗ VIOLÉE").append("\n\n");
        msg.append("Règle 2 (Équilibre 0/1): ");
        msg.append(rule2 ? " OK" : "✗ VIOLÉE").append("\n\n");
        msg.append("Règle 3 (Lignes/colonnes uniques): ");
        msg.append(rule3 ? " OK" : "✗ VIOLÉE").append("\n\n");

        if (rule1 && rule2 && rule3) {
            msg.append(" Toutes les règles sont respectées!");
        } else {
            msg.append(" Certaines règles sont violées!");
        }

        JOptionPane.showMessageDialog(this, msg.toString(),
                "Vérification des règles",
                rule1 && rule2 && rule3 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        updateGridDisplay();
    }

    private void showAbout() {
        String aboutText =
                "🎮 Binairo - Jeu de Logique\n\n" +
                        " Règles du jeu:\n" +
                        "  1. Maximum deux chiffres identiques côte à côte\n" +
                        "  2. Même nombre de 0 et 1 dans chaque ligne/colonne\n" +
                        "  3. Aucune ligne/colonne identique\n\n" +
                        " Algorithmes disponibles:\n" +
                        "  • Backtracking\n" +
                        "  • Forward Checking (FC)\n" +
                        "  • AC-3 (Arc Consistency 3)\n" +
                        "  • AC-4 (Arc Consistency 4)\n\n" +
                        " Heuristiques:\n" +
                        "  • MVR (Minimum Remaining Values)\n" +
                        "  • Degree Heuristic\n" +
                        "  • LCV (Least Constraining Value)\n\n" +
                        " Version 2.0 - Interface Dark Modern";

        JOptionPane.showMessageDialog(this, aboutText, "À propos", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BinairoGUI();
        });
    }
}
