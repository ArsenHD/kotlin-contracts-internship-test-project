package org.jetbrains.dummy.lang.tree.visitors

import org.jetbrains.dummy.lang.exceptions.EmptyBodyException
import org.jetbrains.dummy.lang.tree.*

class EmptyBodyVisitor : DummyLangVisitor<Unit, Any?>() {
    override fun visitElement(element: Element, data: Any?) {
        return
    }

    override fun visitFile(file: File, data: Any?) {
        val functions = file.functions
        var isReturnStatementReached = data
        functions.forEach {
            it.accept(this, isReturnStatementReached)
            isReturnStatementReached = false
        }
        return visitElement(file, data)
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: Any?) {
        functionDeclaration.acceptChildren(this, data)
    }

    override fun visitBlock(block: Block, data: Any?) {
        val statementsAmount = block.statements.size
        if (statementsAmount == 0) {
            throw EmptyBodyException(block)
        }
        block.acceptChildren(this, data)
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: Any?) {
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock

        thenBlock.accept(this, data)
        elseBlock?.accept(this, data)
    }
}