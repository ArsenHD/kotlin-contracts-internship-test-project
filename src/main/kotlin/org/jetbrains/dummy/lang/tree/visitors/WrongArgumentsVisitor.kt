package org.jetbrains.dummy.lang.tree.visitors

import org.jetbrains.dummy.lang.exceptions.WrongArgumentsException
import org.jetbrains.dummy.lang.tree.*

class WrongArgumentsVisitor : DummyLangVisitor<Unit, MutableMap<String, Int>>() {
    override fun visitElement(element: Element, data: MutableMap<String, Int>) {
        return
    }

    override fun visitFile(file: File, data: MutableMap<String, Int>) {
        val functions = file.functions

        functions.forEach {
            val function = it.name
            val parametersAmount = it.parameters.size
            data[function] = parametersAmount
        }

        file.acceptChildren(this, data)
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: MutableMap<String, Int>) {
        functionDeclaration.acceptChildren(this, data)
    }

    override fun visitBlock(block: Block, data: MutableMap<String, Int>) {
        block.acceptChildren(this, data)
    }

    override fun visitAssignment(assignment: Assignment, data: MutableMap<String, Int>) {
        assignment.rhs.accept(this, data)
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: MutableMap<String, Int>) {
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock
        val condition = ifStatement.condition

        condition.accept(this, data)
        thenBlock.accept(this, data)
        elseBlock?.accept(this, data)
    }

    override fun visitVariableDeclaration(variableDeclaration: VariableDeclaration, data: MutableMap<String, Int>) {
        variableDeclaration.initializer?.accept(this, data)
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement, data: MutableMap<String, Int>) {
        returnStatement.result?.accept(this, data)
    }

    override fun visitFunctionCall(functionCall: FunctionCall, data: MutableMap<String, Int>) {
        val function = functionCall.function
        val argumentsAmount = functionCall.arguments.size
        if (argumentsAmount != data[function]) {
            throw WrongArgumentsException(functionCall)
        }
    }
}