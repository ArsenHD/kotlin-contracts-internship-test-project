package org.jetbrains.dummy.lang.tree.visitors

import org.jetbrains.dummy.lang.exceptions.AccessBeforeInitializationException
import org.jetbrains.dummy.lang.tree.*

class VariableInitializationVisitor : DummyLangVisitor<Unit, MutableSet<String>>() {
    override fun visitElement(element: Element, data: MutableSet<String>) {
        return
    }
    
    override fun visitFile(file: File, data: MutableSet<String>) {
        val functions = file.functions
        functions.forEach {
            it.accept(this, data)
            data.clear()
        }
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: MutableSet<String>) {
        val parameters = functionDeclaration.parameters
        parameters.forEach { data.add(it) }
        functionDeclaration.acceptChildren(this, data)
    }

    override fun visitBlock(block: Block, data: MutableSet<String>) {
        block.acceptChildren(this, data)
    }

    override fun visitAssignment(assignment: Assignment, data: MutableSet<String>) {
        val variable = assignment.variable
        assignment.acceptChildren(this, data)
        data.add(variable)
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: MutableSet<String>) {
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock
        val condition = ifStatement.condition

        condition.accept(this, data)

        val dataCopyThen = HashSet<String>()
        val dataCopyElse = HashSet<String>()
        
        dataCopyThen.addAll(data)
        dataCopyElse.addAll(data)

        if ((condition is BooleanConst) && (condition.value)) {
            thenBlock.accept(this, dataCopyThen)
            data.addAll(dataCopyThen)
        } else if ((condition is BooleanConst) && (!condition.value)) {
            elseBlock?.accept(this, dataCopyElse)
            data.addAll(dataCopyElse)
        } else {
            thenBlock.accept(this, dataCopyThen)
            elseBlock?.accept(this, dataCopyElse)

            dataCopyThen.removeIf { data.contains(it) }
            dataCopyThen.forEach {
                if (dataCopyElse.contains(it)) {
                    data.add(it)
                }
            }
        }
    }

    override fun visitVariableDeclaration(variableDeclaration: VariableDeclaration, data: MutableSet<String>) {
        val variable = variableDeclaration.name
        val initializer = variableDeclaration.initializer

        variableDeclaration.acceptChildren(this, data)

        if (initializer != null) {
            data.add(variable)
        }
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement, data: MutableSet<String>) {
        returnStatement.acceptChildren(this, data)
    }

    override fun visitVariableAccess(variableAccess: VariableAccess, data: MutableSet<String>) {
        val variable = variableAccess.name

        if (!data.contains(variable)) {
            throw AccessBeforeInitializationException(variableAccess)
        }
    }

    override fun visitFunctionCall(functionCall: FunctionCall, data: MutableSet<String>) {
        functionCall.acceptChildren(this, data)
    }
}