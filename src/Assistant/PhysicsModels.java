package Assistant;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.base.report.Monitor;
import star.common.Continuum;
import star.common.ImplicitUnsteadyModel;
import star.common.ImplicitUnsteadySolver;
import star.common.InnerIterationStoppingCriterion;
import star.common.PhysicalTimeStoppingCriterion;
import star.common.PhysicsContinuum;
import star.common.Region;
import star.common.ResidualMonitor;
import star.common.ResidualPlot;
import star.common.Solver;
import star.common.StarPlot;
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

public class PhysicsModels {
    
        // ---------- Action ----------

        //TODO: handle cases where incorrect models are activated which prevent the correct models from being added
        public void setPhysicsContinuum() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------
            physicsAir = selectPhysics();
            if (physicsAir == null) {
                printMessage("Physics not set!");
                return;
            }

            // fixed
            try {
                switchModel_Physics(physicsAir, ThreeDimensionalModel.class, true);
                switchModel_Physics(physicsAir, CellQualityRemediationModel.class, true);
                switchModel_Physics(physicsAir, SingleComponentGasModel.class, true);
                switchModel_Physics(physicsAir, SegregatedFlowModel.class, true);
                switchModel_Physics(physicsAir, GradientsModel.class, true);
                switchModel_Physics(physicsAir, IdealGasModel.class, true);
                switchModel_Physics(physicsAir, PassiveScalarModel.class, true);
                if (physicsAir.getModelManager().getModel(PassiveScalarModel.class).getPassiveScalarManager().getPassiveScalarMaterials().isEmpty()) {
                    physicsAir.getModelManager().getModel(PassiveScalarModel.class).getPassiveScalarManager().createPassiveScalarMaterial(PassiveScalarMaterial.class);
                }
                switchModel_Physics(physicsAir, GravityModel.class, true);
            } catch (Exception e) {
                printWarning("Could not set physics models! Check existing models.");
                return;
            }
            // isothermal
            String[] isothermal_opts = {"non-isothermal", "isothermal"};
            int isothermal = JOptionPane.showOptionDialog(null, "Please choose", "Isothermal", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, isothermal_opts, isothermal_opts[0]);
            switch (isothermal_opts[isothermal]) {
                case "non-isothermal":
                    setNonisothermal(physicsAir);
                    break;
                case "isothermal":
                    setIsothermal(physicsAir);
                    break;
                default: // CLOSE_OPTION -> non-isothermal
                    setNonisothermal(physicsAir);
                    break;
            }
            // turbulence
            try {
                switchModel_Physics(physicsAir, TurbulentModel.class, true);
            } catch (Exception e) {
                printWarning("Could not enable turbulence! Check existing models.");
            }
            String[] turbulence_opts = {"RANS k-epsilon", "RANS k-omega"};
            int turbulence = JOptionPane.showOptionDialog(null, "Please choose", "Turbulence", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, turbulence_opts, turbulence_opts[0]);
            switch (turbulence_opts[turbulence]) {
                case "RANS k-epsilon":
                    setRANSke(physicsAir);
                    break;
                case "RANS k-omega":
                    setRANSkw(physicsAir);
                    break;
                default: // CLOSE_OPTION -> RANS k-epsilon
                    setRANSke(physicsAir);
                    break;
            }
            // time
            String[] time_opts = {"steady", "unsteady"};
            int time = JOptionPane.showOptionDialog(null, "Please choose", "Time", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, time_opts, time_opts[0]);
            switch (time_opts[time]) {
                case "steady":
                    setSteady(physicsAir);
                    break;
                case "unsteady":
                    setUnsteady(physicsAir);
                    break;
                default: // CLOSE_OPTION -> steady
                    setSteady(physicsAir);
                    break;
            }

            sim.getSolverManager().getSolver(PassiveScalarSolver.class).setLeaveTemporaryStorage(true);
            sim.getSolverManager().getSolver(SegregatedFlowSolver.class).setLeaveTemporaryStorage(true);
            sim.getSolverManager().getSolver(SegregatedFlowSolver.class).setContinuityInitialization(true);
            sim.getSolverManager().getSolver(SegregatedEnergySolver.class).setLeaveTemporaryStorage(true);

