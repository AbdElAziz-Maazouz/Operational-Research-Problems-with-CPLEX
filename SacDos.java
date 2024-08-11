import ilog.concert.*;
import ilog.cplex.*;

    public class SacDos {
        private double[] c;
        private double[] b;
        private int n;
        private double capacite;
        private IloCplex modele;
        private IloNumVar[] x;

        public SacDos(double[] c, double[] b, double capacite) throws IloException {
            this.c = c;
            this.b = b;
            this.n = c.length;
            this.capacite = capacite;
            modele = new IloCplex();
            createModele();
            System.out.println(modele.toString());
        }

        public void createModele() throws IloException {
            createVariables();
            createConstraints();
            createFonctionObj();
        }

        private void createVariables() throws IloException {
            x = modele.boolVarArray(n);
        }

        private void createConstraints() throws IloException {
            createConstraints1();
            //createConstraints2();
        }

        private void createConstraints1() throws IloException {
            IloLinearNumExpr lin = modele.scalProd(b, x);
            modele.addLe(lin, capacite);
        }
        

//        private void createConstraints2() throws IloException {
//	      	IloLinearNumExpr lin = modele.linearNumExpr();
//				for (int i = 0; i < n; i++) {
//					lin.addTerm(x[i],b[i]);
//				}
//				modele.addLe(lin,capacite);
//        }

        private void createFonctionObj() throws IloException {
            IloLinearNumExpr fon = modele.scalProd(c, x);
            modele.addMaximize(fon);
        }

        public boolean solve() throws IloException {
            return modele.solve();
        }

        public double[] getSolution() throws IloException {
            return modele.getValues(x);
        }

        public void getSolutionSac() throws IloException {
            if (solve()) {
                double[] d = getSolution();
                System.out.println("Les objets dans le sac:");
                for (int i = 0; i < d.length; i++) {
                    if (d[i] == 1)
                        System.out.println("Obj[" + (i + 1) + "]");
                }
            }
        }

        public static void main(String[] args) {
            double[] b = {12, 2, 1, 4, 1};
            double[] c = {4, 2, 1, 10, 2};
            try {
                SacDos ex1 = new SacDos(c, b, 15);
                ex1.getSolutionSac();
            } catch (IloException e) {
                System.err.println("Exception : " + e);
            }
        }
    }
