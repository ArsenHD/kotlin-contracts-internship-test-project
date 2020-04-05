package org.jetbrains.dummy.lang.tree.visitors

import org.jetbrains.dummy.lang.exceptions.UnreachableCodeException
import org.jetbrains.dummy.lang.tree.*

class UnreachableCodeVisitor : DummyLangVisitor<Boolean, Boolean>() {
    override fun visitElement(element: Element, data: Boolean): Boolean {
        return data
    }

    override fun visitFile(file: File, data: Boolean): Boolean {
        file.acceptChildren(this, data)
        val functions = file.functions
        var isReturnStatementReached = data
        functions.forEach {
            it.accept(this, isReturnStatementReached)
            isReturnStatementReached = false
        }
        return visitElement(file, data)
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: Boolean): Boolean {
        functionDeclaration.acceptChildren(this, data)
        return visitElement(functionDeclaration, data)
    }

    override fun visitBlock(block: Block, data: Boolean): Boolean {
        val statements = block.statements
        var isReturnStatementReached = false
        statements.forEach {
            isReturnStatementReached = it.accept(this, isReturnStatementReached)
        }
        return visitElement(block, isReturnStatementReached)
    }

    override fun visitStatement(statement: Statement, data: Boolean): Boolean {
        if (data) {
            throw UnreachableCodeException(statement)
        }
        return visitElement(statement, data)
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: Boolean): Boolean {
        if (data) {
            throw UnreachableCodeException(ifStatement)
        }
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock

        val thenReturned = thenBlock.accept(this, data)
        val elseReturned = elseBlock?.accept(this, data) ?: false

        return thenReturned && elseReturned
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement, data: Boolean): Boolean {
        visitStatement(returnStatement, data)
        return true
    }

    override fun visitFunctionCall(functionCall: FunctionCall, data: Boolean): Boolean {
        return visitExpression(functionCall, data)
    }
}