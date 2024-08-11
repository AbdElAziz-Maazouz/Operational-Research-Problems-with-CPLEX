import ilog.concert.*;
import ilog.cplex.*;

public class Jeux {

    private int nbSerie;
    private int nbNombre;
    private int bornSup;
    private IloCplex modele;
    private IloNumVar[][] x;
    private IloNumVar[] y;
    private short[][] A;

    public Jeux(short[][] A, int bornSup) {
        this.A = A;
        nbSerie = A.length;
        nbNombre = A[0].length;
        this.bornSup = bornSup;
        x = new IloNumVar[nbSerie][nbNombre];
        y = new IloNumVar[nbSerie];
        try {
            modele = new IloCplex();
            createModele();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void createModele() throws IloException {
        createVariables();
        createConstraints1();
        createConstraints2();
        createFonctionObj();
    }

    private void createVariables() throws IloException {
        for (int i = 0; i < nbSerie; i++) {
            y[i] = modele.boolVar();
            for (int j = 0; j < nbNombre; j++) {
                x[i][j] = modele.boolVar();
            }
        }
    }

    private void createConstraints1() {
        try {
            for (int i = 0; i < nbSerie; i++) {
                IloLinearNumExpr c = modele.linearNumExpr();
                for (int j = 0; j < nbNombre; j++) {
                    c.addTerm(A[i][j], x[i][j]);
                }
                c.addTerm(-bornSup, y[i]);
                modele.addLe(c, 0);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void createConstraints2() {
        try {
            modele.addEq(modele.sum(y), 1);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void createFonctionObj() {
        try {
            IloLinearNumExpr obj = modele.linearNumExpr();
            for (int i = 0; i < nbSerie; i++) {
                for (int j = 0; j < nbNombre; j++) {
                    obj.addTerm(A[i][j], x[i][j]);
                }
            }
            modele.addMaximize(obj);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public boolean solve() {
        boolean hasSolved = false;
        try {
            hasSolved = modele.solve();
        } catch (IloException e) {
            e.printStackTrace();
        }
        return hasSolved;
    }

    
    public double[][] getSolutionX() {
        double[][] solutionX = new double[nbSerie][nbNombre];
        try {
            for (int i = 0; i < nbSerie; i++) {
                solutionX[i] = modele.getValues(x[i]);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
        return solutionX;
    }

    
    public double[] getSolutionY() {
        double[] solutionY = new double[nbSerie];
        try {
            solutionY = modele.getValues(y);
        } catch (IloException e) {
            e.printStackTrace();
        }
        return solutionY;
    }
    

    public void getSolutionJeux() {
        if (solve()) {
            double[][] solutionX = getSolutionX();
            double[] solutionY = getSolutionY();
            for (int i = 0; i < nbSerie; i++) {
                if (solutionY[i] == 1) {
                    System.out.println("Série " + (i + 1) + ":");
                    System.out.println("Nombres retenus :");
                    for (int j = 0; j < nbNombre; j++) {
                        if (solutionX[i][j] == 1) {
                            System.out.println("N" + (j + 1) + ": " + A[i][j]);
                        }
                    }
                }
            }
        } else {
            System.out.println("Aucune solution trouvée.");
        }
    }
    

    public static void main(String[] args) {
    	short[][] A = {{11,13,16,41,7,17,21,},{9,13,18,37,13,18,46,},{3,23,31,53,11,17,21}};
        int bornSup = 100;
        Jeux jeu = new Jeux(A, bornSup);
        jeu.getSolutionJeux();
    }
}
