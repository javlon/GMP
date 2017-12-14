import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.cplex.IloCplex;

public class SimpleAlgorithm {
    public void cplexSolution(int[][][] p) throws IloException {
        int l = p.length;
        int n = p[0].length;
        for (int i = 0; i < l; ++i) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < 2; ++k) {
                    p[i][j][k]--;
                }
            }
        }

        IloCplex cplex = new IloCplex();
        cplex.boolVar();
        IloIntVar[][] r = new IloIntVar[2 * n][];
        for (int i = 0; i < 2 * n; ++i) {
            r[i] = cplex.intVarArray(2 * n, 0, 1);
        }

        // symmetric
        for (int i = 0; i < 2 * n; ++i) {
            for (int j = 0; j < i; ++j) {
                cplex.addEq(r[i][j], r[j][i]);
            }
            cplex.addEq(r[i][i], 0);
        }
        // 3
        for (int i = 0; i < 2 * n; ++i) {
            cplex.addEq(cplex.sum(r[i]), 1);
        }

        IloIntVar[][] p_kl = new IloIntVar[l][];
        for (int i = 0; i < l; ++i) {
            p_kl[i] = cplex.intVarArray(n, 1, n);
        }
        // 4
        for (int i = 0; i < l; ++i) {
            for (int j = 0; j < n; ++j) {
                cplex.addLe(p_kl[i][j], j + 1);
            }
        }
        // 5
        for (int i = 0; i < l; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < j; ++k) {
                    cplex.addLe(cplex.diff(p_kl[i][j], p_kl[i][k]), cplex.prod(2 * n, cplex.diff(1, r[p[i][j][0]][p[i][k][0]])));
                    cplex.addLe(cplex.diff(p_kl[i][j], p_kl[i][k]), cplex.prod(2 * n, cplex.diff(1, r[p[i][j][0]][p[i][k][1]])));
                    cplex.addLe(cplex.diff(p_kl[i][j], p_kl[i][k]), cplex.prod(2 * n, cplex.diff(1, r[p[i][j][1]][p[i][k][0]])));
                    cplex.addLe(cplex.diff(p_kl[i][j], p_kl[i][k]), cplex.prod(2 * n, cplex.diff(1, r[p[i][j][1]][p[i][k][1]])));
                    cplex.addGe(cplex.diff(p_kl[i][j], p_kl[i][k]), cplex.prod(-2 * n, cplex.diff(1, r[p[i][j][0]][p[i][k][0]])));
                    cplex.addGe(cplex.diff(p_kl[i][j], p_kl[i][k]), cplex.prod(-2 * n, cplex.diff(1, r[p[i][j][0]][p[i][k][1]])));
                    cplex.addGe(cplex.diff(p_kl[i][j], p_kl[i][k]), cplex.prod(-2 * n, cplex.diff(1, r[p[i][j][1]][p[i][k][0]])));
                    cplex.addGe(cplex.diff(p_kl[i][j], p_kl[i][k]), cplex.prod(-2 * n, cplex.diff(1, r[p[i][j][1]][p[i][k][1]])));
                }
            }
        }
        IloIntVar[][] p_kl_indic = new IloIntVar[l][];
        for (int i = 0; i < l; ++i) {
            p_kl_indic[i] = cplex.intVarArray(n, 0, 1);
        }
        // 6
        for (int i = 0; i < l; ++i) {
            for (int j = 0; j < n; ++j) {
                cplex.addLe(cplex.prod(j + 1, p_kl_indic[i][j]), p_kl[i][j]);
            }
        }

        // 7
        IloNumExpr obj = cplex.constant(0);
        for (int i = 0; i < l; ++i) {
            obj = cplex.sum(obj, cplex.sum(p_kl_indic[i], 0, n));
        }
        cplex.addMaximize(obj);

        cplex.solve();

        int objvalue = (int) cplex.getObjValue();
        System.out.println("Objective value : " + objvalue);
        System.out.println("Builded edges : ");
        for (int i = 0; i < 2 * n; ++i) {
            double[] resR = cplex.getValues(r[i]);
            for (int j = 0; j < i; ++j) {
                if (resR[j] == 1.0)
                    System.out.println((j + 1) + " " + (i + 1));
            }
        }
        System.out.println("Cycle number :");
        for (int i = 0; i < l; ++i) {
            double[] resR = cplex.getValues(p_kl[i]);
            for (int j = 0; j < n; ++j) {
                System.out.print(resR[j] + " ");
            }
            System.out.println();
        }
    }
}
