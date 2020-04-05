package org.jetbrains.dummy.lang.tree.visitors

import org.jetbrains.dummy.lang.exceptions.WrongArgumentsException
import org.jetbrains.dummy.lang.tree.*

class WrongArgumentsVisitor : DummyLangVisitor<Unit, MutableMap<String, Int>>() {
    override fun visitElement(element: Element, data: MutableMap<String, Int>) {
        return
    }

    override fun visitFile(file: File, data: MutableMap<String, Int>) {
        val functions = file.functions

        functions.forEach {  // put each function's parameters amount to the map
            val function = it.name
            val parametersAmount = it.parameters.size
            data[function] = parametersAmount
        }

        file.acceptChildren(this, data) // traverse each function in the file
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: MutableMap<String, Int>) {
        functionDeclaration.acceptChildren(this, data) // traverse the function's body
    }

    override fun visitBlock(block: Block, data: MutableMap<String, Int>) {
        block.acceptChildren(this, data) // traverse each statement in the block
    }

    override fun visitAssignment(assignment: Assignment, data: MutableMap<String, Int>) {
        assignment.rhs.accept(this, data) // check the rhs of the assignment
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: MutableMap<String, Int>) {
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock
        val condition = ifStatement.condition

        condition.accept(this, data)  // check the condition
        thenBlock.accept(this, data)  // and both branches
        elseBlock?.accept(this, data) // of the "if" statement
    }

    override fun visitVariableDeclaration(variableDeclaration: VariableDeclaration, data: MutableMap<String, Int>) {
        variableDeclaration.initializer?.accept(this, data) // check the initializer
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement, data: MutableMap<String, Int>) {
        returnStatement.result?.accept(this, data) // check the return value
    }

    override fun visitFunctionCall(functionCall: FunctionCall, data: MutableMap<String, Int>) {
        val function = functionCall.function
        val argumentsAmount = functionCall.arguments.size
        if (argumentsAmount != data[function]) { // if the function is called with the wrong amount of arguments
            throw WrongArgumentsException(functionCall) // throw an exception
        }
    }
}