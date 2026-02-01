/**
 * Représente un mouvement dans le jeu Binairo
 * Un mouvement consiste à placer un 0 ou un 1 à une position donnée (ligne, colonne)
 */
public class BinairoMove {
    private int row;    
    private int col;   
    private int value;  
    
    /**
     * Crée un nouveau mouvement
     */
    public BinairoMove(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.format("(%d, %d) = %d", row, col, value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BinairoMove move = (BinairoMove) obj;
        return row == move.row && col == move.col && value == move.value;
    }
    
    @Override
    public int hashCode() {
        return row * 1000 + col * 10 + value;
    }
}

