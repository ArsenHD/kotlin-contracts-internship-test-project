package org.jetbrains.dummy.lang.tree.visitors

import org.jetbrains.dummy.lang.exceptions.EmptyBodyException
import org.jetbrains.dummy.lang.tree.*

class EmptyBodyVisitor : DummyLangVisitor<Unit, Any?>() {
    override fun visitElement(element: Element, data: Any?) {
        return
    }

    override fun visitFile(file: File, data: Any?) {
        val functions = file.functions
        functions.forEach {
            it.accept(this, null) // traverse each function in the file
        }
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: Any?) {
        functionDeclaration.acceptChildren(this, data) // traverse the function's body
    }

    override fun visitBlock(block: Block, data: Any?) {
        val statementsAmount = block.statements.size
        if (statementsAmount == 0) { // throw an exception if the block is empty
            throw EmptyBodyException(block)
        }
        block.acceptChildren(this, data) // traverse the statements of this block
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: Any?) {
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock

        thenBlock.accept(this, data) // traverse branches of
        elseBlock?.accept(this, data) // the "if" statement
    }
}