            for (Region region_i : sim.getRegionManager().getRegions()) {
                region_i.setPhysicsContinuum(physicsAir);
            }
            addToTaskLookup(physicsAir);

            // ----------------------------------------------------------------------------------
            selectAndExpandNodeExclusive(physicsAir);
            printMessage("Physics continuum set!");

        }

        private PhysicsContinuum selectPhysics() {

            Continuum continuumWithName = sim.getContinuumManager().hasObject(ConditionManager.PHYSICSAIR_NAME);
            if (continuumWithName != null && !(continuumWithName instanceof PhysicsContinuum)) {
                printWarning("Non-physics continuum called '" + ConditionManager.PHYSICSAIR_NAME + "' had to be renamed!");
                continuumWithName.setPresentationName(ConditionManager.PHYSICSAIR_NAME + " (old)");
            }

            ArrayList<PhysicsContinuum> physicsContinua = new ArrayList(sim.getContinuumManager().getObjectsOf(PhysicsContinuum.class));
            switch (physicsContinua.size()) {
                case 0:
                    PhysicsContinuum newPhysics = sim.getContinuumManager().createContinuum(PhysicsContinuum.class);
                    newPhysics.setPresentationName(ConditionManager.PHYSICSAIR_NAME);
                    return newPhysics;
                case 1:
                    PhysicsContinuum currentPhysics = physicsContinua.get(0);
                    if (currentPhysics.getPresentationName().matches(ConditionManager.PHYSICSAIR_NAME)) {
                        return currentPhysics;
                    } else {
                        currentPhysics.setPresentationName(ConditionManager.PHYSICSAIR_NAME);
                        return currentPhysics;
                    }
                default:
                    switch (JOptionPane.showConfirmDialog(null, "Already found " + physicsContinua.size() + " physics continua! I will delete them.", "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null)) {
                        case JOptionPane.YES_OPTION:
                            PhysicsContinuum correctPhysics = null;
                            for (PhysicsContinuum physics_i : sim.getContinuumManager().getObjectsOf(PhysicsContinuum.class)) {
                                if (physics_i.getPresentationName().matches(ConditionManager.PHYSICSAIR_NAME)) {
                                    correctPhysics = physics_i;
                                    continue;
                                }
                                try {
                                    for (Region reg : physics_i.getRegions()) {
                                        physics_i.erase(reg);
                                    }
                                    try {
                                        removeFromTaskLookup(physics_i);
                                    } catch (Exception e) {
                                        // not in list
                                    }
                                    String tempname = physics_i.getPresentationName();
                                    sim.getContinuumManager().remove(physics_i);
                                    printMessage("PhysicsContinuum '" + tempname + "' was successfully deleted!");
                                } catch (Exception e) {
                                    printWarning("Could not delete PhysicsContinuum '" + physics_i.getPresentationName() + "'");
                                }
                            }
                            if (correctPhysics == null) {
                                correctPhysics = sim.getContinuumManager().createContinuum(PhysicsContinuum.class);
                                correctPhysics.setPresentationName(ConditionManager.PHYSICSAIR_NAME);
                            }
                            return correctPhysics;
                        default:
                            return null;
                    }
            }
        }

        private PhysicsContinuum physicsAir = null;

        // ---------- Action ----------

        public void setRANSke() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------
            if (physicsAir == null) {
                physicsAir = selectPhysics();
                if (physicsAir == null) {
                    printMessage("RANS k-epsilon physics not set!");
                    return;
                }
            }

            if (!setRANSke(physicsAir)) {
                return;
            }

            // ----------------------------------------------------------------------------------
            printMessage("RANS k-epsilon physics set!");

        }

        private boolean setRANSke(PhysicsContinuum physics) {
            if (physics.getModelManager().hasModel(TurbulentModel.class) == null) {
                printWarning("Turbulence not enabled!");
                return false;
            }
            try {
                // kw OFF
                switchModel_Physics(physics, KwAllYplusWallTreatment.class, false);
                switchModel_Physics(physics, WallDistanceModel.class, false);
                switchModel_Physics(physics, SstKwTurbModel.class, false);
                switchModel_Physics(physics, KOmegaTurbulence.class, false);
                switchModel_Physics(physics, RansTurbulenceModel.class, false);
                // ke ON
                switchModel_Physics(physics, RansTurbulenceModel.class, true);
                switchModel_Physics(physics, KEpsilonTurbulence.class, true);
                switchModel_Physics(physics, RkeTwoLayerTurbModel.class, true);
                switchModel_Physics(physics, WallDistanceModel.class, true);
                switchModel_Physics(physics, KeTwoLayerAllYplusWallTreatment.class, true);
            } catch (Exception e) {
                printWarning("Could not set RANS_ke! Check existing models.");
                return false;
            }

            sim.getSolverManager().getSolver(SegregatedFlowSolver.class).setContinuityInitialization(true);
            sim.getSolverManager().getSolver(KeTurbSolver.class).setTurbulenceInitialization(true);
            sim.getSolverManager().getSolver(KeTurbSolver.class).setLeaveTemporaryStorage(true);

            return true;
        }

        // ---------- Action ----------

        public void setRANSkw() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------
            if (physicsAir == null) {
                physicsAir = selectPhysics();
                if (physicsAir == null) {
                    printMessage("RANS k-omega physics not set!");
                    return;
                }
            }

            if (!setRANSkw(physicsAir)) {
                return;
            }

            // ----------------------------------------------------------------------------------
            printMessage("RANS k-omega physics set!");

        }

        private boolean setRANSkw(PhysicsContinuum physics) {
            if (physics.getModelManager().hasModel(TurbulentModel.class) == null) {
                printWarning("Turbulence not enabled!");
                return false;
            }
            try {
                // ke OFF
                switchModel_Physics(physics, KeTwoLayerAllYplusWallTreatment.class, false);
                switchModel_Physics(physics, WallDistanceModel.class, false);
                switchModel_Physics(physics, RkeTwoLayerTurbModel.class, false);
                switchModel_Physics(physics, KEpsilonTurbulence.class, false);
                switchModel_Physics(physics, RansTurbulenceModel.class, false);
                // kw ON
                switchModel_Physics(physics, RansTurbulenceModel.class, true);
                switchModel_Physics(physics, KOmegaTurbulence.class, true);
                switchModel_Physics(physics, SstKwTurbModel.class, true);
                switchModel_Physics(physics, WallDistanceModel.class, true);
                switchModel_Physics(physics, KwAllYplusWallTreatment.class, true);
            } catch (Exception e) {
                printWarning("Could not set RANS_kw! Check existing models.");
                return false;
            }

            sim.getSolverManager().getSolver(SegregatedFlowSolver.class).setContinuityInitialization(true);
            sim.getSolverManager().getSolver(KwTurbSolver.class).setTurbulenceInitialization(true);
            sim.getSolverManager().getSolver(KwTurbSolver.class).setLeaveTemporaryStorage(true);

            return true;
        }

        // ---------- Action ----------

        public void setIsothermal() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------
            if (physicsAir == null) {
                physicsAir = selectPhysics();
                if (physicsAir == null) {
                    printMessage("Isothermal physics not set!");
                    return;
                }
            }

            if (!setIsothermal(physicsAir)) {
                return;
            }

            // ----------------------------------------------------------------------------------
            printMessage("Isothermal physics set!");

        }

        private boolean setIsothermal(PhysicsContinuum physics) {
            if (physics.getModelManager().hasModel(SegregatedFlowModel.class) == null) {
                printWarning("Segregated flow not enabled!");
                return false;
            }
            try {
                switchModel_Physics(physics, GrayThermalRadiationModel.class, false);
                switchModel_Physics(physics, ViewfactorsCalculatorModel.class, false);
                switchModel_Physics(physics, S2sModel.class, false);
                switchModel_Physics(physics, RadiationModel.class, false);
                switchModel_Physics(physics, SegregatedFluidTemperatureModel.class, false);
                switchModel_Physics(physics, SegregatedFluidIsothermalModel.class, true);
            } catch (Exception e) {
                printWarning("Could not set isothermal! Check existing models.");
                return false;
            }
            return true;
        }

        // ---------- Action ----------

        public void setNonisothermal() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------
            if (physicsAir == null) {
                physicsAir = selectPhysics();
                if (physicsAir == null) {
                    printMessage("Non-isothermal physics not set!");
                    return;
                }
            }

            if (!setNonisothermal(physicsAir)) {
                return;
            }

            // ----------------------------------------------------------------------------------
            printMessage("Non-isothermal physics set!");

        }

        private boolean setNonisothermal(PhysicsContinuum physics) {
            if (physics.getModelManager().hasModel(SegregatedFlowModel.class) == null) {
                printWarning("Segregated flow not enabled!");
                return false;
            }
            try {
                switchModel_Physics(physics, SegregatedFluidIsothermalModel.class, false);
                switchModel_Physics(physics, SegregatedFluidTemperatureModel.class, true);
                switchModel_Physics(physics, RadiationModel.class, true);
                switchModel_Physics(physics, S2sModel.class, true);
                switchModel_Physics(physics, ViewfactorsCalculatorModel.class, true);
                switchModel_Physics(physics, GrayThermalRadiationModel.class, true);
            } catch (Exception e) {
                printWarning("Could not set non-isothermal! Check existing models.");
                return false;
            }

            for (Region reg_i : sim.getRegionManager().getRegions()) {
                reg_i.getValues().get(PatchPerBFaceProportion.class).setPatchPerBFaceProportion(20.0);
            }

            return true;
        }

        // ---------- Action ----------

        public void setSteady() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------
            if (physicsAir == null) {
                physicsAir = selectPhysics();
                if (physicsAir == null) {
                    printMessage("Steady physics not set!");
                    return;
                }
            }

            if (!setSteady(physicsAir)) {
                return;
            }

            // ----------------------------------------------------------------------------------
            printMessage("Steady physics set!");

        }

        private boolean setSteady(PhysicsContinuum physics) {
            try {
                switchModel_Physics(physics, ImplicitUnsteadyModel.class, false);
                switchModel_Physics(physics, SteadyModel.class, true);
            } catch (Exception e) {
                printWarning("Could not set steady time! Check existing models.");
                return false;
            }

            int steadyIterations = promptUserForInput("Number of steady iterations", 100);
            ((StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps")).setMaximumNumberSteps(steadyIterations);
            ((StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps")).setIsUsed(true);

            return true;
        }

        // ---------- Action ----------

        public void setUnsteady() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------
            if (physicsAir == null) {
                physicsAir = selectPhysics();
                if (physicsAir == null) {
                    printMessage("Unsteady physics not set!");
                    return;
                }
            }

            if (!setUnsteady(physicsAir)) {
                return;
            }

            // ----------------------------------------------------------------------------------
            printMessage("Unsteady physics set!");

        }

        private boolean setUnsteady(PhysicsContinuum physics) {
            try {
                switchModel_Physics(physics, SteadyModel.class, false);
                switchModel_Physics(physics, ImplicitUnsteadyModel.class, true);
            } catch (Exception e) {
                printWarning("Could not set unsteady time! Check existing models.");
                return false;
            }

            ((StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps")).setIsUsed(false);
            double timeStep = promptUserForInput("Time step [s]", 0.2);
            sim.getSolverManager().getSolver(ImplicitUnsteadySolver.class).getTimeStep().setUnits(sim.getUnitsManager().getUnits("s"));
            sim.getSolverManager().getSolver(ImplicitUnsteadySolver.class).getTimeStep().setValue(timeStep);
            sim.getSolverManager().getSolver(ImplicitUnsteadySolver.class).getTimeDiscretizationOption().setSelected(TimeDiscretizationOption.Type.SECOND_ORDER);
            int innerIterations = promptUserForInput("Number of inner iterations", 10);
            ((InnerIterationStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Inner Iterations")).setMaximumNumberInnerIterations(innerIterations);
            double physicalTime = promptUserForInput("Maximum physical time [min]", 5.0);
            ((PhysicalTimeStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time")).getMaximumTime().setUnits(sim.getUnitsManager().getUnits("min"));
            ((PhysicalTimeStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Physical Time")).getMaximumTime().setValue(physicalTime);

            return true;
        }

}
