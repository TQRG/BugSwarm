package betsy.bpmn.model;

import betsy.common.model.TestAssertion;
import betsy.common.model.TestCase;
import betsy.common.util.StringUtils;

import java.util.*;

public class BPMNTestCase extends TestCase {

    public BPMNTestCase() {
        this.getTestSteps().add(new BPMNTestStep());
    }

    private BPMNTestCase addInputTestString(BPMNTestInput value) {
        getTestStep().setInput(value);

        return this;
    }

    public BPMNTestCase inputA() {
        return addInputTestString(BPMNTestInput.INPUT_A);
    }

    public BPMNTestCase inputB() {
        return addInputTestString(BPMNTestInput.INPUT_B);
    }

    public BPMNTestCase inputAB() {
        return addInputTestString(BPMNTestInput.INPUT_AB);
    }

    public BPMNTestCase inputC() {
        return addInputTestString(BPMNTestInput.INPUT_C);
    }

    public BPMNTestCase assertTask1() {
        return addAssertion(BPMNAssertions.SCRIPT_task1);
    }

    private BPMNTestCase addAssertion(BPMNAssertions script_task1) {
        getTestStep().addAssertions(script_task1);

        return this;
    }

    public BPMNTestCase assertTask2() {
        return addAssertion(BPMNAssertions.SCRIPT_task2);
    }

    public BPMNTestCase assertTask3() {
        return addAssertion(BPMNAssertions.SCRIPT_task3);
    }

    public BPMNTestCase assertTask4() {
        return addAssertion(BPMNAssertions.SCRIPT_task4);
    }

    public BPMNTestCase assertTask5() {
        return addAssertion(BPMNAssertions.SCRIPT_task5);
    }

    public BPMNTestCase optionDelay(int delay) {
        getTestStep().setDelay(delay);

        return this;
    }

    public BPMNTestCase assertRuntimeException() {
        return addAssertion(BPMNAssertions.ERROR_RUNTIME);
    }

    public BPMNTestCase assertErrorThrownErrorEvent() {
        return addAssertion(BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
    }

    public BPMNTestCase assertErrorThrownEscalationEvent() {
        return addAssertion(BPMNAssertions.ERROR_THROWN_ESCALATION_EVENT);
    }

    public BPMNTestCase assertGenericError() {
        return addAssertion(BPMNAssertions.ERROR_GENERIC);
    }

    public BPMNTestCase assertExecutionParallel() {
        return addAssertion(BPMNAssertions.EXECUTION_PARALLEL);
    }

    public BPMNTestCase assertDataCorrect() {
        return addAssertion(BPMNAssertions.DATA_CORRECT);
    }

    public Optional<Integer> getDelay() {
        return getTestStep().getDelay();
    }

    public BPMNTestStep getTestStep() {
        return (BPMNTestStep) Objects.requireNonNull(getTestSteps().get(0), "call input methods before!");
    }

    public List<String> getAssertions() {
        List<TestAssertion> assertions = getTestStep().getAssertions();

        List<String> result = new ArrayList<>();
        for (TestAssertion assertion : assertions) {
            BPMNTestAssertion bpmnTestAssertion = (BPMNTestAssertion) assertion;
            result.add(bpmnTestAssertion.getAssertion().toString());
        }
        return result;
    }

    public String getNormalizedTestCaseName() {
        StringBuilder sb = new StringBuilder();
        sb.append("test").append(getNumber()).append("Assert");
        for (String assertion : getAssertions()) {
            sb.append(StringUtils.capitalize(assertion));
        }
        return sb.toString();
    }

    public List<BPMNTestVariable> getVariables() {
        List<BPMNTestVariable> result = new ArrayList<>();

        getTestStep().getVariable().ifPresent(result::add);
        result.add(new BPMNTestVariable("testCaseNumber", "Integer", getNumber()));

        return result;
    }

}
