import java.util.*;

/**
 * Implémente les algorithmes de recherche avec satisfaction de contraintes
 * pour résoudre le jeu Binairo
 */
public class GameSearch {
    private BinairoPosition initialPosition;
    private int nodesExplored;
    private long startTime;
    private long endTime;
    private String methodUsed;
    
    // Limites pour éviter OutOfMemoryError
    private static final int MAX_NODES = 50000; 
    private static final long MAX_TIME_MS = 30000; 
    
    // Constantes pour les méthodes de résolution
    public static final String BACKTRACKING = "Backtracking";
    public static final String FC = "Forward Checking";
    public static final String AC3 = "AC-3";
    public static final String AC4 = "AC-4";
    
    public GameSearch(BinairoPosition position) {
        this.initialPosition = new BinairoPosition(position);
        this.nodesExplored = 0;
    }
    
    /**
     * Résout la grille avec backtracking simple
     */
    public BinairoPosition solveBacktracking() {
        methodUsed = BACKTRACKING;
        startTime = System.currentTimeMillis();
        nodesExplored = 0;
        BinairoPosition result = backtracking(new BinairoPosition(initialPosition));
        endTime = System.currentTimeMillis();
        return result;
    }
    
    /**
     * Résout avec Forward Checking
     */
    public BinairoPosition solveForwardChecking() {
        methodUsed = FC;
        startTime = System.currentTimeMillis();
        nodesExplored = 0;
        BinairoPosition result = forwardChecking(new BinairoPosition(initialPosition));
        endTime = System.currentTimeMillis();
        return result;
    }
    
    /**
     * Résout avec AC-3
     */
    public BinairoPosition solveAC3() {
        methodUsed = AC3;
        startTime = System.currentTimeMillis();
        nodesExplored = 0;
        // Appliquer AC-3 complet au début pour réduire les domaines
        BinairoPosition startPos = new BinairoPosition(initialPosition);
        if (!ac3(startPos)) {
            return null; // Pas de solution si AC-3 échoue au début
        }
        BinairoPosition result = ac3Search(startPos);
        endTime = System.currentTimeMillis();
        return result;
    }
    
    /**
     * Résout avec AC-4
     */
    public BinairoPosition solveAC4() {
        methodUsed = AC4;
        startTime = System.currentTimeMillis();
        nodesExplored = 0;
        // Appliquer AC-3 complet au début (AC-4 utilise AC-3 comme base)
        BinairoPosition startPos = new BinairoPosition(initialPosition);
        if (!ac3(startPos)) {
            return null;
        }
        BinairoPosition result = ac4Search(startPos);
        endTime = System.currentTimeMillis();
        return result;
    }
    
    /**
     * Backtracking simple avec heuristiques MVR et LCV
     */
    private BinairoPosition backtracking(BinairoPosition position) {
        nodesExplored++;
        
        if (nodesExplored > MAX_NODES) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime > MAX_TIME_MS) {
            return null;
        }
        
        if (position.isSolution()) {
            return position;
        }
        
        if (!position.isValid()) {
            return null;
        }
        
        List<int[]> emptyPositions = position.getEmptyPositions();
        if (emptyPositions.isEmpty()) {
            return null;
        }
        
        int[] nextVar = selectVariableMVR(position, emptyPositions);
        int row = nextVar[0];
        int col = nextVar[1];
        
        List<Integer> values = selectValueLCV(position, row, col);
        
