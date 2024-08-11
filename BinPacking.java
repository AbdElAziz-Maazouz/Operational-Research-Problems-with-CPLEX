import ilog.concert.*;
import ilog.cplex.*;

public class BinPacking {

    private int nbObj;
    private int nbBoite;
    private int W;
    private IloCplex modele;
    private IloNumVar[][] y;
    private IloNumVar[] z;
    private short[] A;

    public BinPacking(short[] A, int W, int k) {
        this.A = A;
        nbObj = A.length;
        nbBoite = k;
        this.W = W;
        y = new IloNumVar[nbObj][nbBoite];
        z = new IloNumVar[nbBoite];
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
        for (int i = 0; i < nbObj; i++) {
            for (int j = 0; j < nbBoite; j++) {
                y[i][j] = modele.boolVar();
            }
        }
        for (int j = 0; j < nbBoite; j++) {
            z[j] = modele.boolVar();
        }
    }

    private void createConstraints1() {
        try {
            for (int i = 0; i < nbObj; i++) {
                IloLinearNumExpr c = modele.linearNumExpr();
                for (int j = 0; j < nbBoite; j++) {
                    c.addTerm(1, y[i][j]);
                }
                modele.addEq(c, 1);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void createConstraints2() {
        try {
            for (int j = 0; j < nbBoite; j++) {
                IloLinearNumExpr c = modele.linearNumExpr();
                for (int i = 0; i < nbObj; i++) {
                    c.addTerm(A[i], y[i][j]);
                }
                c.addTerm(-W, z[j]);
                modele.addLe(c, 0);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void createFonctionObj() {
        try {
            IloLinearNumExpr obj = modele.linearNumExpr();
            for (int j = 0; j < nbBoite; j++) {
                obj.addTerm(1, z[j]);
            }
            modele.addMinimize(obj);
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

    public double[][] getSolutionY() {
        double[][] solutionY = new double[nbObj][nbBoite];
        try {
            for (int i = 0; i < nbObj; i++) {
                solutionY[i] = modele.getValues(y[i]);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
        return solutionY;
    }

    public double[] getSolutionZ() {
        double[] solutionZ = new double[nbBoite];
        try {
            solutionZ = modele.getValues(z);
        } catch (IloException e) {
            e.printStackTrace();
        }
        return solutionZ;
    }

    public void getSolutionBinPacking() {
        if (solve()) {
            double[][] solutionY = getSolutionY();
            double[] solutionZ = getSolutionZ();
            for (int j = 0; j < nbBoite; j++) {
                if (solutionZ[j] == 1) {
                    System.out.println("Boîte " + (j + 1) + ":");
                    System.out.println("Objets dans la boîte :");
                    for (int i = 0; i < nbObj; i++) {
                        if (solutionY[i][j] == 1) {
                            System.out.println("Objet " + (i + 1) + ": taille = " + A[i]);
                        }
                    }
                }
            }
        } else {
            System.out.println("Aucune solution trouvée.");
        }
    }

    public static void main(String[] args) {
        short[] A = {100, 22, 25, 51, 95, 58, 97, 30, 79, 23, 53, 80, 20, 65, 64,
        			21, 26, 100, 81, 98, 70, 85, 92, 45, 29, 88, 85, 54, 40, 57};
        
        int W = 150;

        BinPacking bp = new BinPacking(A, W, 20);
        bp.getSolutionBinPacking();
    }
}