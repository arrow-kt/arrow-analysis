package arrow.meta.plugins.liquid.phases.analysis.solver.check.model

import arrow.meta.plugins.liquid.smt.ObjectFormula
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamed

/**
 * Maps return points to the SMT variables representing that place.
 */
data class ReturnPoints(
  val topMostReturnPointVariableName: Pair<String?, ObjectFormula>,
  val namedReturnPointVariableNames: Map<String, ObjectFormula>
) {

  fun addAndReplaceTopMost(newScopeName: String, newVariableName: ObjectFormula) =
    this
      .replaceTopMost(newScopeName, newVariableName)
      .add(newScopeName, newVariableName)

  private fun replaceTopMost(newScopeName: String, newVariableName: ObjectFormula) =
    ReturnPoints(Pair(newScopeName, newVariableName), namedReturnPointVariableNames)

  private fun add(returnPoint: String, variableName: ObjectFormula) =
    ReturnPoints(
      topMostReturnPointVariableName,
      namedReturnPointVariableNames + (returnPoint to variableName)
    )

  companion object {
    private fun new(scope: String?, variableName: ObjectFormula): ReturnPoints =
      when (scope) {
        null -> ReturnPoints(Pair(scope, variableName), emptyMap())
        else -> ReturnPoints(Pair(scope, variableName), mapOf(scope to variableName))
      }

    fun new(scope: KtElement, variableName: ObjectFormula): ReturnPoints =
      when (scope) {
        is KtNamed -> new(scope.nameAsName?.asString(), variableName)
        else -> new(null, variableName)
      }
  }
}