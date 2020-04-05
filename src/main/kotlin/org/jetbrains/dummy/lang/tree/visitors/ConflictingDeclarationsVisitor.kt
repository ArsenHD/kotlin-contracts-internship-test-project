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
            it.accept(this, data)
            data.clear()
        }
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, data: MutableSet<String>) {
        val parameters = functionDeclaration.parameters
        parameters.forEach {
            if (data.contains(it)) {
                val line = functionDeclaration.line
                val variableDeclaration =
                    VariableDeclaration(line, it, null)
                throw ConflictingDeclarationsException(variableDeclaration)
            }
            data.add(it)
        }
        functionDeclaration.acceptChildren(this, data)
    }

    override fun visitBlock(block: Block, data: MutableSet<String>) {
        block.acceptChildren(this, data)
    }

    override fun visitIfStatement(ifStatement: IfStatement, data: MutableSet<String>) {
        val thenBlock = ifStatement.thenBlock
        val elseBlock = ifStatement.elseBlock

        val dataCopyThen = HashSet<String>()
        val dataCopyElse = HashSet<String>()

        dataCopyThen.addAll(data)
        dataCopyElse.addAll(data)

        thenBlock.accept(this, dataCopyThen)
        elseBlock?.accept(this, dataCopyElse)
    }

    override fun visitVariableDeclaration(variableDeclaration: VariableDeclaration, data: MutableSet<String>) {
        val variable = variableDeclaration.name
        if (data.contains(variable)) {
            throw ConflictingDeclarationsException(variableDeclaration)
        }
        data.add(variable)
    }
}