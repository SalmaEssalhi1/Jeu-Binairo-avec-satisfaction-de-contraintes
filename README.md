# 🎮 Jeu Binairo avec Satisfaction de Contraintes

Un jeu de logique Binairo (également connu sous le nom de Takuzu ou Binero) implémenté en Java avec plusieurs algorithmes de satisfaction de contraintes pour la résolution automatique.

## 📋 Description

Le Binairo est un puzzle de logique binaire où l'objectif est de remplir une grille avec des 0 et des 1 en respectant trois règles strictes. Ce projet implémente le jeu avec deux interfaces (terminal et graphique) et propose plusieurs algorithmes de résolution automatique basés sur la satisfaction de contraintes.

## 🎯 Règles du Jeu

Le jeu se joue sur une grille carrée de taille paire (6x6, 8x8, ou 10x10) avec trois règles fondamentales :

1. **Règle 1 - Maximum deux identiques côte à côte** : Il ne peut pas y avoir plus de deux chiffres identiques consécutifs horizontalement ou verticalement.

2. **Règle 2 - Équilibre 0/1** : Chaque ligne et chaque colonne doit contenir le même nombre de 0 et de 1 (ou une différence d'au plus 1 pour les grilles impaires).

3. **Règle 3 - Unicité** : Aucune ligne ou colonne ne peut être identique à une autre ligne ou colonne.

## ✨ Fonctionnalités

### Interface Utilisateur
- **Mode Terminal** : Interface en ligne de commande avec menu interactif
- **Interface Graphique** : Interface moderne avec thème sombre utilisant Java Swing
- **Sauvegarde/Chargement** : Possibilité de sauvegarder et charger des grilles

### Génération de Grilles
- Création manuelle de grilles
- Génération aléatoire avec trois niveaux de difficulté :
  - **Débutant** : 40% de cellules vides
  - **Intermédiaire** : 50% de cellules vides
  - **Expert** : 60% de cellules vides

### Résolution Automatique
Le projet implémente quatre algorithmes de satisfaction de contraintes :

1. **Backtracking** : Recherche arrière classique avec heuristiques
2. **Forward Checking (FC)** : Détection précoce des dead ends
3. **AC-3 (Arc Consistency 3)** : Cohérence d'arc avec propagation
4. **AC-4 (Arc Consistency 4)** : Version améliorée avec comptage précis des supports

### Heuristiques Implémentées
- **MVR (Minimum Remaining Values)** : Sélectionne la variable avec le moins de valeurs possibles
- **Degree Heuristic** : En cas d'égalité MVR, choisit la variable la plus contrainte
- **LCV (Least Constraining Value)** : Sélectionne la valeur qui élimine le moins de possibilités pour les autres variables

### Fonctionnalités Avancées
- Vérification en temps réel des violations de règles
- Système d'aide suggérant les meilleures valeurs à placer
- Comparaison de performance entre les différentes méthodes de résolution
- Statistiques détaillées (nœuds explorés, temps d'exécution)

## 🚀 Installation et Compilation

### Prérequis
- Java JDK 8 ou supérieur
- Un compilateur Java (javac)

### Compilation

Compilez tous les fichiers Java :

```bash
javac *.java
```

### Exécution

**Mode Terminal :**
```bash
java Binairo
```

**Interface Graphique :**
```bash
java BinairoGUI
```

## 📖 Utilisation

### Mode Terminal

Le menu principal offre les options suivantes :

1. **Créer une grille manuellement** : Entrez les valeurs initiales une par une
2. **Générer une grille aléatoire** : Crée une grille résolvable avec difficulté choisie
3. **Charger une grille sauvegardée** : Charge depuis `binairo_save.txt`
4. **Résoudre manuellement** : Mode interactif pour jouer
5. **Résoudre automatiquement** : Choisissez l'algorithme de résolution
6. **Comparer les méthodes** : Teste toutes les méthodes et affiche les statistiques
7. **Sauvegarder la grille** : Sauvegarde dans `binairo_save.txt`
8. **Quitter**

### Commandes en Mode Manuel

- `set ligne colonne valeur` : Placer une valeur (0 ou 1)
- `ligne colonne valeur` : Format compact (ex: `0 0 1`)
- `001` : Format ultra-compact (ligne 0, colonne 0, valeur 1)
- `clear ligne colonne` : Effacer une cellule
- `check` : Vérifier toutes les règles
- `help ligne colonne` : Obtenir de l'aide pour une position
- `quit` : Retour au menu

### Interface Graphique

L'interface graphique offre une expérience visuelle moderne avec :
- Clic sur les cellules pour placer des valeurs
- Panneau latéral avec toutes les fonctionnalités
- Journal des actions en temps réel
- Indicateur de statut de la grille
- Détection visuelle des violations (cellules en rouge)

## 📁 Structure du Projet

```
Jeu Binairo/
├── Binairo.java              # Classe principale (mode terminal)
├── BinairoGUI.java           # Interface graphique
├── BinairoPosition.java      # Représentation de la grille et validation
├── BinairoMove.java          # Représentation d'un mouvement
├── GameSearch.java           # Algorithmes de résolution
├── binairo_save.txt          # Fichier de sauvegarde
└── README.md                 # Ce fichier
```

## 🔬 Algorithmes de Résolution

### Backtracking
Algorithme de recherche arrière classique avec heuristiques MVR et LCV pour optimiser l'ordre de sélection des variables et valeurs.

### Forward Checking
Améliore le backtracking en vérifiant après chaque assignation si toutes les variables non assignées ont encore au moins une valeur possible. Détecte les dead ends plus tôt.

### AC-3 (Arc Consistency 3)
Maintient la cohérence d'arc en propageant les contraintes. Utilise une queue pour traiter les arcs qui doivent être révisés.

### AC-4 (Arc Consistency 4)
Version améliorée d'AC-3 avec un comptage plus précis des supports. Plus strict mais peut être plus coûteux en temps.

## 📊 Comparaison des Méthodes

Le système de comparaison teste toutes les méthodes sur la même grille et affiche :
- Statut de résolution (réussi/échec)
- Nombre de nœuds explorés
- Temps d'exécution en millisecondes

**Note** : Des limites de sécurité sont en place (50,000 nœuds max, 30 secondes max) pour éviter les problèmes de mémoire.

## 🎓 Aspects Pédagogiques

Ce projet est idéal pour comprendre :
- Les algorithmes de satisfaction de contraintes (CSP)
- Les heuristiques de sélection de variables (MVR, Degree)
- Les heuristiques de sélection de valeurs (LCV)
- La propagation de contraintes (Forward Checking, AC-3, AC-4)
- L'implémentation de jeux de logique

## 🐛 Limitations

- Les grilles très difficiles peuvent atteindre les limites de sécurité (50,000 nœuds ou 30 secondes)
- La génération de grilles aléatoires peut parfois échouer pour les grilles 10x10
- AC-4 peut être plus lent que les autres méthodes sur certaines grilles

## 📝 Format de Sauvegarde

Le fichier `binairo_save.txt` utilise le format suivant :
```
6
010110
101010
010101
101001
110010
001101
```

- Première ligne : taille de la grille
- Lignes suivantes : valeurs de la grille (0, 1, ou - pour vide)

## 👨‍💻 Auteur

Projet développé dans le cadre de l'étude des algorithmes de satisfaction de contraintes.

## 📄 Licence

Ce projet est fourni à des fins éducatives.

## 🙏 Remerciements

Le jeu Binairo est également connu sous les noms :
- **Takuzu** (Japon)
- **Binero** (Europe)
- **Binary Puzzle** (États-Unis)

---

**Bon jeu ! 🎮**
