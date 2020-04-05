package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.exceptions.UnreachableCodeException
import org.jetbrains.dummy.lang.tree.Element
import org.jetbrains.dummy.lang.tree.File
import org.jetbrains.dummy.lang.tree.visitors.UnreachableCodeVisitor

class UnreachableCodeChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {
    override fun inspect(file: File) {
        try {
            val visitor = UnreachableCodeVisitor()
            file.accept(visitor, false)
        } catch (e : UnreachableCodeException) {
            reportUnreachableCode(e.element)
        }
    }

    private fun reportUnreachableCode(element: Element) {
        reporter.report(element, "Unreachable code")
    }
}