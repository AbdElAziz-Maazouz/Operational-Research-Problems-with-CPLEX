import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class AffectationFrequence {

    private int[][] matrice;
    private int nb_sommets;
    private int nb_couleurs;
    IloCplex modele;
    IloNumVar[][] x;
    IloIntVar[] y;

    public AffectationFrequence(int[][] matrice, int nb_couleurs) throws IloException {
        this.matrice = matrice;
        this.nb_couleurs = nb_couleurs;
        this.nb_sommets = matrice.length;
        modele = new IloCplex();
        x = new IloNumVar[nb_sommets][nb_couleurs];
        createModele();
        System.err.println(modele.toString());
    }

    private void createModele() throws IloException {
        creationVariables();
        creationContraintes();
        creationFonctionObjective();
    }

    private void creationVariables() throws IloException {
        for (int i = 0; i < nb_sommets; i++) {
            x[i] = modele.boolVarArray(nb_couleurs);
        }
        y = modele.boolVarArray(nb_couleurs);
    }


    private void creationContraintes() throws IloException {
        creationContrainte1();
        creationContrainte2();
        creationContrainte3();
        creationContrainte4();
    }

    private void creationContrainte1() throws IloException {
        for (int i = 0; i < nb_sommets; i++) {
            IloLinearNumExpr ex = modele.linearNumExpr();
            for (int j = 0; j < nb_couleurs; j++) {
                ex.addTerm(1, x[i][j]);
            }
            modele.addEq(ex, 1);
        }
    }

    private void creationContrainte2() throws IloException {
        for (int j = 0; j < nb_couleurs; j++) {
            IloLinearNumExpr ex = modele.linearNumExpr();
            for (int i = 0; i < nb_sommets; i++) {
                ex.addTerm(1, x[i][j]);
            }
            ex.addTerm(-nb_sommets, y[j]);
            modele.addLe(ex, 0);
        }
    }

    private void creationContrainte3() throws IloException {
        for (int i = 0; i < nb_sommets; i++) {
            for (int j = i + 1; j < nb_sommets; j++) {
                if (matrice[i][j] == 1) {
                    for (int k = 0; k < nb_couleurs; k++) {
                        IloLinearNumExpr ex = modele.linearNumExpr();
                        ex.addTerm(1, x[i][k]);
                        ex.addTerm(1, x[j][k]);
                        modele.addLe(ex, 1);
                    }
                }
            }
        }
    }

    private void creationContrainte4() throws IloException {
        for (int j = 0; j < nb_couleurs - 1; j++) {
            IloLinearNumExpr ex = modele.linearNumExpr();
            ex.addTerm(1, y[j + 1]);
            ex.addTerm(-1, y[j]);
            modele.addLe(ex, 0);
        }
    }

    
    private void creationFonctionObjective() throws IloException {
        IloLinearNumExpr f = modele.linearNumExpr();
        for (int j = 0; j < nb_couleurs; j++) {
            f.addTerm(1, y[j]);
        }
        modele.addMinimize(f);
    }
    
    
    public boolean solve() throws IloException {
        return modele.solve();
    }

    public double[][] getSolutionX() throws IloException {
        double[][] solutionX = new double[nb_sommets][nb_couleurs];
        for (int i = 0; i < nb_sommets; i++) {
            solutionX[i] = modele.getValues(x[i]);
        }
        return solutionX;
    }

    public double[] getSolutionY() throws IloException {
        return modele.getValues(y);
    }

    public void getSolutionAffectationFrequence() throws IloException {
        if (solve()) {
            double[][] solutionX = getSolutionX();
            double[] solutionY = getSolutionY();
            System.out.println("La solution d'affectation des fréquences:");
            for (int i = 0; i < nb_sommets; i++) {
                for (int j = 0; j < nb_couleurs; j++) {
                    if (solutionX[i][j] == 1) {
                        System.out.println("V" + (i + 1) + " --> Couleur " + (j + 1));
                    }
                }
            }
            int minColors = 0;
            for (int j = 0; j < nb_couleurs; j++) {
                if (solutionY[j] == 1) {
                    minColors++;
                }
            }
            System.out.println("Nombre minimal de couleurs = " + minColors);
        }
    }


    public static void main(String[] args) throws IloException {
        int[][] A = {
            {0, 1, 1, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 1, 0, 0},
            {1, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 1, 0, 0, 1},
            {0, 0, 0, 1, 0, 1, 0, 0},
            {0, 1, 0, 0, 1, 0, 1, 0},
            {0, 0, 1, 0, 0, 1, 0, 1},
            {0, 0, 0, 1, 0, 0, 1, 0}
        };
        AffectationFrequence af = new AffectationFrequence(A, 7);
        af.getSolutionAffectationFrequence();
    }
}
