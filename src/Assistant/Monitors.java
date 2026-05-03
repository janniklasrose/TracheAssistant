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


public class Monitors {
    
        //TODO: add more criteria, i.e. asymptotic monitors etc.
        public void setConvergence() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------

            // Get objects
            Region sim_region = sim.getRegionManager().getRegion(ConditionManager.REGIONCABIN_NAME);
            ArrayList<Region> regions = new ArrayList(sim.getRegionManager().getRegions());

            // Helper class with methods for creating reports, etc.
            Source.MacroHelper.Base helper = new Source.MacroHelper.Base(sim, sim_region);

            // Group name
            String groupName_convergence = "Convergence";

            // Residuals
            helper.group_AllMonitorsOfType(ResidualMonitor.class, groupName_convergence);
            helper.manageMonitors_normalizeResiduals(true);

            // Solver
            for (Solver sol : sim.getSolverManager().getObjects()) {
                if (sol instanceof SegregatedFlowSolver) {
                    ((SegregatedFlowSolver) sol).setLeaveTemporaryStorage(true);
                    ((SegregatedFlowSolver) sol).setContinuityInitialization(true);
                }
                if (sol instanceof SegregatedEnergySolver) {
                    ((SegregatedEnergySolver) sol).setLeaveTemporaryStorage(true);
                }
                if (sol instanceof KeTurbSolver) {
                    ((KeTurbSolver) sol).setLeaveTemporaryStorage(true);
                    ((KeTurbSolver) sol).setTurbulenceInitialization(true);
                }
                if (sol instanceof KwTurbSolver) {
                    ((KwTurbSolver) sol).setLeaveTemporaryStorage(true);
                    ((KwTurbSolver) sol).setTurbulenceInitialization(true);
                }
                if (sol instanceof PassiveScalarSolver) {
                    ((PassiveScalarSolver) sol).setLeaveTemporaryStorage(true);
                }
            }

            // Corrections
            ArrayList<Monitor> m_velo = new ArrayList(0);
            m_velo.add(helper.createMonitor_Report("maxCorrectionVelocity", helper.createReport_Max("maxCorrectionVelocity", helper.createFieldFunction_Scalar("absCorrection_U", "max(abs($U_VelocitySource),max(abs($V_VelocitySource),abs($W_VelocitySource)))"), helper.putObjects(regions), groupName_convergence), groupName_convergence));
            helper.createPlot_Monitor("Velocity Corrections", m_velo, groupName_convergence);
            ArrayList<Monitor> m_pres = new ArrayList(0);
            m_pres.add(helper.createMonitor_Report("maxCorrectionPressure", helper.createReport_Max("maxCorrectionPressure", helper.createFieldFunction_Scalar("absCorrection_P", "abs($PPrime)"), helper.putObjects(regions), groupName_convergence), groupName_convergence));
            helper.createPlot_Monitor("Pressure Corrections", m_pres, groupName_convergence);
            ArrayList<Monitor> m_temp = new ArrayList(0);
            m_temp.add(helper.createMonitor_Report("maxCorrectionTemperature", helper.createReport_Max("maxCorrectionTemperature", helper.createFieldFunction_Scalar("absCorrection_T", "abs($TemperatureSource)"), helper.putObjects(regions), groupName_convergence), groupName_convergence));
            helper.createPlot_Monitor("Temperature Corrections", m_temp, groupName_convergence);
            ArrayList<ResidualPlot> residualPlots = helper.putPlots(ResidualPlot.class);
            if (residualPlots.size() == 1) {
                StarPlot residualPlot = residualPlots.get(0);
                StarPlot otherPlot_sameName = helper.putPlot("Residuals");
                if (otherPlot_sameName != residualPlot) {
                    otherPlot_sameName.setPresentationName("Residuals (old)");
                }
                residualPlot.setPresentationName("Residuals");
                helper.group(groupName_convergence, residualPlot);
            }

            // ----------------------------------------------------------------------------------

            printMessage("Convergence monitors set!");

        }

        // ---------- Action ----------

        //TODO: commented (need to write the methods in Helper.Base for the two reports)
        public void setResources() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------

            // Get objects
            Region sim_region = sim.getRegionManager().getRegion(ConditionManager.REGIONCABIN_NAME);
            ArrayList<Region> regions = new ArrayList(sim.getRegionManager().getRegions());

            // Helper class with methods for creating reports, etc.
            Source.MacroHelper.Base helper = new Source.MacroHelper.Base(sim, sim_region);

            // Group name
            String groupName_resources = "Resources";

            // Resources
            /*
            ArrayList<Monitor> m_mem = new ArrayList(0);
            m_mem.add(helper.createMonitor_Report("MaxMemory", helper.createReport_MaximumMemory(), groupName_resources));
            helper.createPlot_Monitor("MaxMemory", m_mem, groupName_resources);
            ArrayList<Monitor> m_cpu = new ArrayList(0);
            m_cpu.add(helper.createMonitor_Report("SolverTime", helper.createReport_TimestepElapsedTime(), groupName_resources));
            helper.createPlot_Monitor("SolverTime", m_cpu, groupName_resources);
            */

            // ----------------------------------------------------------------------------------

            printMessage("Resource monitors set!");

        }
}
