package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.exceptions.WrongArgumentsException
import org.jetbrains.dummy.lang.tree.*
import org.jetbrains.dummy.lang.tree.visitors.WrongArgumentsVisitor

class WrongArgumentsChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {
    override fun inspect(file: File) {
        try {
            val visitor = WrongArgumentsVisitor()
            val map = HashMap<String, Int>()
            file.accept(visitor, map)
        } catch (e : WrongArgumentsException) {
            reportWrongArguments(e.functionCall)
        }
    }

    private fun reportWrongArguments(functionCall : FunctionCall) {
        reporter.report(functionCall, "Wrong arguments at '${functionCall.function}' function call")
    }
}