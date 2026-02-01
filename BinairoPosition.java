import java.util.*;

/**
 * Représente l'état d'une grille Binairo
 * Contient la grille (tableau 2D) et toutes les méthodes pour vérifier les 3 règles du jeu
 */
public class BinairoPosition {
    private int[][] grid;  
    private int size;      
    
    // Constantes pour les valeurs possibles dans une cellule
    private static final int EMPTY = -1;  
    private static final int ZERO = 0;    
    private static final int ONE = 1;    
    
    /**
     * Crée une nouvelle grille vide de la taille spécifiée
     */
    public BinairoPosition(int size) {
        this.size = size;
        this.grid = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = EMPTY;
            }
        }
    }
    
    /**
     * Crée une copie complète d'une autre grille
     * Utile pour tester des valeurs sans modifier la grille originale
     */
    public BinairoPosition(BinairoPosition other) {
        this.size = other.size;
        this.grid = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(other.grid[i], 0, this.grid[i], 0, size);
        }
    }
    
    public int getSize() {
        return size;
    }
    
    public int getValue(int row, int col) {
        return grid[row][col];
    }
    
    public void setValue(int row, int col, int value) {
        if (value == EMPTY || value == ZERO || value == ONE) {
            grid[row][col] = value;
        }
    }
    
    public boolean isEmpty(int row, int col) {
        return grid[row][col] == EMPTY;
    }
    
    /**
     * Vérifie la règle 1: Maximum deux chiffres identiques côte à côte
     * Détecte trois chiffres identiques consécutifs horizontalement ou verticalement
     */
    public boolean checkRule1() {
        // Vérifier les lignes
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 2; j++) {
                if (grid[i][j] != EMPTY && grid[i][j+1] != EMPTY && grid[i][j+2] != EMPTY) {
                    if (grid[i][j] == grid[i][j+1] && grid[i][j+1] == grid[i][j+2]) {
                        return false;
                    }
                }
            }
        }
        
        // Vérifier les colonnes
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size - 2; i++) {
                if (grid[i][j] != EMPTY && grid[i+1][j] != EMPTY && grid[i+2][j] != EMPTY) {
                    if (grid[i][j] == grid[i+1][j] && grid[i+1][j] == grid[i+2][j]) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Vérifie la règle 2: Équilibre entre 0 et 1
     * Grilles paires: même nombre de 0 et 1
     * Grilles impaires: différence d'au plus 1
     */
    public boolean checkRule2() {
        int maxCount = size / 2;
        int maxAllowed = (size % 2 == 0) ? maxCount : maxCount + 1;
        
        // Vérifier chaque ligne
        for (int i = 0; i < size; i++) {
            int count0 = 0, count1 = 0;
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == ZERO) count0++;
                else if (grid[i][j] == ONE) count1++;
            }
            if (count0 > maxAllowed || count1 > maxAllowed) {
                return false;
            }
        }
        
        // Vérifier chaque colonne
        for (int j = 0; j < size; j++) {
            int count0 = 0, count1 = 0;
            for (int i = 0; i < size; i++) {
                if (grid[i][j] == ZERO) count0++;
                else if (grid[i][j] == ONE) count1++;
            }
            if (count0 > maxAllowed || count1 > maxAllowed) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Vérifie la règle 3: Aucune ligne ou colonne identique
     * Compare toutes les paires de lignes/colonnes complètes
     */
    public boolean checkRule3() {
        // Vérifier les lignes
        for (int i = 0; i < size; i++) {
            for (int k = i + 1; k < size; k++) {
                if (isRowComplete(i) && isRowComplete(k)) {
                    if (areRowsEqual(i, k)) {
                        return false;
                    }
                }
            }
        }
        
        // Vérifier les colonnes
        for (int j = 0; j < size; j++) {
            for (int k = j + 1; k < size; k++) {
                if (isColComplete(j) && isColComplete(k)) {
                    if (areColsEqual(j, k)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    private boolean isRowComplete(int row) {
        for (int j = 0; j < size; j++) {
            if (grid[row][j] == EMPTY) return false;
        }
        return true;
    }
    
    private boolean isColComplete(int col) {
        for (int i = 0; i < size; i++) {
            if (grid[i][col] == EMPTY) return false;
        }
        return true;
    }
    
    private boolean areRowsEqual(int row1, int row2) {
        for (int j = 0; j < size; j++) {
            if (grid[row1][j] != grid[row2][j]) return false;
        }
        return true;
    }
    
    private boolean areColsEqual(int col1, int col2) {
        for (int i = 0; i < size; i++) {
            if (grid[i][col1] != grid[i][col2]) return false;
        }
        return true;
    }
    
    public boolean isValid() {
        return checkRule1() && checkRule2() && checkRule3();
    }
    
    public boolean isComplete() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == EMPTY) return false;
            }
        }
        return true;
    }
    
    public boolean isSolution() {
        return isComplete() && isValid();
    }
    
    public int getEmptyCount() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == EMPTY) count++;
            }
        }
        return count;
    }
    
    /**
     * Retourne la liste de toutes les positions vides
     * Utilisé par les algorithmes de recherche pour savoir quelles cellules remplir
     */
    public List<int[]> getEmptyPositions() {
        List<int[]> empty = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == EMPTY) {
                    empty.add(new int[]{i, j});
                }
            }
        }
        return empty;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == EMPTY) {
                    sb.append("· ");
                } else {
                    sb.append(grid[i][j]).append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Convertit la grille en format texte pour sauvegarde
     * Format: première ligne = taille, puis une ligne par ligne (0, 1, ou -)
     */
    public String toSaveString() {
        StringBuilder sb = new StringBuilder();
        sb.append(size).append("\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == EMPTY) {
                    sb.append("-");
                } else {
                    sb.append(grid[i][j]);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Reconstruit une grille depuis une chaîne sauvegardée
     */
    public static BinairoPosition fromSaveString(String data) {
        String[] lines = data.trim().split("\n");
        int size = Integer.parseInt(lines[0]);
        BinairoPosition pos = new BinairoPosition(size);
        
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            for (int j = 0; j < line.length() && j < size; j++) {
                char c = line.charAt(j);
                if (c == '0') {
                    pos.setValue(i-1, j, ZERO);
                } else if (c == '1') {
                    pos.setValue(i-1, j, ONE);
                }
            }
        }
        return pos;
    }
    
    /**
     * Retourne la liste des règles violées pour une position spécifique
     */
    public List<String> getViolations(int row, int col) {
        List<String> violations = new ArrayList<>();
        
        if (!checkRule1At(row, col)) {
            violations.add("Règle 1 violée: Trois chiffres identiques côte à côte");
        }
        
        if (!checkRule2At(row, col)) {
            violations.add("Règle 2 violée: Déséquilibre dans la ligne ou colonne");
        }
        
        if (!checkRule3At(row, col)) {
            violations.add("Règle 3 violée: Ligne ou colonne identique à une autre");
        }
        
        return violations;
    }
    
    /**
     * Vérifie la règle 1 pour une position spécifique
     * Regarde les voisins pour détecter trois identiques consécutifs
     */
    private boolean checkRule1At(int row, int col) {
        // Vérifier horizontalement (gauche et droite)
        if (col >= 2) {
            if (grid[row][col-2] != EMPTY && grid[row][col-1] != EMPTY && grid[row][col] != EMPTY) {
                if (grid[row][col-2] == grid[row][col-1] && grid[row][col-1] == grid[row][col]) {
                    return false;
                }
            }
        }
        if (col < size - 2) {
            if (grid[row][col] != EMPTY && grid[row][col+1] != EMPTY && grid[row][col+2] != EMPTY) {
                if (grid[row][col] == grid[row][col+1] && grid[row][col+1] == grid[row][col+2]) {
                    return false;
                }
            }
        }
        
        // Vérifier verticalement (haut et bas)
        if (row >= 2) {
            if (grid[row-2][col] != EMPTY && grid[row-1][col] != EMPTY && grid[row][col] != EMPTY) {
                if (grid[row-2][col] == grid[row-1][col] && grid[row-1][col] == grid[row][col]) {
                    return false;
                }
            }
        }
        if (row < size - 2) {
            if (grid[row][col] != EMPTY && grid[row+1][col] != EMPTY && grid[row+2][col] != EMPTY) {
                if (grid[row][col] == grid[row+1][col] && grid[row+1][col] == grid[row+2][col]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Vérifie la règle 2 pour une position spécifique
     * Compte les 0 et 1 dans la ligne et colonne
     */
    private boolean checkRule2At(int row, int col) {
        int maxAllowed = (size % 2 == 0) ? size / 2 : size / 2 + 1;
        
        // Compter dans la ligne
        int count0 = 0, count1 = 0;
        for (int j = 0; j < size; j++) {
            if (grid[row][j] == ZERO) count0++;
            else if (grid[row][j] == ONE) count1++;
        }
        if (count0 > maxAllowed || count1 > maxAllowed) {
            return false;
        }
        
        // Compter dans la colonne
        count0 = 0; count1 = 0;
        for (int i = 0; i < size; i++) {
            if (grid[i][col] == ZERO) count0++;
            else if (grid[i][col] == ONE) count1++;
        }
        if (count0 > maxAllowed || count1 > maxAllowed) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Vérifie la règle 3 pour une position spécifique
     * Vérifie si la ligne/colonne complète est identique à une autre
     */
    private boolean checkRule3At(int row, int col) {
        if (isRowComplete(row)) {
            for (int i = 0; i < size; i++) {
                if (i != row && isRowComplete(i) && areRowsEqual(row, i)) {
                    return false;
                }
            }
        }
        
        if (isColComplete(col)) {
            for (int j = 0; j < size; j++) {
                if (j != col && isColComplete(j) && areColsEqual(col, j)) {
                    return false;
                }
            }
        }
        
        return true;
    }
}

