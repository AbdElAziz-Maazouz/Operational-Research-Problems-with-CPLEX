import ilog.concert.*;
import ilog.cplex.*;

public class ProblemMaxStable {
    private IloCplex modele;
    private IloNumVar[] x;
    private short[][] C;
    private int n;

    public ProblemMaxStable(short[][] C) throws IloException {
        this.C = C;
        this.n = C.length;
        modele = new IloCplex();
        createModele();
        System.out.println(modele.toString());
    }

    public void createModele() throws IloException {
        createVariables();
        createConstraints();
        createFonctionObj();
    }

    public void createVariables() throws IloException {
        x = modele.boolVarArray(n);
    }

    public void createConstraints() throws IloException {
        for (int l = 0; l < C.length; l++) {
            for (int k = l + 1; k < C[0].length; k++) {
                if (C[l][k] != 0) {
                    IloLinearNumExpr expr = modele.linearNumExpr();
                    expr.addTerm(1.0, x[l]);
                    expr.addTerm(1.0, x[k]);
                    modele.addLe(expr, 1.0);
                }
            }
        }
    }

    public void createFonctionObj() throws IloException {
        IloLinearNumExpr fonction = modele.linearNumExpr();
        for (int i = 0; i < n; i++) {
            fonction.addTerm(1, x[i]);
        }
        modele.addMaximize(fonction);
    }

    public boolean solve() throws IloException {
        return modele.solve();
    }

    public double[] getSolution() throws IloException {
        return modele.getValues(x);
    }

    public void getSolutionStableMax() throws IloException {
        if (solve()) {
            double[] d = getSolution();
            System.out.println("La solution du problème du stable maximum est :");
            System.out.print("S = { ");
            for (int i = 0; i < n; i++) {
                if (d[i] == 1) {
                    System.out.print("v" + (i + 1) + " ");
                }
            }
            System.out.println("}");
        }
    }

    public static void main(String[] args) {
        short[][] C = {
            {0, 1, 1, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 1, 0, 0},
            {1, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 1, 0, 0, 1},
            {0, 0, 0, 1, 0, 1, 0, 0},
            {0, 1, 0, 0, 1, 0, 1, 0},
            {0, 0, 1, 0, 0, 1, 0, 1},
            {0, 0, 0, 1, 0, 0, 1, 0}
        };

        try {
            ProblemMaxStable ex2 = new ProblemMaxStable(C);
            ex2.getSolutionStableMax();
        } catch (IloException e) {
            System.err.println("Exception : " + e);
        }
    }
}
