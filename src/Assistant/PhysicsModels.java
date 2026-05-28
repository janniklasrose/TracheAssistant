package Assistant;

import javax.swing.JOptionPane;
import star.common.Continuum;
import star.common.ImplicitUnsteadyModel;
import star.common.ImplicitUnsteadySolver;
import star.common.InnerIterationStoppingCriterion;
import star.common.Model;
import star.common.ModelManager;
import star.common.PhysicalTimeStoppingCriterion;
import star.common.PhysicsContinuum;
import star.common.Region;
import star.common.SteadyModel;
import star.common.StepStoppingCriterion;
import star.common.TimeDiscretizationOption;
import star.flow.GravityModel;
import star.flow.IdealGasModel;
import star.keturb.KEpsilonTurbulence;
import star.keturb.KeTurbSolver;
import star.keturb.KeTwoLayerAllYplusWallTreatment;
import star.keturb.RkeTwoLayerTurbModel;
import star.kwturb.KOmegaTurbulence;
import star.kwturb.KwAllYplusWallTreatment;
import star.kwturb.KwTurbSolver;
import star.kwturb.SstKwTurbModel;
import star.material.SingleComponentGasModel;
import star.metrics.CellQualityRemediationModel;
import star.metrics.GradientsModel;
import star.metrics.ThreeDimensionalModel;
import star.passivescalar.PassiveScalarMaterial;
import star.passivescalar.PassiveScalarModel;
import star.passivescalar.PassiveScalarSolver;
import star.radiation.common.GrayThermalRadiationModel;
import star.radiation.common.RadiationModel;
import star.radiation.s2s.S2sModel;
import star.segregatedenergy.SegregatedEnergySolver;
import star.segregatedenergy.SegregatedFluidIsothermalModel;
import star.segregatedenergy.SegregatedFluidTemperatureModel;
import star.segregatedflow.SegregatedFlowModel;
import star.segregatedflow.SegregatedFlowSolver;
import star.turbulence.RansTurbulenceModel;
import star.turbulence.TurbulentModel;
import star.viewfactors.PatchPerBFaceProportion;
import star.viewfactors.ViewfactorsCalculatorModel;
import star.walldistance.WallDistanceModel;

public final class PhysicsModels {

    private PhysicsModels() {
    }

    public static PhysicsContinuum setPhysicsContinuum(ExtendedFunctionTaskController controller) {
        PhysicsContinuum physics = selectPhysics(controller);
        if (physics == null) {
            controller.printWarning("Physics continuum was not changed.");
            return null;
        }

        try {
            switchModel(physics, ThreeDimensionalModel.class, true);
            switchModel(physics, CellQualityRemediationModel.class, true);
            switchModel(physics, SingleComponentGasModel.class, true);
            switchModel(physics, SegregatedFlowModel.class, true);
            switchModel(physics, GradientsModel.class, true);
            switchModel(physics, IdealGasModel.class, true);
            switchModel(physics, PassiveScalarModel.class, true);
            if (physics.getModelManager().getModel(PassiveScalarModel.class)
                    .getPassiveScalarManager().getPassiveScalarMaterials().isEmpty()) {
                physics.getModelManager().getModel(PassiveScalarModel.class)
                        .getPassiveScalarManager()
                        .createPassiveScalarMaterial(PassiveScalarMaterial.class);
            }
            switchModel(physics, GravityModel.class, true);
            switchModel(physics, TurbulentModel.class, true);
        } catch (RuntimeException ex) {
            controller.printWarning("Could not set the base physics model set: " + ex.getMessage());
            return physics;
        }

        String thermal = controller.promptOption("Thermal model", "Please choose the thermal model",
                new String[]{"non-isothermal", "isothermal"}, "non-isothermal");
        if ("isothermal".equals(thermal)) {
            setIsothermal(controller, physics);
        } else {
            setNonisothermal(controller, physics);
        }

        String turbulence = controller.promptOption("Turbulence", "Please choose the turbulence model",
                new String[]{"RANS k-epsilon", "RANS k-omega"}, "RANS k-epsilon");
        if ("RANS k-omega".equals(turbulence)) {
            setRANSkw(controller, physics);
        } else {
            setRANSke(controller, physics);
        }

        String time = controller.promptOption("Time", "Please choose the time model",
                new String[]{"steady", "unsteady"}, "steady");
        if ("unsteady".equals(time)) {
            setUnsteady(controller, physics);
        } else {
            setSteady(controller, physics);
        }

        try {
            controller.sim.getSolverManager().getSolver(PassiveScalarSolver.class).setLeaveTemporaryStorage(true);
            controller.sim.getSolverManager().getSolver(SegregatedFlowSolver.class).setLeaveTemporaryStorage(true);
            controller.sim.getSolverManager().getSolver(SegregatedFlowSolver.class).setContinuityInitialization(true);
            controller.sim.getSolverManager().getSolver(SegregatedEnergySolver.class).setLeaveTemporaryStorage(true);
        } catch (RuntimeException ex) {
            controller.printWarning("Some solver initialization settings could not be applied.");
        }

        for (Region region : controller.sim.getRegionManager().getRegions()) {
            region.setPhysicsContinuum(physics);
        }

        controller.printMessage("Physics continuum set.");
        return physics;
    }

