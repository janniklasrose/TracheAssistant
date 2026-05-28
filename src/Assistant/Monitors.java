package Assistant;

import star.common.Solver;
import star.keturb.KeTurbSolver;
import star.kwturb.KwTurbSolver;
import star.passivescalar.PassiveScalarSolver;
import star.segregatedenergy.SegregatedEnergySolver;
import star.segregatedflow.SegregatedFlowSolver;

public final class Monitors {

    private Monitors() {
    }

    public static void setConvergence(ExtendedFunctionTaskController controller) {
        for (Solver solver : controller.sim.getSolverManager().getObjects()) {
            if (solver instanceof SegregatedFlowSolver) {
                ((SegregatedFlowSolver) solver).setLeaveTemporaryStorage(true);
                ((SegregatedFlowSolver) solver).setContinuityInitialization(true);
            } else if (solver instanceof SegregatedEnergySolver) {
                ((SegregatedEnergySolver) solver).setLeaveTemporaryStorage(true);
            } else if (solver instanceof KeTurbSolver) {
                ((KeTurbSolver) solver).setLeaveTemporaryStorage(true);
                ((KeTurbSolver) solver).setTurbulenceInitialization(true);
            } else if (solver instanceof KwTurbSolver) {
                ((KwTurbSolver) solver).setLeaveTemporaryStorage(true);
                ((KwTurbSolver) solver).setTurbulenceInitialization(true);
            } else if (solver instanceof PassiveScalarSolver) {
                ((PassiveScalarSolver) solver).setLeaveTemporaryStorage(true);
            }
        }
        controller.printMessage("Convergence-related solver storage and initialization settings were applied.");
        controller.printMessage("Custom correction monitors are WIP because the external MacroHelper library is not part of this repository.");
    }

    public static void setResources(ExtendedFunctionTaskController controller) {
        controller.printMessage("Resource monitor setup is WIP because the required report helper is not part of this repository.");
    }
}
