import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Affectation {
    double[][] c;
    int n, m;
    IloCplex modele;
    IloNumVar[][] x;

    public Affectation(double[][] c) throws IloException {
        this.c = c;
        this.n = c.length;
        this.m = c[0].length;
        this.modele = new IloCplex();
        this.x = new IloNumVar[n][m];
        createModele();
    }

    private void createModele() throws IloException {
        createVariables();
        createConstraints();
        createObjective();
    }

    private void createVariables() throws IloException {
        for (int i = 0; i < n; i++) {
            x[i] = modele.boolVarArray(m);
        }
    }

    private void createConstraints() throws IloException {
        createConstraints1();
        createConstraints2();
    }

    private void createConstraints1() throws IloException {
        for (int j = 0; j < m; j++) {
            IloLinearNumExpr f = modele.linearNumExpr();
            for (int i = 0; i < n; i++) {
                f.addTerm(1, x[i][j]);
            }
            modele.addEq(f, 1);
        }
    }

    private void createConstraints2() throws IloException {
        for (int i = 0; i < n; i++) {
            IloLinearNumExpr f = modele.linearNumExpr();
            for (int j = 0; j < m; j++) {
                f.addTerm(1, x[i][j]);
            }
            modele.addEq(f, 1);
        }
    }

    private void createObjective() throws IloException {
        IloLinearNumExpr f = modele.linearNumExpr();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                f.addTerm(c[i][j], x[i][j]);
            }
        }
        modele.addMinimize(f);
    }

    public boolean solve() throws IloException {
        return modele.solve();
    }

    public double[][] getSolution() throws IloException {
        double[][] solution = new double[n][m];
        for (int i = 0; i < n; i++) {
             solution[i] = modele.getValues(x[i]);
        }
        return solution;
    }

    public void getSolutionAffectation() throws IloException {
        if (solve()) {
            double[][] solution = getSolution();
            System.out.println("La solution d'affectation:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (solution[i][j] == 1) {
                        System.out.println("La tâche " + (i + 1) + " est affectée à la machine " + (j + 1));
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IloException {
        double[][] m = {
            {15, 40, 5, 20, 20},
            {22, 33, 9, 16, 20},
            {40, 6, 28, 0, 26},
            {8, 0, 7, 25, 60},
            {10, 10, 60, 15, 5}
        };
        Affectation a = new Affectation(m);
        a.getSolutionAffectation();
    }
}
