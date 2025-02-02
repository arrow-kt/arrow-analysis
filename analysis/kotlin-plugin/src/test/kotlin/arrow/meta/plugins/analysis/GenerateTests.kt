package arrow.meta.plugins.analysis

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import arrow.meta.plugins.analysis.runners.AbstractBoxTest
import arrow.meta.plugins.analysis.runners.AbstractDiagnosticTest

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "src/testData", testsRoot = "src/test-gen") {
            testClass<AbstractDiagnosticTest> {
                model("diagnostics")
            }

            testClass<AbstractBoxTest> {
                model("box")
            }
        }
    }
}