    public static PhysicsContinuum selectPhysics(ExtendedFunctionTaskController controller) {
        Continuum namedContinuum = controller.sim.getContinuumManager().hasObject(WorkflowState.AIR_PHYSICS);
        if (namedContinuum != null && !(namedContinuum instanceof PhysicsContinuum)) {
            namedContinuum.setPresentationName(WorkflowState.AIR_PHYSICS + " (old)");
            controller.printWarning("Renamed non-physics continuum called '" + WorkflowState.AIR_PHYSICS + "'.");
        }

        PhysicsContinuum existing = WorkflowState.airPhysics(controller.sim);
        if (existing != null) {
            return existing;
        }

        if (controller.sim.getContinuumManager().getObjectsOf(PhysicsContinuum.class).size() == 1) {
            PhysicsContinuum physics = controller.sim.getContinuumManager()
                    .getObjectsOf(PhysicsContinuum.class).iterator().next();
            physics.setPresentationName(WorkflowState.AIR_PHYSICS);
            return physics;
        }

        if (controller.sim.getContinuumManager().getObjectsOf(PhysicsContinuum.class).size() > 1) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Multiple physics continua exist. Create a new '" + WorkflowState.AIR_PHYSICS + "' continuum?",
                    "Physics continuum", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        PhysicsContinuum physics = controller.sim.getContinuumManager().createContinuum(PhysicsContinuum.class);
        physics.setPresentationName(WorkflowState.AIR_PHYSICS);
        return physics;
    }

    public static boolean setRANSke(ExtendedFunctionTaskController controller, PhysicsContinuum physics) {
        if (!ensureTurbulence(controller, physics)) {
            return false;
        }
        try {
            switchModel(physics, KwAllYplusWallTreatment.class, false);
            switchModel(physics, WallDistanceModel.class, false);
            switchModel(physics, SstKwTurbModel.class, false);
            switchModel(physics, KOmegaTurbulence.class, false);
            switchModel(physics, RansTurbulenceModel.class, false);
            switchModel(physics, RansTurbulenceModel.class, true);
            switchModel(physics, KEpsilonTurbulence.class, true);
            switchModel(physics, RkeTwoLayerTurbModel.class, true);
            switchModel(physics, WallDistanceModel.class, true);
            switchModel(physics, KeTwoLayerAllYplusWallTreatment.class, true);
            controller.sim.getSolverManager().getSolver(SegregatedFlowSolver.class).setContinuityInitialization(true);
            controller.sim.getSolverManager().getSolver(KeTurbSolver.class).setTurbulenceInitialization(true);
            controller.sim.getSolverManager().getSolver(KeTurbSolver.class).setLeaveTemporaryStorage(true);
            controller.printMessage("RANS k-epsilon physics set.");
            return true;
        } catch (RuntimeException ex) {
            controller.printWarning("Could not set RANS k-epsilon physics: " + ex.getMessage());
            return false;
        }
    }

    public static boolean setRANSkw(ExtendedFunctionTaskController controller, PhysicsContinuum physics) {
        if (!ensureTurbulence(controller, physics)) {
            return false;
        }
        try {
            switchModel(physics, KeTwoLayerAllYplusWallTreatment.class, false);
            switchModel(physics, WallDistanceModel.class, false);
            switchModel(physics, RkeTwoLayerTurbModel.class, false);
            switchModel(physics, KEpsilonTurbulence.class, false);
            switchModel(physics, RansTurbulenceModel.class, false);
            switchModel(physics, RansTurbulenceModel.class, true);
            switchModel(physics, KOmegaTurbulence.class, true);
            switchModel(physics, SstKwTurbModel.class, true);
            switchModel(physics, WallDistanceModel.class, true);
            switchModel(physics, KwAllYplusWallTreatment.class, true);
            controller.sim.getSolverManager().getSolver(SegregatedFlowSolver.class).setContinuityInitialization(true);
            controller.sim.getSolverManager().getSolver(KwTurbSolver.class).setTurbulenceInitialization(true);
            controller.sim.getSolverManager().getSolver(KwTurbSolver.class).setLeaveTemporaryStorage(true);
            controller.printMessage("RANS k-omega physics set.");
            return true;
        } catch (RuntimeException ex) {
            controller.printWarning("Could not set RANS k-omega physics: " + ex.getMessage());
            return false;
        }
    }

