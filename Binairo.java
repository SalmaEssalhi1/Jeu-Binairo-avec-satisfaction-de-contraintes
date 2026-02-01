import java.util.*;
import java.io.*;

/**
 * Classe principale du jeu Binairo
 * Gère l'interface utilisateur en mode terminal et la logique du jeu
 */
public class Binairo {
    private BinairoPosition currentPosition;
    private Scanner scanner;
    private static final String SAVE_FILE = "binairo_save.txt";
    
    /**
     * Constructeur
     */
    public Binairo() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Point d'entrée principal du programme
     * Lance le jeu Binairo
     */
    public static void main(String[] args) {
        Binairo game = new Binairo();
        game.run();
    }
    
    /**
     * Boucle principale du jeu
     * Affiche le menu et gère les choix de l'utilisateur
     */
    public void run() {
        System.out.println("=== JEU BINAIRO (TAKUZU/BINERO) ===");
        System.out.println("Jeu de logique avec satisfaction de contraintes\n");
        
        while (true) {
            showMainMenu();
            int choice = getIntInput("Votre choix: ");
            
            switch (choice) {
                case 1:
                    createManualGrid();
                    break;
                case 2:
                    generateRandomGrid();
                    break;
                case 3:
                    loadGrid();
                    break;
                case 4:
                    if (currentPosition != null) {
                        playManually();
                    } else {
                        System.out.println("Aucune grille chargée!");
                    }
                    break;
                case 5:
                    if (currentPosition != null) {
                        solveAutomatically();
                    } else {
                        System.out.println("Aucune grille chargée!");
                    }
                    break;
                case 6:
                    if (currentPosition != null) {
                        compareMethods();
                    } else {
                        System.out.println("Aucune grille chargée!");
                    }
                    break;
                case 7:
                    if (currentPosition != null) {
                        saveGrid();
                    } else {
                        System.out.println("Aucune grille chargée!");
                    }
                    break;
                case 8:
                    System.out.println("Au revoir!");
                    return;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }
    
    /**
     * Affiche le menu principal avec les 8 options disponibles
     */
    private void showMainMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Créer une grille manuellement");
        System.out.println("2. Générer une grille aléatoire");
        System.out.println("3. Charger une grille sauvegardée");
        System.out.println("4. Résoudre manuellement");
        System.out.println("5. Résoudre automatiquement");
        System.out.println("6. Comparer les méthodes de résolution");
        System.out.println("7. Sauvegarder la grille");
        System.out.println("8. Quitter");
    }
    
    /**
     * Crée une grille manuellement
     * L'utilisateur entre les valeurs une par une
     */
    private void createManualGrid() {
        int size = getIntInput("Taille de la grille (6, 8, ou 10): ");
        if (size != 6 && size != 8 && size != 10) {
            System.out.println("Taille invalide! Utilisation de 6x6 par défaut.");
            size = 6;
        }
        
        currentPosition = new BinairoPosition(size);
        
        System.out.println("\nEntrez les valeurs initiales:");
        System.out.println("Format: ligne colonne valeur (0 ou 1)");
        System.out.println("Tapez 'fin' pour terminer");
        System.out.println("Exemple: 0 0 1");
        
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("fin")) {
                break;
            }
            
            String[] parts = input.split("\\s+");
            if (parts.length == 3) {
                try {
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    int value = Integer.parseInt(parts[2]);
                    
                    if (row >= 0 && row < size && col >= 0 && col < size && 
                        (value == 0 || value == 1)) {
                        currentPosition.setValue(row, col, value);
                        displayGrid();
                    } else {
                        System.out.println("Valeurs invalides!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Format invalide!");
                }
            } else {
                System.out.println("Format: ligne colonne valeur");
            }
        }
        
        if (validateInitialGrid()) {
            System.out.println("Grille créée avec succès!");
        } else {
            System.out.println("ATTENTION: La grille initiale n'est pas valide ou résolvable!");
        }
    }
    
