package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.exceptions.ConflictingDeclarationsException
import org.jetbrains.dummy.lang.tree.visitors.ConflictingDeclarationsVisitor
import org.jetbrains.dummy.lang.tree.File
import org.jetbrains.dummy.lang.tree.VariableDeclaration

class ConflictingDeclarationsChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {
    override fun inspect(file: File) {
        try {
            val visitor = ConflictingDeclarationsVisitor()
            val set = HashSet<String>()
            file.accept(visitor, set)
        } catch (e : ConflictingDeclarationsException) {
            reportConflictingDeclarations(e.variableDeclaration)
        }
    }

    private fun reportConflictingDeclarations(variableDeclaration: VariableDeclaration) {
        reporter.report(variableDeclaration, "Variable '${variableDeclaration.name}' is already declared")
    }
}