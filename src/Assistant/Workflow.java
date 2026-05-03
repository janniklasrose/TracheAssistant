package Assistant;

import java.util.Collections;
import star.assistant.CSOCondition;
import star.assistant.CSOLookupConditionTrigger;
import star.common.GeometryPart;
import star.common.filters.Predicate;

public class Workflow {

    public static synchronized CSOCondition<GeometryPart> Condition() {
        CSOCondition<GeometryPart> condition = new CSOCondition<>();
        condition.setDesc("A valid part must be present.");
        condition.setTriggers(Collections.singleton(new CSOLookupConditionTrigger<>(GeometryPart.class)));
        condition.setPredicate(new Predicate<GeometryPart>() {
            @Override
            public boolean evaluate(GeometryPart part) {
                return true; // always satisfies
            }
        });
        return condition;
    }

}
