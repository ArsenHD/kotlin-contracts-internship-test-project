package org.jetbrains.dummy.lang

import org.jetbrains.dummy.lang.checkers.*
import java.io.OutputStream

class DummyLanguageAnalyzer(outputStream: OutputStream) {
    companion object {
        private val CHECKERS: List<(DiagnosticReporter) -> AbstractChecker> = listOf(
            ::VariableInitializationChecker, ::UnreachableCodeChecker, ::ConflictingDeclarationsChecker,
            ::WrongArgumentsChecker, ::EmptyBodyChecker
        )
    }

    private val reporter: DiagnosticReporter = DiagnosticReporter(outputStream)

    fun analyze(path: String) {
        val transformer = DummySourceToTreeTransformer()
        val file = transformer.transform(path)

        val checkers = CHECKERS.map { it(reporter) }
        for (checker in checkers) {
            checker.inspect(file)
        }
    }
}

