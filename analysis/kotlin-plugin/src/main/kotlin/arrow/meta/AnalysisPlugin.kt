package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.analysis.phases.analysisPhases
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.LanguageVersionSettingsCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.type.TypeCheckers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class AnalysisPlugin : Meta() {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf("Arrow Analysis" { meta(analysisPhases()) })
}

class AnalysisMetaCliProcessor : MetaCliProcessor("analysis")

class FirAnalysisPluginRegistrar : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::AnalysisChecker
  }
}

class AnalysisChecker(session: FirSession) : FirAdditionalCheckersExtension(session) {
  override val declarationCheckers: DeclarationCheckers
    get() = super.declarationCheckers
  override val expressionCheckers: ExpressionCheckers
    get() = super.expressionCheckers
  override val typeCheckers: TypeCheckers
    get() = super.typeCheckers
  override val languageVersionSettingsCheckers: LanguageVersionSettingsCheckers
    get() = super.languageVersionSettingsCheckers
}
