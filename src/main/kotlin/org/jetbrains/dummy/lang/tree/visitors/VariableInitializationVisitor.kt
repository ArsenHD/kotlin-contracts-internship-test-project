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
            it.accept(this, data) // traverse each function in the file
            data.clear()                 // each time starting with an empty set
        }
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: MutableSet<String>) {
        val parameters = functionDeclaration.parameters
        parameters.forEach { data.add(it) } // arguments passed to the function are initialized variables
        functionDeclaration.acceptChildren(this, data) // traverse the function's body
    }

    override fun visitBlock(block: Block, data: MutableSet<String>) {
        block.acceptChildren(this, data) // traverse each statement in the block
    }

    override fun visitAssignment(assignment: Assignment, data: MutableSet<String>) {
        val variable = assignment.variable
        assignment.acceptChildren(this, data) // check the rhs
        data.add(variable) // add this variable to the set of initialized variables
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: MutableSet<String>) {
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock
        val condition = ifStatement.condition

        condition.accept(this, data) // check the condition expression

        val dataCopyThen = HashSet<String>()
        val dataCopyElse = HashSet<String>()
        
        dataCopyThen.addAll(data) // copy "data" set for traversing
        dataCopyElse.addAll(data) // branches of the "if" statement

        if ((condition is BooleanConst) && (condition.value)) { // if the condition is a boolean constant "true"
            thenBlock.accept(this, dataCopyThen)         // then consider only "then" block
            data.addAll(dataCopyThen)
        } else if ((condition is BooleanConst) && (!condition.value)) { // if the condition is a boolean constant "false"
            elseBlock?.accept(this, dataCopyElse)                // then consider only "else" block if there is one
            data.addAll(dataCopyElse)
        } else { // otherwise, consider both branches
            thenBlock.accept(this, dataCopyThen)
            elseBlock?.accept(this, dataCopyElse)

            dataCopyThen.removeIf { data.contains(it) } // leave only newly initialized variables
            dataCopyThen.forEach {
                if (dataCopyElse.contains(it)) { // add all of the variables that have been initialized in both branches
                    data.add(it)                 // to the initialized variables set
                }
            }
        }
    }

    override fun visitVariableDeclaration(variableDeclaration: VariableDeclaration, data: MutableSet<String>) {
        val variable = variableDeclaration.name
        val initializer = variableDeclaration.initializer

        variableDeclaration.acceptChildren(this, data) // check initializer expression

        if (initializer != null) { // if the variable declaration has an initializer
            data.add(variable)     // add this variable to the initialized variables set
        }
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement, data: MutableSet<String>) {
        returnStatement.acceptChildren(this, data) // check the return value
    }

    override fun visitVariableAccess(variableAccess: VariableAccess, data: MutableSet<String>) {
        val variable = variableAccess.name

        if (!data.contains(variable)) { // if the variable has been accessed before initialization
            throw AccessBeforeInitializationException(variableAccess) // throw an exception
        }
    }

    override fun visitFunctionCall(functionCall: FunctionCall, data: MutableSet<String>) {
        functionCall.acceptChildren(this, data) // check the function's arguments
    }
}