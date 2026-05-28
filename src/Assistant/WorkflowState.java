package Assistant;

import star.common.FvRepresentation;
import star.common.GeometryPart;
import star.common.PhysicsContinuum;
import star.common.Region;
import star.common.Simulation;
import star.common.Solution;

public final class WorkflowState {

    public static final String TRACHEA_PART = "Trachea";
    public static final String AIRWAYS_REGION = "Airways";
    public static final String AIR_PHYSICS = "Air";
    public static final String VOLUME_MESH = "Volume Mesh";

    private WorkflowState() {
    }

    public static boolean isTracheaPart(GeometryPart part) {
        return part != null && TRACHEA_PART.equals(part.getPresentationName());
    }

    public static GeometryPart tracheaPart(Simulation sim) {
        return sim.getGeometryPartManager().hasObject(TRACHEA_PART);
    }

    public static boolean isAirwaysRegion(Region region) {
        return region != null && AIRWAYS_REGION.equals(region.getPresentationName());
    }

    public static Region airwaysRegion(Simulation sim) {
        return sim.getRegionManager().hasObject(AIRWAYS_REGION);
    }

    public static boolean isAirPhysics(PhysicsContinuum continuum) {
        return continuum != null && AIR_PHYSICS.equals(continuum.getPresentationName());
    }

    public static PhysicsContinuum airPhysics(Simulation sim) {
        return sim.getContinuumManager().hasObject(AIR_PHYSICS) instanceof PhysicsContinuum
                ? (PhysicsContinuum) sim.getContinuumManager().hasObject(AIR_PHYSICS)
                : null;
    }

    public static boolean isVolumeMesh(FvRepresentation representation) {
        return representation != null && VOLUME_MESH.equals(representation.getPresentationName());
    }

    public static FvRepresentation volumeMesh(Simulation sim) {
        return sim.getRepresentationManager().hasObject(VOLUME_MESH) instanceof FvRepresentation
                ? (FvRepresentation) sim.getRepresentationManager().hasObject(VOLUME_MESH)
                : null;
    }

    public static boolean isInitialSolution(Solution solution) {
        return solution != null;
    }
}