        for (int value : values) {
            BinairoPosition newPosition = new BinairoPosition(position);
            newPosition.setValue(row, col, value);
            
            BinairoPosition result = backtracking(newPosition);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Forward Checking avec détection précoce des dead ends
     */
    private BinairoPosition forwardChecking(BinairoPosition position) {
        nodesExplored++;
        
        if (nodesExplored > MAX_NODES) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime > MAX_TIME_MS) {
            return null;
        }
        
        if (position.isSolution()) {
            return position;
        }
        
        if (!position.isValid()) {
            return null;
        }
        
        List<int[]> emptyPositions = position.getEmptyPositions();
        if (emptyPositions.isEmpty()) {
            return null;
        }
        
        int[] nextVar = selectVariableMVR(position, emptyPositions);
        int row = nextVar[0];
        int col = nextVar[1];
        
        List<Integer> values = selectValueLCV(position, row, col);
        
        for (int value : values) {
            BinairoPosition newPosition = new BinairoPosition(position);
            newPosition.setValue(row, col, value);
            
            // Forward checking: détecte si une variable non assignée n'a plus de valeurs possibles
            if (forwardCheck(newPosition)) {
                BinairoPosition result = forwardChecking(newPosition);
                if (result != null) {
                    return result;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Forward checking: vérifie si chaque variable non assignée a au moins une valeur possible
     */
    private boolean forwardCheck(BinairoPosition position) {
        List<int[]> emptyPositions = position.getEmptyPositions();
        
        for (int[] pos : emptyPositions) {
            int row = pos[0];
            int col = pos[1];
            boolean hasValidValue = false;
            
            for (int value = 0; value <= 1; value++) {
                BinairoPosition test = new BinairoPosition(position);
                test.setValue(row, col, value);
                if (test.isValid()) {
                    hasValidValue = true;
                    break;
                }
            }
            
            if (!hasValidValue) {
                return false; 
            }
        }
        
        return true;
    }
    
    /**
     * Recherche avec AC-3
     * Utilise ac3Local pour une vérification rapide de cohérence locale
     */
    private BinairoPosition ac3Search(BinairoPosition position) {
        nodesExplored++;
        
        if (nodesExplored > MAX_NODES) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime > MAX_TIME_MS) {
            return null;
        }
        
        if (position.isSolution()) {
            return position;
        }
        
        if (!position.isValid()) {
            return null;
        }
        
        // Vérification locale améliorée (vérifie toutes les contraintes, pas seulement les voisins directs)
        if (!ac3LocalImproved(position)) {
            return null;
        }
        
        List<int[]> emptyPositions = position.getEmptyPositions();
        if (emptyPositions.isEmpty()) {
            return null;
        }
        
        int[] nextVar = selectVariableMVR(position, emptyPositions);
        int row = nextVar[0];
        int col = nextVar[1];
        
        List<Integer> values = selectValueLCV(position, row, col);
        
        for (int value : values) {
            BinairoPosition newPosition = new BinairoPosition(position);
            newPosition.setValue(row, col, value);
            
            BinairoPosition result = ac3Search(newPosition);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Algorithme AC-3 complet (utilisé seulement au début)
     * Utilise seulement les contraintes locales (voisins directs) pour réduire la complexité
     */
    private boolean ac3(BinairoPosition position) {
        Queue<int[]> queue = new LinkedList<>();
        Set<String> processed = new HashSet<>();
        
        for (int i = 0; i < position.getSize(); i++) {
            for (int j = 0; j < position.getSize(); j++) {
                if (position.isEmpty(i, j)) {
                    addLocalConstraints(queue, i, j, position.getSize(), processed);
                }
            }
        }
        
        int iterations = 0;
        int maxIterations = position.getSize() * position.getSize() * 10;
        
        while (!queue.isEmpty() && iterations < maxIterations) {
            iterations++;
            int[] constraint = queue.poll();
            int row1 = constraint[0], col1 = constraint[1];
            int row2 = constraint[2], col2 = constraint[3];
            
            if (revise(position, row1, col1, row2, col2)) {
                if (position.isEmpty(row1, col1)) {
                    if (!hasValidValues(position, row1, col1)) {
                        return false;
                    }
                    addLocalConstraints(queue, row1, col1, position.getSize(), processed);
                }
            }
        }
        
        return true;
    }
    
    /**
     * AC-3 local simplifié: vérification rapide avec propagation locale
     * Évite de réexécuter AC-3 complet à chaque nœud
     */
    private boolean ac3Local(BinairoPosition position) {
        List<int[]> emptyPositions = position.getEmptyPositions();
        
        for (int[] pos : emptyPositions) {
            int row = pos[0];
            int col = pos[1];
            
            if (!hasValidValues(position, row, col)) {
                return false;
            }
            
            // Propagation locale: vérifier la cohérence avec les voisins directs
            int size = position.getSize();
            int[][] neighbors = {
                {row, col - 1}, {row, col + 1},
                {row - 1, col}, {row + 1, col}
            };
            
            for (int[] neighbor : neighbors) {
                int r = neighbor[0];
                int c = neighbor[1];
                if (r >= 0 && r < size && c >= 0 && c < size) {
                    if (!checkLocalConsistency(position, row, col, r, c)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * AC-3 local amélioré: vérifie toutes les contraintes (ligne, colonne, voisins)
     * Plus complet que ac3Local mais plus rapide que AC-3 complet
     */
    private boolean ac3LocalImproved(BinairoPosition position) {
        List<int[]> emptyPositions = position.getEmptyPositions();
        int size = position.getSize();
        
        for (int[] pos : emptyPositions) {
            int row = pos[0];
            int col = pos[1];
            
            // Vérifier si au moins une valeur est possible
            if (!hasValidValues(position, row, col)) {
                return false;
            }
            
            // Vérifier les contraintes avec toute la ligne
            for (int j = 0; j < size; j++) {
                if (j != col) {
                    if (!checkLocalConsistency(position, row, col, row, j)) {
                        return false;
                    }
                }
            }
            
            // Vérifier les contraintes avec toute la colonne
            for (int i = 0; i < size; i++) {
                if (i != row) {
                    if (!checkLocalConsistency(position, row, col, i, col)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Vérifie la cohérence locale entre deux cellules voisines
     */
    private boolean checkLocalConsistency(BinairoPosition position, int row1, int col1, int row2, int col2) {
        if (!position.isEmpty(row1, col1) && !position.isEmpty(row2, col2)) {
            return position.isValid();
        }
        
        if (!position.isEmpty(row2, col2)) {
            boolean hasConsistentValue = false;
            
            for (int v1 = 0; v1 <= 1; v1++) {
                BinairoPosition test = new BinairoPosition(position);
                test.setValue(row1, col1, v1);
                if (test.isValid()) {
                    hasConsistentValue = true;
                    break;
                }
            }
            
            return hasConsistentValue;
        }
        
        return true;
    }
    
    /**
     * Ajoute seulement les contraintes locales (voisins directs)
     */
    private void addLocalConstraints(Queue<int[]> queue, int row, int col, int size, Set<String> processed) {
        int[][] neighbors = {
            {row, col - 1}, {row, col + 1},
            {row - 1, col}, {row + 1, col}
        };
        
        for (int[] neighbor : neighbors) {
            int r = neighbor[0];
            int c = neighbor[1];
            if (r >= 0 && r < size && c >= 0 && c < size) {
                String key = row + "," + col + "-" + r + "," + c;
                if (!processed.contains(key)) {
                    queue.add(new int[]{row, col, r, c});
                    processed.add(key);
                }
            }
        }
    }
    
    /**
     * Révision AC-3: vérifie si les valeurs de (row1, col1) ont un support dans (row2, col2)
     */
    private boolean revise(BinairoPosition position, int row1, int col1, int row2, int col2) {
        boolean revised = false;
        
        for (int v1 = 0; v1 <= 1; v1++) {
            BinairoPosition test = new BinairoPosition(position);
            test.setValue(row1, col1, v1);
            
            boolean hasSupport = false;
            for (int v2 = 0; v2 <= 1; v2++) {
                BinairoPosition test2 = new BinairoPosition(test);
                test2.setValue(row2, col2, v2);
                if (test2.isValid()) {
                    hasSupport = true;
                    break;
                }
            }
            
            if (!hasSupport) {
                revised = true;
            }
        }
        
        return revised;
    }
    
    /**
     * Recherche avec AC-4
     * AC-4 est une amélioration de AC-3 avec comptage plus précis des supports
     */
    private BinairoPosition ac4Search(BinairoPosition position) {
        nodesExplored++;
        
        if (nodesExplored > MAX_NODES) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime > MAX_TIME_MS) {
            return null;
        }
        
        if (position.isSolution()) {
            return position;
        }
        
        if (!position.isValid()) {
            return null;
        }
        
        // AC-4 utilise une vérification améliorée avec comptage des supports
        if (!ac4Check(position)) {
            return null;
        }
        
        List<int[]> emptyPositions = position.getEmptyPositions();
        if (emptyPositions.isEmpty()) {
            return null;
        }
        
        int[] nextVar = selectVariableMVR(position, emptyPositions);
        int row = nextVar[0];
        int col = nextVar[1];
        
        List<Integer> values = selectValueLCV(position, row, col);
        
        for (int value : values) {
            BinairoPosition newPosition = new BinairoPosition(position);
            newPosition.setValue(row, col, value);
            
            BinairoPosition result = ac4Search(newPosition);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * AC-4: vérification améliorée avec comptage précis des supports
     * Plus strict que AC-3 car vérifie que chaque valeur a des supports valides
     */
    private boolean ac4Check(BinairoPosition position) {
        List<int[]> emptyPositions = position.getEmptyPositions();
        int size = position.getSize();
        
        for (int[] pos : emptyPositions) {
            int row = pos[0];
            int col = pos[1];
            
            // Vérifier si au moins une valeur a des supports valides
            boolean hasValidValueWithSupport = false;
            
            for (int value = 0; value <= 1; value++) {
                BinairoPosition test = new BinairoPosition(position);
                test.setValue(row, col, value);
                
                if (!test.isValid()) {
                    continue;
                }
                
                // Vérifier si cette valeur a au moins un support dans la ligne
                boolean hasSupportInRow = false;
                for (int j = 0; j < size; j++) {
                    if (j != col && position.isEmpty(row, j)) {
                        for (int v2 = 0; v2 <= 1; v2++) {
                            BinairoPosition test2 = new BinairoPosition(test);
                            test2.setValue(row, j, v2);
                            if (test2.isValid()) {
                                hasSupportInRow = true;
                                break;
                            }
                        }
                        if (hasSupportInRow) break;
                    }
                }
                
                // Vérifier si cette valeur a au moins un support dans la colonne
                boolean hasSupportInCol = false;
                for (int i = 0; i < size; i++) {
                    if (i != row && position.isEmpty(i, col)) {
                        for (int v2 = 0; v2 <= 1; v2++) {
                            BinairoPosition test2 = new BinairoPosition(test);
                            test2.setValue(i, col, v2);
                            if (test2.isValid()) {
                                hasSupportInCol = true;
                                break;
                            }
                        }
                        if (hasSupportInCol) break;
                    }
                }
                
                // Si cette valeur a des supports dans la ligne ET la colonne, elle est valide
                if (hasSupportInRow && hasSupportInCol) {
                    hasValidValueWithSupport = true;
                    break;
                }
            }
            
            // Si aucune valeur n'a de support valide, la position est inconsistante
            if (!hasValidValueWithSupport) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Heuristique MVR (Minimum Remaining Values)
     * Sélectionne la variable avec le moins de valeurs possibles
     * En cas d'égalité, utilise Degree heuristic
     */
    private int[] selectVariableMVR(BinairoPosition position, List<int[]> emptyPositions) {
        int minRemaining = Integer.MAX_VALUE;
        int[] bestVar = emptyPositions.get(0);
        
        for (int[] pos : emptyPositions) {
            int row = pos[0];
            int col = pos[1];
            int remaining = countRemainingValues(position, row, col);
            
            if (remaining < minRemaining) {
                minRemaining = remaining;
                bestVar = pos;
            } else if (remaining == minRemaining) {
                int degree1 = getDegree(position, bestVar[0], bestVar[1]);
                int degree2 = getDegree(position, row, col);
                if (degree2 > degree1) {
                    bestVar = pos;
                }
            }
        }
        
        return bestVar;
    }
    
    private int countRemainingValues(BinairoPosition position, int row, int col) {
        int count = 0;
        for (int value = 0; value <= 1; value++) {
            BinairoPosition test = new BinairoPosition(position);
            test.setValue(row, col, value);
            if (test.isValid()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Heuristique Degree: compte les variables non assignées dans la même ligne/colonne
     */
    private int getDegree(BinairoPosition position, int row, int col) {
        int degree = 0;
        int size = position.getSize();
        
        for (int j = 0; j < size; j++) {
            if (j != col && position.isEmpty(row, j)) {
                degree++;
            }
        }
        
        for (int i = 0; i < size; i++) {
            if (i != row && position.isEmpty(i, col)) {
                degree++;
            }
        }
        
        return degree;
    }
    
    /**
     * Heuristique LCV (Least Constraining Value)
     * Sélectionne la valeur qui élimine le moins de valeurs possibles pour les autres variables
     */
    public List<Integer> selectValueLCV(BinairoPosition position, int row, int col) {
        List<ValueScore> scores = new ArrayList<>();
        
        for (int value = 0; value <= 1; value++) {
            BinairoPosition test = new BinairoPosition(position);
            test.setValue(row, col, value);
            
            if (test.isValid()) {
                int score = countRemainingValuesForOthers(test, row, col);
                scores.add(new ValueScore(value, score));
            }
        }
        
        // Trier par score décroissant (plus de valeurs restantes = moins contraignant)
        scores.sort((a, b) -> Integer.compare(b.score, a.score));
        
        List<Integer> result = new ArrayList<>();
        for (ValueScore vs : scores) {
            result.add(vs.value);
        }
        
        return result;
    }
    
    private int countRemainingValuesForOthers(BinairoPosition position, int excludeRow, int excludeCol) {
        int total = 0;
        List<int[]> emptyPositions = position.getEmptyPositions();
        
        for (int[] pos : emptyPositions) {
            if (pos[0] != excludeRow || pos[1] != excludeCol) {
                total += countRemainingValues(position, pos[0], pos[1]);
            }
        }
        
        return total;
    }
    
    private static class ValueScore {
        int value;
        int score;
        
        ValueScore(int value, int score) {
            this.value = value;
            this.score = score;
        }
    }
    
    private void addConstraints(Queue<int[]> queue, int row, int col, int size) {
        if (col > 0) {
            queue.add(new int[]{row, col, row, col - 1});
        }
        if (col < size - 1) {
            queue.add(new int[]{row, col, row, col + 1});
        }
        
        if (row > 0) {
            queue.add(new int[]{row, col, row - 1, col});
        }
        if (row < size - 1) {
            queue.add(new int[]{row, col, row + 1, col});
        }
        
        for (int j = 0; j < size; j++) {
            if (j != col) {
                queue.add(new int[]{row, col, row, j});
            }
        }
        
        for (int i = 0; i < size; i++) {
            if (i != row) {
                queue.add(new int[]{row, col, i, col});
            }
        }
    }
    
    private void addConstraintsFor(Queue<int[]> queue, int row, int col, int size) {
        addConstraints(queue, row, col, size);
    }
    
    private boolean hasValidValues(BinairoPosition position, int row, int col) {
        for (int value = 0; value <= 1; value++) {
            BinairoPosition test = new BinairoPosition(position);
            test.setValue(row, col, value);
            if (test.isValid()) {
                return true;
            }
        }
        return false;
    }
    
    public int getNodesExplored() {
        return nodesExplored;
    }
    
    public long getExecutionTime() {
        return endTime - startTime;
    }
    
    public String getMethodUsed() {
        return methodUsed;
    }
    
    /**
     * Compare les différentes méthodes de résolution
     */
    public static void compareMethods(BinairoPosition position) {
        System.out.println("\n=== Comparaison des méthodes de résolution ===\n");
        System.out.println("⚠ Note: Limite de 50,000 nœuds et 30 secondes par méthode pour éviter OutOfMemoryError\n");
        
        String[] methods = {BACKTRACKING, FC, AC3, AC4};
        List<SearchResult> results = new ArrayList<>();
        
        for (String method : methods) {
            System.out.println("Test de " + method + "...");
            try {
                GameSearch search = new GameSearch(position);
                BinairoPosition solution = null;
                
                long start = System.currentTimeMillis();
                switch (method) {
                    case BACKTRACKING:
                        solution = search.solveBacktracking();
                        break;
                    case FC:
                        solution = search.solveForwardChecking();
                        break;
                    case AC3:
                        solution = search.solveAC3();
                        break;
                    case AC4:
                        solution = search.solveAC4();
                        break;
                }
                long end = System.currentTimeMillis();
                
                boolean solved = (solution != null && solution.isSolution());
                String status = solved ? "✓ Résolu" : "✗ Limite atteinte ou insoluble";
                System.out.println("  " + status + " (" + search.getNodesExplored() + " nœuds, " + (end - start) + " ms)");
                
                results.add(new SearchResult(method, solved, 
                    search.getNodesExplored(), end - start));
                    
                // Nettoyer la mémoire
                solution = null;
                search = null;
                System.gc(); // Suggérer garbage collection
                
            } catch (OutOfMemoryError e) {
                System.out.println("  ✗ OutOfMemoryError - Méthode trop lourde pour cette grille");
                results.add(new SearchResult(method, false, MAX_NODES, MAX_TIME_MS));
                System.gc(); // Forcer garbage collection
            } catch (Exception e) {
                System.out.println("  ✗ Erreur: " + e.getMessage());
                results.add(new SearchResult(method, false, 0, 0));
            }
        }
        
        // Afficher les résultats
        System.out.println("\n--- Résumé ---");
        System.out.printf("%-20s %-10s %-15s %-15s%n", 
            "Méthode", "Résolu", "Nœuds explorés", "Temps (ms)");
        System.out.println("------------------------------------------------------------");
        
        for (SearchResult result : results) {
            String solvedStr = result.solved ? "Oui" : "Non";
            if (result.nodes >= MAX_NODES) {
                solvedStr += " (limite)";
            }
            System.out.printf("%-20s %-10s %-15d %-15d%n",
                result.method, solvedStr, result.nodes, result.time);
        }
    }
    
    private static class SearchResult {
        String method;
        boolean solved;
        int nodes;
        long time;
        
        SearchResult(String method, boolean solved, int nodes, long time) {
            this.method = method;
            this.solved = solved;
            this.nodes = nodes;
            this.time = time;
        }
    }
}

