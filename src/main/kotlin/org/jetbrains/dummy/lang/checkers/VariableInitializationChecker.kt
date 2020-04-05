package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.exceptions.AccessBeforeInitializationException
import org.jetbrains.dummy.lang.tree.File
import org.jetbrains.dummy.lang.tree.VariableAccess
import org.jetbrains.dummy.lang.tree.visitors.VariableInitializationVisitor

class VariableInitializationChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {
    override fun inspect(file: File) {
        try {
            val visitor = VariableInitializationVisitor()
            val set = HashSet<String>()
            file.accept(visitor, set)
        } catch (e : AccessBeforeInitializationException) {
            reportAccessBeforeInitialization(e.access)
        }
    }

    private fun reportAccessBeforeInitialization(access: VariableAccess) {
        reporter.report(access, "Variable '${access.name}' is accessed before initialization")
    }
}