package org.jetbrains.dummy.lang.tree.visitors

import org.jetbrains.dummy.lang.exceptions.ConflictingDeclarationsException
import org.jetbrains.dummy.lang.tree.*

class ConflictingDeclarationsVisitor : DummyLangVisitor<Unit, MutableSet<String>>() {
    override fun visitElement(element: Element, data: MutableSet<String>) {
        return
    }

    override fun visitFile(file: File, data: MutableSet<String>) {
        val functions = file.functions
        functions.forEach {
            it.accept(this, data) // visit each function in the file
            data.clear()                 // each time starting with an empty set
        }
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: MutableSet<String>) {
        val parameters = functionDeclaration.parameters
        parameters.forEach {
            if (data.contains(it)) { // if a parameter with the same name has already been declared
                val line = functionDeclaration.line // throw an exception
                val variableDeclaration =
                    VariableDeclaration(line, it, null)
                throw ConflictingDeclarationsException(variableDeclaration)
            }
            data.add(it) // add the parameter to the set of declared variables
        }
        functionDeclaration.acceptChildren(this, data) // traverse function's body
    }

    override fun visitBlock(block: Block, data: MutableSet<String>) {
        block.acceptChildren(this, data) // traverse each statement in the block
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: MutableSet<String>) {
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock

        val dataCopyThen = HashSet<String>()
        val dataCopyElse = HashSet<String>()

        dataCopyThen.addAll(data) // create copies of the current set
        dataCopyElse.addAll(data) // for traversal of each branch of an "if" statement
                                  // since variables declared inside branches don't affect outer scope

        thenBlock.accept(this, dataCopyThen) // traverse branches
        elseBlock?.accept(this, dataCopyElse)
    }

    override fun visitVariableDeclaration(variableDeclaration: VariableDeclaration, data: MutableSet<String>) {
        val variable = variableDeclaration.name
        if (data.contains(variable)) { // throw an exception if the variable has already been declared
            throw ConflictingDeclarationsException(variableDeclaration)
        }
        data.add(variable) // add this variable to the set of declared variables
    }
}