    /**
     * Génère une grille aléatoire résolvable
     * Crée d'abord une grille complète résolue, puis retire des cellules selon la difficulté
     */
    private void generateRandomGrid() {
        int size = getIntInput("Taille de la grille (6, 8, ou 10): ");
        if (size != 6 && size != 8 && size != 10) {
            size = 6;
        }
        
        int difficulty = getIntInput("Difficulté (1=Débutant, 2=Intermédiaire, 3=Expert): ");
        int emptyCells;
        
        switch (difficulty) {
            case 1:
                emptyCells = (int)(size * size * 0.4); // 40% vides
                break;
            case 2:
                emptyCells = (int)(size * size * 0.5); // 50% vides
                break;
            case 3:
                emptyCells = (int)(size * size * 0.6); // 60% vides
                break;
            default:
                emptyCells = (int)(size * size * 0.5);
        }
        
        System.out.println("Génération d'une grille résolue...");
        
        // Générer une grille complète valide
        BinairoPosition solved = generateSolvedGrid(size);
        
        if (solved == null) {
            System.out.println("Erreur lors de la génération!");
            return;
        }
        
        // Retirer des cellules aléatoirement pour créer le puzzle
        List<int[]> positions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                positions.add(new int[]{i, j});
            }
        }
        
        Collections.shuffle(positions);
        
        currentPosition = new BinairoPosition(solved);
        int removed = 0;
        for (int[] pos : positions) {
            if (removed >= emptyCells) break;
            currentPosition.setValue(pos[0], pos[1], -1);
            removed++;
        }
        
        System.out.println("Grille générée avec succès!");
        displayGrid();
    }
    
    /**
     * Génère une grille complète résolue
     * Place quelques valeurs aléatoires puis utilise un algorithme de résolution
     */
    private BinairoPosition generateSolvedGrid(int size) {
        BinairoPosition grid = new BinairoPosition(size);
        Random random = new Random();
        
        // Placer quelques valeurs initiales pour accélérer la résolution
        int initialValues = size / 2;
        for (int i = 0; i < initialValues; i++) {
            int row = random.nextInt(size);
            int col = random.nextInt(size);
            int value = random.nextInt(2);
            grid.setValue(row, col, value);
        }
        
        // Essayer Forward Checking d'abord (plus rapide)
        GameSearch search = new GameSearch(grid);
        BinairoPosition solution = search.solveForwardChecking();
        
        // Si échec, essayer Backtracking
        if (solution == null || !solution.isSolution()) {
            search = new GameSearch(grid);
            solution = search.solveBacktracking();
        }
        
        return solution;
    }
    
    /**
     * Charge une grille sauvegardée depuis le fichier binairo_save.txt
     */
    private void loadGrid() {
        try {
            File file = new File(SAVE_FILE);
            if (!file.exists()) {
                System.out.println("Aucune grille sauvegardée trouvée!");
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
            System.out.println("Grille chargée avec succès!");
            displayGrid();
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement: " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde la grille actuelle dans le fichier binairo_save.txt
     */
    private void saveGrid() {
        try {
            try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
                writer.print(currentPosition.toSaveString());
            }
            System.out.println("Grille sauvegardée dans " + SAVE_FILE);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }
    
    /**
     * Mode de résolution manuelle
     * L'utilisateur peut placer des valeurs directement avec des commandes simples
     */
    private void playManually() {
        System.out.println("\n=== RÉSOLUTION MANUELLE ===");
        System.out.println("Commandes:");
        System.out.println("  set ligne colonne valeur - Placer une valeur");
        System.out.println("  clear ligne colonne - Effacer une valeur");
        System.out.println("  check - Vérifier les violations");
        System.out.println("  help ligne colonne - Obtenir de l'aide");
        System.out.println("  quit - Retour au menu");
        
        while (true) {
            displayGrid();
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");
            
            if (parts.length == 0) continue;
            
            String command = parts[0].toLowerCase();
            
            // Détection automatique: si 3 nombres sont entrés, traiter comme "set"
            if (parts.length == 3) {
                try {
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    int value = Integer.parseInt(parts[2]);
                    makeMove(row, col, value);
                    if (currentPosition.isSolution()) {
                        System.out.println("\n🎉 Félicitations! Vous avez résolu la grille!");
                        return;
                    }
                    continue;
                } catch (NumberFormatException e) {
                    // Continuer avec les commandes normales
                }
            }
            
            // Format compact: "001" = ligne 0, colonne 0, valeur 1
            if (parts.length == 1 && parts[0].length() == 3) {
                try {
                    String compact = parts[0];
                    int row = Integer.parseInt(compact.substring(0, 1));
                    int col = Integer.parseInt(compact.substring(1, 2));
                    int value = Integer.parseInt(compact.substring(2, 3));
                    makeMove(row, col, value);
                    if (currentPosition.isSolution()) {
                        System.out.println("\n🎉 Félicitations! Vous avez résolu la grille!");
                        return;
                    }
                    continue;
                } catch (NumberFormatException e) {
                    // Continuer
                }
            }
            
            switch (command) {
                case "set":
                    if (parts.length == 4) {
                        try {
                            int row = Integer.parseInt(parts[1]);
                            int col = Integer.parseInt(parts[2]);
                            int value = Integer.parseInt(parts[3]);
                            makeMove(row, col, value);
                        } catch (NumberFormatException e) {
                            System.out.println("Format invalide!");
                        }
                    } else {
                        System.out.println("Format: set ligne colonne valeur");
                    }
                    break;
                    
                case "clear":
                    if (parts.length == 3) {
                        try {
                            int row = Integer.parseInt(parts[1]);
                            int col = Integer.parseInt(parts[2]);
                            currentPosition.setValue(row, col, -1);
                        } catch (NumberFormatException e) {
                            System.out.println("Format invalide!");
                        }
                    } else {
                        System.out.println("Format: clear ligne colonne");
                    }
                    break;
                    
                case "check":
                    checkViolations();
                    break;
                    
                case "help":
                    if (parts.length == 3) {
                        try {
                            int row = Integer.parseInt(parts[1]);
                            int col = Integer.parseInt(parts[2]);
                            provideHelp(row, col);
                        } catch (NumberFormatException e) {
                            System.out.println("Format invalide!");
                        }
                    } else {
                        System.out.println("Format: help ligne colonne");
                    }
                    break;
                    
                case "quit":
                    return;
                    
                default:
                    System.out.println("Commande inconnue! Utilisez: set ligne colonne valeur, ou simplement: ligne colonne valeur");
            }
            
            if (currentPosition.isSolution()) {
                System.out.println("\nFélicitations! Vous avez résolu la grille!");
                return;
            }
        }
    }
    
    /**
     * Place une valeur et vérifie les violations
     */
    private void makeMove(int row, int col, int value) {
        if (row < 0 || row >= currentPosition.getSize() || 
            col < 0 || col >= currentPosition.getSize()) {
            System.out.println("Position invalide!");
            return;
        }
        
        if (value != 0 && value != 1) {
            System.out.println("Valeur doit être 0 ou 1!");
            return;
        }
        
        if (!currentPosition.isEmpty(row, col)) {
            int currentValue = currentPosition.getValue(row, col);
            if (currentValue == value) {
                System.out.println("⚠ Cette cellule contient déjà la valeur " + value + "!");
                return;
            } else {
                System.out.println("⚠ Cette cellule contient " + currentValue + ". Remplacement par " + value + "...");
            }
        }
        
        currentPosition.setValue(row, col, value);
        
        List<String> violations = currentPosition.getViolations(row, col);
        if (!violations.isEmpty()) {
            System.out.println("⚠ Violations détectées:");
            for (String violation : violations) {
                System.out.println("  - " + violation);
            }
        } else {
            System.out.println("✓ Valeur placée avec succès!");
        }
    }
    
    /**
     * Vérifie toutes les règles et affiche l'état de chacune
     */
    private void checkViolations() {
        System.out.println("\n=== VÉRIFICATION DES RÈGLES ===");
        
        boolean rule1 = currentPosition.checkRule1();
        boolean rule2 = currentPosition.checkRule2();
        boolean rule3 = currentPosition.checkRule3();
        
        System.out.println("Règle 1 (Max 2 identiques côte à côte): " + 
            (rule1 ? "✓ OK" : "✗ VIOLÉE"));
        System.out.println("Règle 2 (Équilibre 0/1): " + 
            (rule2 ? "✓ OK" : "✗ VIOLÉE"));
        System.out.println("Règle 3 (Lignes/colonnes uniques): " + 
            (rule3 ? "✓ OK" : "✗ VIOLÉE"));
        
        if (rule1 && rule2 && rule3) {
            System.out.println("\n✓ Toutes les règles sont respectées!");
        } else {
            System.out.println("\n✗ Certaines règles sont violées!");
        }
    }
    
    /**
     * Fournit de l'aide pour une position
     * Teste les deux valeurs possibles et suggère selon l'heuristique LCV
     */
    private void provideHelp(int row, int col) {
        if (row < 0 || row >= currentPosition.getSize() || 
            col < 0 || col >= currentPosition.getSize()) {
            System.out.println("Position invalide!");
            return;
        }
        
        if (!currentPosition.isEmpty(row, col)) {
            System.out.println("Cette position est déjà remplie!");
            return;
        }
        
        System.out.println("\n=== AIDE POUR (" + row + ", " + col + ") ===");
        
        // Tester les deux valeurs possibles
        for (int value = 0; value <= 1; value++) {
            BinairoPosition test = new BinairoPosition(currentPosition);
            test.setValue(row, col, value);
            
            List<String> violations = test.getViolations(row, col);
            if (violations.isEmpty()) {
                System.out.println("Valeur " + value + ": ✓ Valide");
            } else {
                System.out.println("Valeur " + value + ": ✗ Invalide");
                for (String violation : violations) {
                    System.out.println("  - " + violation);
                }
            }
        }
        
        // Suggestion basée sur LCV (Least Constraining Value)
        // Choisit la valeur qui élimine le moins de possibilités pour les autres cellules
        GameSearch search = new GameSearch(currentPosition);
        List<Integer> suggestions = search.selectValueLCV(currentPosition, row, col);
        if (!suggestions.isEmpty()) {
            System.out.println("\n💡 Suggestion: Essayez " + suggestions.get(0) + 
                " (moins contraignant)");
        }
    }
    
    /**
     * Résout automatiquement avec l'algorithme choisi
     * Affiche les statistiques de performance
     */
    private void solveAutomatically() {
        System.out.println("\n=== RÉSOLUTION AUTOMATIQUE ===");
        System.out.println("Choisissez la méthode:");
        System.out.println("1. Backtracking");
        System.out.println("2. Forward Checking (FC)");
        System.out.println("3. AC-3");
        System.out.println("4. AC-4");
        
        int choice = getIntInput("Votre choix: ");
        
        GameSearch search = new GameSearch(currentPosition);
        BinairoPosition solution = null;
        
        long startTime = System.currentTimeMillis();
        switch (choice) {
            case 1:
                solution = search.solveBacktracking();
                break;
            case 2:
                solution = search.solveForwardChecking();
                break;
            case 3:
                solution = search.solveAC3();
                break;
            case 4:
                solution = search.solveAC4();
                break;
            default:
                System.out.println("Choix invalide!");
                return;
        }
        long endTime = System.currentTimeMillis();
        
        if (solution != null && solution.isSolution()) {
            System.out.println("\n✓ Solution trouvée!");
            System.out.println("Méthode: " + search.getMethodUsed());
            System.out.println("Nœuds explorés: " + search.getNodesExplored());
            System.out.println("Temps: " + (endTime - startTime) + " ms");
            System.out.println("\nSolution:");
            System.out.println(solution);
            
            System.out.print("Remplacer la grille actuelle par la solution? (o/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("o") || response.equals("oui")) {
                currentPosition = solution;
            }
        } else {
            System.out.println("\n✗ Aucune solution trouvée!");
            System.out.println("La grille pourrait être insoluble.");
        }
    }
    
    /**
     * Compare les 4 méthodes de résolution
     * Affiche les statistiques de performance
     */
    private void compareMethods() {
        System.out.println("\n=== COMPARAISON DES MÉTHODES ===");
        System.out.println("Cette opération peut prendre du temps...");
        GameSearch.compareMethods(currentPosition);
    }
    
    /**
     * Affiche la grille actuelle
     */
    private void displayGrid() {
        System.out.println("\nGrille actuelle:");
        System.out.println(currentPosition);
        
        if (currentPosition.isSolution()) {
            System.out.println("✓ Grille complète et valide!");
        } else if (currentPosition.isValid()) {
            System.out.println("✓ Grille valide (incomplète)");
        } else {
            System.out.println("✗ Grille invalide!");
        }
    }
    
    /**
     * Valide la grille initiale
     * Vérifie les règles puis teste la résolubilité avec Forward Checking
     */
    private boolean validateInitialGrid() {
        if (!currentPosition.isValid()) {
            System.out.println("La grille ne respecte pas les règles!");
            return false;
        }
        
        System.out.println("Vérification de la résolubilité...");
        GameSearch search = new GameSearch(currentPosition);
        BinairoPosition solution = search.solveForwardChecking();
        
        if (solution != null && solution.isSolution()) {
            System.out.println("✓ La grille est résolvable!");
            return true;
        } else {
            System.out.println("⚠ La grille pourrait ne pas être résolvable!");
            return false;
        }
    }
    
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

