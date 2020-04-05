package org.jetbrains.dummy.lang.checkers

import org.jetbrains.dummy.lang.AbstractChecker
import org.jetbrains.dummy.lang.DiagnosticReporter
import org.jetbrains.dummy.lang.exceptions.EmptyBodyException
import org.jetbrains.dummy.lang.tree.*
import org.jetbrains.dummy.lang.tree.visitors.EmptyBodyVisitor

class EmptyBodyChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {
    override fun inspect(file: File) {
        try {
            val visitor = EmptyBodyVisitor()
            file.accept(visitor, null)
        } catch (e : EmptyBodyException) {
            reportEmptyBody(e.block)
        }
    }

    private fun reportEmptyBody(block: Block) {
        reporter.report(block, "Empty block")
    }
}