    public static boolean setIsothermal(ExtendedFunctionTaskController controller, PhysicsContinuum physics) {
        try {
            switchModel(physics, GrayThermalRadiationModel.class, false);
            switchModel(physics, ViewfactorsCalculatorModel.class, false);
            switchModel(physics, S2sModel.class, false);
            switchModel(physics, RadiationModel.class, false);
            switchModel(physics, SegregatedFluidTemperatureModel.class, false);
            switchModel(physics, SegregatedFluidIsothermalModel.class, true);
            controller.printMessage("Isothermal physics set.");
            return true;
        } catch (RuntimeException ex) {
            controller.printWarning("Could not set isothermal physics: " + ex.getMessage());
            return false;
        }
    }

    public static boolean setNonisothermal(ExtendedFunctionTaskController controller, PhysicsContinuum physics) {
        try {
            switchModel(physics, SegregatedFluidIsothermalModel.class, false);
            switchModel(physics, SegregatedFluidTemperatureModel.class, true);
            switchModel(physics, RadiationModel.class, true);
            switchModel(physics, S2sModel.class, true);
            switchModel(physics, ViewfactorsCalculatorModel.class, true);
            switchModel(physics, GrayThermalRadiationModel.class, true);
            for (Region region : controller.sim.getRegionManager().getRegions()) {
                region.getValues().get(PatchPerBFaceProportion.class).setPatchPerBFaceProportion(20.0);
            }
            controller.printMessage("Non-isothermal physics set.");
            return true;
        } catch (RuntimeException ex) {
            controller.printWarning("Could not set non-isothermal physics: " + ex.getMessage());
            return false;
        }
    }

    public static boolean setSteady(ExtendedFunctionTaskController controller, PhysicsContinuum physics) {
        try {
            switchModel(physics, ImplicitUnsteadyModel.class, false);
            switchModel(physics, SteadyModel.class, true);
            int steadyIterations = controller.promptInt("Number of steady iterations", 100);
            StepStoppingCriterion criterion = (StepStoppingCriterion) controller.sim
                    .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps");
            criterion.setMaximumNumberSteps(steadyIterations);
            criterion.setIsUsed(true);
            controller.printMessage("Steady time model set.");
            return true;
        } catch (RuntimeException ex) {
            controller.printWarning("Could not set steady physics: " + ex.getMessage());
            return false;
        }
    }

    public static boolean setUnsteady(ExtendedFunctionTaskController controller, PhysicsContinuum physics) {
        try {
            switchModel(physics, SteadyModel.class, false);
            switchModel(physics, ImplicitUnsteadyModel.class, true);
            StepStoppingCriterion maxSteps = (StepStoppingCriterion) controller.sim
                    .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps");
            maxSteps.setIsUsed(false);

            double timeStep = controller.promptDouble("Time step [s]", 0.2);
            ImplicitUnsteadySolver unsteadySolver = controller.sim.getSolverManager()
                    .getSolver(ImplicitUnsteadySolver.class);
            unsteadySolver.getTimeStep().setUnits(controller.sim.getUnitsManager().getUnits("s"));
            unsteadySolver.getTimeStep().setValue(timeStep);
            unsteadySolver.getTimeDiscretizationOption().setSelected(TimeDiscretizationOption.Type.SECOND_ORDER);

            int innerIterations = controller.promptInt("Number of inner iterations", 10);
            InnerIterationStoppingCriterion innerCriterion = (InnerIterationStoppingCriterion) controller.sim
                    .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Inner Iterations");
            innerCriterion.setMaximumNumberInnerIterations(innerIterations);

            double physicalTime = controller.promptDouble("Maximum physical time [min]", 5.0);
            PhysicalTimeStoppingCriterion timeCriterion = (PhysicalTimeStoppingCriterion) controller.sim
                    .getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time");
            timeCriterion.getMaximumTime().setUnits(controller.sim.getUnitsManager().getUnits("min"));
            timeCriterion.getMaximumTime().setValue(physicalTime);
            controller.printMessage("Unsteady time model set.");
            return true;
        } catch (RuntimeException ex) {
            controller.printWarning("Could not set unsteady physics: " + ex.getMessage());
            return false;
        }
    }

    public static <T extends Model> void switchModel(PhysicsContinuum physics, Class<T> type, boolean on) {
        ModelManager modelManager = physics.getModelManager();
        Model model = modelManager.hasObjectOfClass(type.getCanonicalName());
        if (on && model == null) {
            physics.enable(type);
        } else if (!on && model != null) {
            physics.disableModel(model);
        }
    }

    private static boolean ensureTurbulence(ExtendedFunctionTaskController controller, PhysicsContinuum physics) {
        if (physics.getModelManager().hasModel(TurbulentModel.class) == null) {
            try {
                switchModel(physics, TurbulentModel.class, true);
            } catch (RuntimeException ex) {
                controller.printWarning("Turbulence could not be enabled.");
                return false;
            }
        }
        return true;
    }
}
