package org.jetbrains.dummy.lang.tree.visitors

import org.jetbrains.dummy.lang.exceptions.UnreachableCodeException
import org.jetbrains.dummy.lang.tree.*

class UnreachableCodeVisitor : DummyLangVisitor<Boolean, Boolean>() {
    override fun visitElement(element: Element, data: Boolean): Boolean {
        return data
    }

    override fun visitFile(file: File, data: Boolean): Boolean {
        val functions = file.functions
        var isReturnStatementReached = false
        functions.forEach {
            it.accept(this, isReturnStatementReached) // traverse each function in the file
            isReturnStatementReached = false // starting each function's traversal with false value
        }
        return visitElement(file, data)
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: Boolean): Boolean {
        functionDeclaration.acceptChildren(this, data) // traverse the function's body
        return visitElement(functionDeclaration, data)
    }

    override fun visitBlock(block: Block, data: Boolean): Boolean {
        val statements = block.statements
        var isReturnStatementReached = false
        statements.forEach {  // traverse each statement in the block passing the flag signalizing whether
            isReturnStatementReached = it.accept(this, isReturnStatementReached) // the return statement has been reached
        }
        return visitElement(block, isReturnStatementReached)
    }

    override fun visitStatement(statement: Statement, data: Boolean): Boolean {
        if (data) { // if the return statement has been reached then this statement is unreachable
            throw UnreachableCodeException(statement) // throw an exception
        }
        return visitElement(statement, data)
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: Boolean): Boolean {
        if (data) { // if the return statement has been reached then this statement is unreachable
            throw UnreachableCodeException(ifStatement) // throw an exception
        }
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock
        val condition = ifStatement.condition

        if ((condition is BooleanConst) && (condition.value)) { // if condition is "true"
            return thenBlock.accept(this, data)          // then consider only "then" branch
        }

        if ((condition is BooleanConst) && (!condition.value)) { // if condition is "false"
            return elseBlock?.accept(this, data) ?: false // then consider only "else" branch
        }

        val thenReturned = thenBlock.accept(this, data)           // if condition is not "true" of "false" boolean constants
        val elseReturned = elseBlock?.accept(this, data) ?: false // then consider both branches
        return thenReturned && elseReturned // return true only if both branches have reached the return statement
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement, data: Boolean): Boolean {
        visitStatement(returnStatement, data) // check if the return statement has already been reached
        return true // signal that the return statement has been reached
    }
}